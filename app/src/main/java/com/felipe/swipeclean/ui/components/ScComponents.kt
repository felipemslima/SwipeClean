package com.felipe.swipeclean.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.swipeclean.ui.theme.LocalSc
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────────────
// Marca: logo "spark" (do Pop) — quadrado arredondado com gradiente âmbar→coral,
// duas cartas brancas inclinadas (a metáfora do swipe) e um brilho no canto.
// ─────────────────────────────────────────────────────────────────────
@Composable
fun LogoMark(size: Int = 44, modifier: Modifier = Modifier) {
    val sc = LocalSc.current
    val s = size.dp
    Box(
        modifier = modifier
            .size(s)
            .clip(RoundedCornerShape((size * 0.27f).dp))
            .background(Brush.linearGradient(listOf(sc.accent, sc.delete))),
        contentAlignment = Alignment.Center,
    ) {
        // carta de trás
        Box(
            Modifier
                .graphicsLayer { rotationZ = -13f; translationX = -size * 0.8f }
                .size((size * 0.5f).dp, (size * 0.62f).dp)
                .clip(RoundedCornerShape((size * 0.18f).dp))
                .background(Color.White.copy(alpha = 0.45f)),
        )
        // carta da frente
        Box(
            Modifier
                .graphicsLayer { rotationZ = 8f; translationX = size * 0.9f }
                .size((size * 0.5f).dp, (size * 0.62f).dp)
                .clip(RoundedCornerShape((size * 0.18f).dp))
                .background(Color.White),
        )
        Icon(
            Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding((size * 0.12f).dp)
                .size((size * 0.26f).dp),
        )
    }
}

@Composable
fun Wordmark(size: Int = 22) {
    val sc = LocalSc.current
    Row {
        Text("Swipe", color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold, fontSize = size.sp)
        Text("Clean", color = sc.accent, fontWeight = FontWeight.Bold, fontSize = size.sp)
    }
}

// ── Chip de origem do volume (cartão SD) ──────────────────────────────
@Composable
fun SdChip(small: Boolean = false) {
    val sc = LocalSc.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(sc.accentSoft)
            .padding(horizontal = if (small) 7.dp else 9.dp, vertical = if (small) 2.dp else 3.dp),
    ) {
        Text("SD", color = sc.accent, fontWeight = FontWeight.Bold,
            fontSize = if (small) 10.sp else 11.sp)
    }
}

// ── "Liberando / você liberou X" ──────────────────────────────────────
@Composable
fun FreedTally(text: String, label: String = "liberando", big: Boolean = false) {
    val sc = LocalSc.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (big) 8.dp else 6.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(sc.keepSoft)
            .padding(horizontal = if (big) 16.dp else 12.dp, vertical = if (big) 8.dp else 5.dp),
    ) {
        Icon(Icons.Outlined.AutoAwesome, null, tint = sc.keep, modifier = Modifier.size(if (big) 18.dp else 14.dp))
        Text(label, color = sc.dim, fontWeight = FontWeight.SemiBold, fontSize = if (big) 14.sp else 11.5.sp)
        Text(text, color = sc.keep, fontWeight = FontWeight.Bold, fontSize = if (big) 18.sp else 13.sp)
    }
}

// ── Botões ────────────────────────────────────────────────────────────
@Composable
fun ScPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = LocalSc.current.accent,
    content: Color = LocalSc.current.accentOn,
    icon: @Composable (() -> Unit)? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
    ) {
        if (icon != null) { icon(); Spacer(Modifier.width(8.dp)) }
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun ScGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
) {
    val sc = LocalSc.current
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, sc.line2),
    ) {
        if (icon != null) { icon(); Spacer(Modifier.width(8.dp)) }
        Text(text, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

// ── Progresso em "beads" (do Sereno, em âmbar) ────────────────────────
@Composable
fun ProgressBeads(done: Int, total: Int, modifier: Modifier = Modifier) {
    val sc = LocalSc.current
    val maxDots = 16
    val dots = if (total <= maxDots) total.coerceAtLeast(1) else maxDots
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically) {
            for (i in 0 until dots) {
                val realIdx = if (total <= maxDots) i
                else ((i.toFloat() / (dots - 1)) * (total - 1)).roundToInt()
                val isDone = realIdx < done
                val isCur = realIdx == done
                Box(
                    Modifier
                        .weight(if (isCur) 1.6f else 1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (isDone) sc.accent else if (isCur) sc.accentSoft else sc.line2),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Row {
            Text("$done", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            Text(" / $total", color = sc.faint, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
        }
    }
}

// ── Selo de arraste MANTER / APAGAR (estilo pill do Sereno) ───────────
@Composable
fun DragStamp(keep: Boolean, alpha: Float, modifier: Modifier = Modifier) {
    val sc = LocalSc.current
    val color = if (keep) sc.keep else sc.delete
    val on = if (keep) sc.keepOn else sc.deleteOn
    val a = (alpha * 1.15f).coerceIn(0f, 1f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .alpha(a)
            .scale(0.92f + a * 0.12f)
            .clip(CircleShape)
            .background(color)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Icon(if (keep) Icons.Filled.Favorite else Icons.Outlined.DeleteOutline, null, tint = on, modifier = Modifier.size(20.dp))
        Text(if (keep) "MANTER" else "APAGAR", color = on, fontWeight = FontWeight.Bold, fontSize = 19.sp)
    }
}

// ── Trio de ações do swipe (FABs circulares do Sereno) ────────────────
@Composable
fun ActionControls(
    onDelete: () -> Unit,
    onUndo: () -> Unit,
    onKeep: () -> Unit,
    canUndo: Boolean,
    modifier: Modifier = Modifier,
) {
    val sc = LocalSc.current
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Fab(big = true, bg = sc.deleteSoft, tint = sc.delete, onClick = onDelete) {
            Icon(Icons.Outlined.DeleteOutline, "Apagar", tint = sc.delete, modifier = Modifier.size(28.dp))
        }
        Fab(big = false, bg = Color.Transparent, tint = sc.dim, border = sc.line2,
            enabled = canUndo, onClick = onUndo) {
            Icon(Icons.AutoMirrored.Filled.Undo, "Desfazer", tint = sc.dim, modifier = Modifier.size(22.dp))
        }
        Fab(big = true, bg = sc.keepSoft, tint = sc.keep, onClick = onKeep) {
            Icon(Icons.Filled.Favorite, "Manter", tint = sc.keep, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun Fab(
    big: Boolean,
    bg: Color,
    tint: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    border: Color? = null,
    content: @Composable () -> Unit,
) {
    val d = if (big) 68.dp else 54.dp
    var m = Modifier
        .size(d)
        .clip(CircleShape)
        .background(bg)
    if (border != null) m = m.border(1.5.dp, border, CircleShape)
    m = m.alpha(if (enabled) 1f else 0.4f).clickable(enabled = enabled, onClick = onClick)
    Box(m, contentAlignment = Alignment.Center) { content() }
}
