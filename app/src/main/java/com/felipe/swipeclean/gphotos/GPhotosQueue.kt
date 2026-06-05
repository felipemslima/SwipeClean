package com.felipe.swipeclean.gphotos

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Estado compartilhado entre a UI (GPhotosScreen) e o AccessibilityService
 * (GPhotosDeleteService) — ambos no mesmo processo do app.
 *
 * A UI define a fila e abre a 1ª foto; o serviço dirige o Google Fotos
 * (apagar + confirmar) e avança a fila.
 */
object GPhotosQueue {

    enum class Phase { Idle, AbrindoFoto, ProcurandoApagar, ProcurandoConfirmar, Concluido, Parado }

    var uris by mutableStateOf<List<Uri>>(emptyList())
        private set
    var index by mutableIntStateOf(0)
        private set
    var active by mutableStateOf(false)
        private set
    var phase by mutableStateOf(Phase.Idle)
        private set
    var log by mutableStateOf("")
        private set

    val total: Int get() = uris.size
    val currentUri: Uri? get() = uris.getOrNull(index)

    fun start(list: List<Uri>) {
        uris = list
        index = 0
        active = list.isNotEmpty()
        phase = if (active) Phase.AbrindoFoto else Phase.Concluido
        log = "Iniciando…"
    }

    fun stop() {
        active = false
        phase = Phase.Parado
        log = "Parado pelo usuário."
    }

    /** O serviço chama quando o Google Fotos já está mostrando a foto. */
    fun onPhotoShown() {
        if (active && phase == Phase.AbrindoFoto) phase = Phase.ProcurandoApagar
    }

    fun changePhase(p: Phase) { phase = p }
    fun note(msg: String) { log = msg }

    /** Avança para a próxima foto; retorna a próxima Uri ou null se acabou. */
    fun advance(): Uri? {
        index += 1
        return if (index >= uris.size) {
            active = false
            phase = Phase.Concluido
            log = "Concluído: $total fotos processadas."
            null
        } else {
            phase = Phase.AbrindoFoto
            currentUri
        }
    }
}
