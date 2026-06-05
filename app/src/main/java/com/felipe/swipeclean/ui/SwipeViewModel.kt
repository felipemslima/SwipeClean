package com.felipe.swipeclean.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.felipe.swipeclean.data.Album
import com.felipe.swipeclean.data.KeepStore
import com.felipe.swipeclean.data.MediaRepository
import com.felipe.swipeclean.data.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Decision { KEEP, DELETE }

/**
 * Estado central do app: biblioteca de fotos, sessão de swipe e decisões.
 * Nada é apagado até o usuário confirmar no lote (ReviewScreen) — é o que
 * dá o "undo": enquanto não confirma, qualquer decisão pode ser desfeita.
 */
class SwipeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MediaRepository(app)
    private val keepStore = KeepStore(app)

    /** Chaves das fotos já decididas como MANTER (persistido entre sessões). */
    private val keptKeys: MutableSet<String> = keepStore.loadKept()

    var loading by mutableStateOf(false)
        private set
    var albums by mutableStateOf<List<Album>>(emptyList())
        private set

    private var allPhotos: List<Photo> = emptyList()

    // --- Sessão de swipe ---
    var sessionPhotos by mutableStateOf<List<Photo>>(emptyList())
        private set
    var index by mutableStateOf(0)
        private set

    /** Decisão por foto (id -> KEEP/DELETE). */
    val decisions = mutableStateMapOf<Long, Decision>()

    /** Histórico de ids decididos, na ordem — usado pelo undo. */
    private val history = ArrayDeque<Long>()

    val current: Photo? get() = sessionPhotos.getOrNull(index)
    val finished: Boolean get() = sessionPhotos.isNotEmpty() && index >= sessionPhotos.size
    val canUndo: Boolean get() = history.isNotEmpty()

    val pendingDelete: List<Photo>
        get() = sessionPhotos.filter { decisions[it.id] == Decision.DELETE }
    val pendingBytes: Long
        get() = pendingDelete.sumOf { it.sizeBytes }

    fun loadLibrary() {
        loading = true
        viewModelScope.launch {
            val photos = withContext(Dispatchers.IO) { repo.loadAllPhotos() }
            val grouped = withContext(Dispatchers.IO) { repo.groupIntoAlbums(photos) }
            allPhotos = photos
            albums = grouped
            loading = false
        }
    }

    fun startAlbum(bucketId: String) {
        // Pula as fotos já mantidas em sessões anteriores.
        sessionPhotos = allPhotos.filter { it.bucketId == bucketId && it.key !in keptKeys }
        resetSession()
    }

    fun startAll() {
        sessionPhotos = allPhotos.filter { it.key !in keptKeys }
        resetSession()
    }

    /** Quantas fotos do álbum ainda faltam revisar (descontando as já mantidas). */
    fun pendingCount(bucketId: String): Int =
        allPhotos.count { it.bucketId == bucketId && it.key !in keptKeys }

    /** Esquece todas as decisões "manter" — faz tudo voltar a ser revisável. */
    fun clearKept() {
        keptKeys.clear()
        keepStore.clear()
    }

    private fun resetSession() {
        index = 0
        decisions.clear()
        history.clear()
    }

    fun decide(decision: Decision) {
        val photo = current ?: return
        decisions[photo.id] = decision
        history.addLast(photo.id)
        if (decision == Decision.KEEP) {
            keptKeys.add(photo.key)
            keepStore.save(keptKeys)   // persiste já — sobrevive a fechar o app
        }
        index++
    }

    fun undo() {
        if (history.isEmpty()) return
        val lastId = history.removeLast()
        decisions.remove(lastId)
        index = (index - 1).coerceAtLeast(0)
        // Se a decisão desfeita era MANTER, remove também da memória persistida.
        val photo = sessionPhotos.getOrNull(index)
        if (photo != null && photo.id == lastId) {
            if (keptKeys.remove(photo.key)) keepStore.save(keptKeys)
        }
    }

    /** Chamado após o sistema confirmar a exclusão em lote. */
    fun onDeleted() {
        val ids = pendingDelete.map { it.id }.toSet()
        allPhotos = allPhotos.filterNot { it.id in ids }
        sessionPhotos = sessionPhotos.filterNot { it.id in ids }
        ids.forEach { decisions.remove(it) }
        albums = repo.groupIntoAlbums(allPhotos)
        // Reindexa para não passar do fim após remover itens já percorridos.
        index = index.coerceAtMost(sessionPhotos.size)
    }
}
