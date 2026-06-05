package com.felipe.swipeclean.util

import java.util.Locale

/** Formata bytes em B/KB/MB/GB legível. */
fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble() / 1024
    var i = 0
    while (value >= 1024 && i < units.size - 1) {
        value /= 1024
        i++
    }
    return String.format(Locale.US, "%.1f %s", value, units[i])
}
