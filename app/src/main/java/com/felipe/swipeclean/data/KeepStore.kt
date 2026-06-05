package com.felipe.swipeclean.data

import android.content.Context

/**
 * Persiste as fotos que o usuário decidiu MANTER, para não revê-las em sessões
 * futuras (importante p/ bibliotecas grandes).
 *
 * Guarda as chaves (Photo.key = "volume:id") em SharedPreferences.
 * Decisões de APAGAR não são guardadas: ou a foto é deletada (some), ou volta
 * a aparecer na próxima sessão.
 */
class KeepStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun loadKept(): MutableSet<String> =
        HashSet(prefs.getStringSet(KEY_KEPT, emptySet()) ?: emptySet())

    fun save(kept: Set<String>) {
        // Sempre grava um Set novo — editar o retornado por getStringSet é UB.
        prefs.edit().putStringSet(KEY_KEPT, HashSet(kept)).apply()
    }

    fun clear() {
        prefs.edit().remove(KEY_KEPT).apply()
    }

    companion object {
        private const val PREFS = "swipeclean_keep"
        private const val KEY_KEPT = "kept_keys"
    }
}
