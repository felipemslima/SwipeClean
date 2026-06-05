package com.felipe.swipeclean.gphotos

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils

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

/** True se o nosso AccessibilityService está habilitado nas Configurações. */
fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
    val expected = ComponentName(context, service).flattenToString()
    val enabled = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
    ) ?: return false
    val splitter = TextUtils.SimpleStringSplitter(':')
    splitter.setString(enabled)
    for (item in splitter) {
        if (item.equals(expected, ignoreCase = true)) return true
    }
    return false
}

/** Abre a tela de Acessibilidade do sistema para o usuário ligar o serviço. */
fun openAccessibilitySettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}
