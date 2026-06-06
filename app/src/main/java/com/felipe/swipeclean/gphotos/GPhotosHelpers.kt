package com.felipe.swipeclean.gphotos

import android.content.Context
import android.content.Intent
import android.net.Uri

const val GPHOTOS_PKG = "com.google.android.apps.photos"

/** Intent que abre uma foto (content uri) direto no visualizador do Google Fotos. */
fun gphotosViewIntent(uri: Uri): Intent =
    Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        setPackage(GPHOTOS_PKG)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

/** True se o app do Google Fotos está instalado. */
fun isGooglePhotosInstalled(context: Context): Boolean =
    try {
        context.packageManager.getPackageInfo(GPHOTOS_PKG, 0); true
    } catch (e: Exception) {
        false
    }

/** Abre o app do Google Fotos (tela inicial), para seleção/lixeira em lote. */
fun openGooglePhotosApp(context: Context) {
    val launch = context.packageManager.getLaunchIntentForPackage(GPHOTOS_PKG)
        ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (launch != null) context.startActivity(launch)
}
