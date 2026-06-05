package com.felipe.swipeclean.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────
// Tipografia · Space Grotesk
//
// OPÇÃO A (recomendada, sem baixar .ttf) — Downloadable Google Fonts.
//   1) build.gradle (app):  implementation("androidx.compose.ui:ui-text-google-fonts:1.6.8")
//   2) Crie res/values/font_certs.xml com o array de certificados do GMS
//      (o snippet está no README) e descomente o bloco abaixo.
//
// OPÇÃO B (offline) — baixe os .ttf em https://fonts.google.com/specimen/Space+Grotesk,
//   jogue em res/font/ (space_grotesk_regular.ttf, _medium, _semibold, _bold) e troque
//   ScFontFamily pelo FontFamily(Font(R.font.space_grotesk_regular), ...).
//
// Enquanto não configurar, fica FontFamily.Default (o app compila normalmente).
// ─────────────────────────────────────────────────────────────────────

/*
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.font.Font
import com.felipe.swipeclean.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)
private val spaceGrotesk = GoogleFont("Space Grotesk")
val ScFontFamily = FontFamily(
    Font(spaceGrotesk, provider, FontWeight.Normal),
    Font(spaceGrotesk, provider, FontWeight.Medium),
    Font(spaceGrotesk, provider, FontWeight.SemiBold),
    Font(spaceGrotesk, provider, FontWeight.Bold),
)
*/

// Fallback até as fontes serem configuradas (ver acima):
val ScFontFamily: FontFamily = FontFamily.Default

private fun base(
    weight: FontWeight,
    size: Int,
    line: Int,
    spacing: Double = 0.0,
) = TextStyle(
    fontFamily = ScFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = spacing.sp,
)

// Escala enxuta, com tracking levemente negativo nos títulos (cara do Space Grotesk).
val ScTypography = Typography(
    displaySmall   = base(FontWeight.Bold, 30, 36, -0.5),
    headlineMedium = base(FontWeight.Bold, 26, 32, -0.5),
    headlineSmall  = base(FontWeight.Bold, 22, 28, -0.4),
    titleLarge     = base(FontWeight.SemiBold, 19, 24, -0.2),
    titleMedium    = base(FontWeight.SemiBold, 16, 22),
    bodyLarge      = base(FontWeight.Normal, 15, 22),
    bodyMedium     = base(FontWeight.Normal, 14, 20),
    bodySmall      = base(FontWeight.Normal, 12, 16),
    labelLarge     = base(FontWeight.Bold, 15, 20),
    labelMedium    = base(FontWeight.SemiBold, 13, 16),
    labelSmall     = base(FontWeight.SemiBold, 11, 14, 0.2),
)
