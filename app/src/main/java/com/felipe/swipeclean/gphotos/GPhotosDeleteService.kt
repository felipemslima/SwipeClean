package com.felipe.swipeclean.gphotos

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Locale

/**
 * Automatiza a exclusão de fotos no app do Google Fotos.
 *
 * Fluxo por foto: a UI/serviço abre a foto no Google Fotos -> o serviço acha o
 * botão "apagar/lixeira" (direto ou via "mais opções"), clica, acha o botão de
 * confirmação no diálogo, clica, e abre a próxima foto. No fim, volta ao app.
 *
 * EXPERIMENTAL / uso pessoal. Depende dos textos da UI do Google Fotos — se o
 * Google mudar a tela ou o idioma for outro, ajuste as listas de palavras abaixo
 * (veja os logs no logcat com a tag "GPhotosDelete").
 */
class GPhotosDeleteService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var retries = 0

    companion object {
        private const val MAX_RETRIES = 12        // ~ MAX_RETRIES * STEP_MS de espera por etapa
        private const val STEP_MS = 500L

        // Palavras (PT-BR + EN) procuradas em texto OU contentDescription.
        private val DELETE_WORDS = listOf(
            "mover para a lixeira", "mover para lixeira", "lixeira", "excluir", "apagar",
            "move to trash", "move to bin", "trash", "delete", "bin", "remove",
        )
        private val MORE_WORDS = listOf(
            "mais opções", "mais opcoes", "more options", "more", "opções", "options", "menu",
        )
        private val CONFIRM_WORDS = listOf(
            "mover para a lixeira", "mover para lixeira", "excluir", "apagar", "permitir", "remover",
            "move to trash", "move to bin", "delete", "allow", "remove", "ok",
        )
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        GPhotosQueue.setLog("Serviço de acessibilidade conectado.")
    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!GPhotosQueue.active) return
        if (event?.packageName != GPHOTOS_PKG) return

        // Assim que o Google Fotos aparece com a foto, passa a procurar o "apagar".
        GPhotosQueue.onPhotoShown()

        // Debounce: espera a tela assentar antes de agir.
        handler.removeCallbacks(step)
        handler.postDelayed(step, STEP_MS)
    }

    private val step = Runnable { attempt() }

    private fun attempt() {
        if (!GPhotosQueue.active) return
        val root = rootInActiveWindow ?: return scheduleRetry()

        when (GPhotosQueue.phase) {
            GPhotosQueue.Phase.ProcurandoApagar -> {
                val del = findClickable(root, DELETE_WORDS)
                if (del != null) {
                    log("apagar -> clicando")
                    clickNode(del)
                    GPhotosQueue.setPhase(GPhotosQueue.Phase.ProcurandoConfirmar)
                    retries = 0
                    handler.postDelayed(step, STEP_MS)
                    return
                }
                // talvez esteja atrás de "mais opções"
                val more = findClickable(root, MORE_WORDS)
                if (more != null) {
                    log("abrindo mais opções")
                    clickNode(more)
                    handler.postDelayed(step, STEP_MS)
                    return
                }
                scheduleRetry()
            }

            GPhotosQueue.Phase.ProcurandoConfirmar -> {
                val ok = findClickable(root, CONFIRM_WORDS)
                if (ok != null) {
                    log("confirmar -> clicando")
                    clickNode(ok)
                    retries = 0
                    handler.postDelayed({ goNext() }, STEP_MS)
                    return
                }
                scheduleRetry()
            }

            else -> { /* AbrindoFoto / Concluido / Parado: nada a fazer */ }
        }
    }

    private fun scheduleRetry() {
        retries += 1
        if (retries <= MAX_RETRIES) {
            handler.postDelayed(step, STEP_MS)
        } else {
            log("não encontrei o botão — pulando esta foto")
            retries = 0
            goNext()
        }
    }

    private fun goNext() {
        val next = GPhotosQueue.advance()
        if (next != null && GPhotosQueue.active) {
            startActivity(gphotosViewIntent(next))
        } else {
            finishAndReturn()
        }
    }

    private fun finishAndReturn() {
        log("fim — voltando ao SwipeClean")
        val launch = packageManager.getLaunchIntentForPackage(packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (launch != null) startActivity(launch)
    }

    // ── busca na árvore de acessibilidade ─────────────────────────────────

    /** Acha um nó clicável cujo texto/descrição contém alguma das palavras. */
    private fun findClickable(root: AccessibilityNodeInfo, words: List<String>): AccessibilityNodeInfo? {
        val match = findMatch(root, words) ?: return null
        return nearestClickable(match)
    }

    private fun findMatch(node: AccessibilityNodeInfo?, words: List<String>): AccessibilityNodeInfo? {
        if (node == null) return null
        val text = (node.text?.toString() ?: "").lowercase(Locale.ROOT)
        val desc = (node.contentDescription?.toString() ?: "").lowercase(Locale.ROOT)
        if (text.isNotBlank() || desc.isNotBlank()) {
            for (w in words) {
                if (text.contains(w) || desc.contains(w)) return node
            }
        }
        for (i in 0 until node.childCount) {
            val found = findMatch(node.getChild(i), words)
            if (found != null) return found
        }
        return null
    }

    private fun nearestClickable(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var n: AccessibilityNodeInfo? = node
        var hops = 0
        while (n != null && hops < 6) {
            if (n.isClickable) return n
            n = n.parent
            hops += 1
        }
        return node // tenta no próprio nó mesmo que não marcado clicável
    }

    private fun clickNode(node: AccessibilityNodeInfo) {
        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    private fun log(msg: String) {
        android.util.Log.d("GPhotosDelete", msg)
        GPhotosQueue.setLog(msg)
    }
}
