package com.felipe.swipeclean.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────
// Tema "Sereno+" · DARK FORÇADO.
//
// O Material 3 só carrega 1 "primary"/"error"/"tertiary". Como precisamos de
// alguns tokens a mais (versões soft, linhas, dim/faint, card/elev), eles vão
// num objeto extra exposto via CompositionLocal -> LocalSc.current.
// ─────────────────────────────────────────────────────────────────────

@Immutable
data class ScColors(
    val keep: Color, val keepOn: Color, val keepSoft: Color,
    val delete: Color, val deleteOn: Color, val deleteSoft: Color,
    val accent: Color, val accentOn: Color, val accentSoft: Color,
    val card: Color, val elev: Color,
    val line: Color, val line2: Color,
    val dim: Color, val faint: Color,
)

private val ScColorsDark = ScColors(
    keep = Keep, keepOn = KeepOn, keepSoft = Keep.copy(alpha = 0.16f),
    delete = Delete, deleteOn = DeleteOn, deleteSoft = Delete.copy(alpha = 0.16f),
    accent = Accent, accentOn = AccentOn, accentSoft = Accent.copy(alpha = 0.18f),
    card = ScCard, elev = ScElev,
    line = ScLine, line2 = ScLine2,
    dim = ScDim, faint = ScFaint,
)

val LocalSc = staticCompositionLocalOf { ScColorsDark }

private val DarkScheme = darkColorScheme(
    primary = Keep, onPrimary = KeepOn,
    secondary = Accent, onSecondary = AccentOn,
    tertiary = Accent, onTertiary = AccentOn,
    error = Delete, onError = DeleteOn,
    background = ScBg, onBackground = ScText,
    surface = ScSurface, onSurface = ScText,
    surfaceVariant = ScCard, onSurfaceVariant = ScDim,
    outline = ScLine2, outlineVariant = ScLine,
)

// Cantos generosos (Sereno é arredondado): chips/pills = full, cards = 30dp, tiles = 22dp.
val ScShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(30.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun SwipeCleanTheme(
    // dark forçado — o parâmetro fica só por compatibilidade com chamadas antigas.
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSc provides ScColorsDark) {
        MaterialTheme(
            colorScheme = DarkScheme,
            typography = ScTypography,
            shapes = ScShapes,
            content = content,
        )
    }
}
