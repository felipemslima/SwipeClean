package com.felipe.swipeclean.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.HapticFeedbackConstants
import coil.compose.AsyncImage
import com.felipe.swipeclean.data.Photo
import com.felipe.swipeclean.ui.Decision
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.components.ActionControls
import com.felipe.swipeclean.ui.components.DragStamp
import com.felipe.swipeclean.ui.components.FreedTally
import com.felipe.swipeclean.ui.components.ProgressBeads
import com.felipe.swipeclean.ui.components.SdChip
import com.felipe.swipeclean.ui.theme.LocalSc
import com.felipe.swipeclean.util.formatBytes
import kotlinx.coroutines.launch
import kotlin.math.abs

private fun Photo.isSd() = volumeName != "external_primary"

@Composable
fun SwipeScreen(
    vm: SwipeViewModel,
    onBack: () -> Unit,
    onReview: () -> Unit,
) {
    val sc = LocalSc.current
    val total = vm.sessionPhotos.size
    val pos = (vm.index + 1).coerceAtMost(total)

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // top bar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier.clip(CircleShape).clickable(onClick = onBack).padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, tint = sc.dim, modifier = Modifier.size(18.dp))
                Text("Álbuns", color = sc.dim, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
            }
            Box(Modifier.weight(1f))
            val pending = vm.pendingDelete.size
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(if (pending > 0) sc.deleteSoft else Color.Transparent)
                    .clickable(enabled = pending > 0, onClick = onReview)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.DeleteOutline, null, tint = if (pending > 0) sc.delete else sc.faint, modifier = Modifier.size(16.dp))
                Text("  Apagar ($pending)", color = if (pending > 0) sc.delete else sc.faint,
                    fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            }
        }

        // progress band
        Column(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
            ProgressBeads(done = vm.index.coerceAtMost(total), total = total)
            if (vm.pendingBytes > 0) {
                Spacer(Modifier.height(8.dp))
                FreedTally(text = formatBytes(vm.pendingBytes))
            }
        }

        // card area
        BoxWithConstraints(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            val photo = vm.current
            if (photo == null) {
                FinishedState(
                    pendingCount = vm.pendingDelete.size,
                    pendingBytes = vm.pendingBytes,
                    onReview = onReview, onBack = onBack,
                )
                return@BoxWithConstraints
            }

            val density = LocalDensity.current
            val view = LocalView.current
            val limit = with(density) { maxWidth.toPx() }
            val threshold = limit * 0.32f
            val scope = rememberCoroutineScope()
            // recriar por índice zera a posição a cada nova foto
            val offsetX = remember(vm.index) { Animatable(0f) }
            val crossed = remember(vm.index) { booleanArrayOf(false) }

            fun fling(decision: Decision) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                scope.launch {
                    val target = if (decision == Decision.KEEP) limit * 1.7f else -limit * 1.7f
                    offsetX.animateTo(target)
                    vm.decide(decision)
                }
            }

            val intensity = (abs(offsetX.value) / threshold).coerceIn(0f, 1f)
            val keepAlpha = (offsetX.value / threshold).coerceIn(0f, 1f)
            val delAlpha = (-offsetX.value / threshold).coerceIn(0f, 1f)
            val floodColor = if (offsetX.value >= 0) sc.keep else sc.delete

            // color flood behind the deck
            Box(Modifier.fillMaxSize().graphicsLayer { alpha = intensity * 0.28f }
                .background(Brush.radialGradient(listOf(floodColor, Color.Transparent))))

            // behind card (próxima foto da pilha)
            val next = vm.sessionPhotos.getOrNull(vm.index + 1)
            if (next != null) {
                Box(
                    Modifier.fillMaxSize()
                        .graphicsLayer { scaleX = 0.95f; scaleY = 0.95f; translationY = -12.dp.toPx() }
                        .clip(RoundedCornerShape(30.dp)).background(sc.card),
                ) {
                    AsyncImage(next.uri, next.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }

            // front card (draggable)
            Box(
                Modifier.fillMaxSize()
                    .graphicsLayer {
                        translationX = offsetX.value
                        rotationZ = offsetX.value / 22f
                    }
                    .clip(RoundedCornerShape(30.dp)).background(sc.card)
                    .pointerInput(vm.index) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, drag ->
                                change.consume()
                                scope.launch { offsetX.snapTo(offsetX.value + drag) }
                                if (abs(offsetX.value) > threshold && !crossed[0]) {
                                    crossed[0] = true
                                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                } else if (abs(offsetX.value) < threshold) crossed[0] = false
                            },
                            onDragEnd = {
                                when {
                                    offsetX.value > threshold -> fling(Decision.KEEP)
                                    offsetX.value < -threshold -> fling(Decision.DELETE)
                                    else -> scope.launch { offsetX.animateTo(0f) }
                                }
                            },
                        )
                    },
            ) {
                AsyncImage(photo.uri, photo.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                // veil para legibilidade da legenda
                Box(Modifier.fillMaxSize().background(
                    Brush.verticalGradient(0.62f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.55f))
                ))
                if (photo.isSd()) Box(Modifier.align(Alignment.TopStart).padding(12.dp)) { SdChip() }
                // selos de arraste
                Box(Modifier.align(Alignment.TopEnd).padding(16.dp)) { DragStamp(keep = true, alpha = keepAlpha) }
                Box(Modifier.align(Alignment.TopStart).padding(16.dp)) { DragStamp(keep = false, alpha = delAlpha) }
                // legenda
                Row(
                    Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(photo.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                    Text(formatBytes(photo.sizeBytes), color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // actions
        if (vm.current != null) {
            val scope = rememberCoroutineScope()
            Column(Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                // os botões repetem o gesto programaticamente
                ActionControls(
                    onDelete = { vm.decide(Decision.DELETE) },
                    onUndo = { vm.undo() },
                    onKeep = { vm.decide(Decision.KEEP) },
                    canUndo = vm.canUndo,
                )
                Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.Center) {
                    Text("Arraste · ", color = sc.faint, fontSize = 11.5.sp)
                    Text("esquerda apaga", color = sc.delete, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    Text(" · ", color = sc.faint, fontSize = 11.5.sp)
                    Text("direita mantém", color = sc.keep, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun FinishedState(pendingCount: Int, pendingBytes: Long, onReview: () -> Unit, onBack: () -> Unit) {
    val sc = LocalSc.current
    Column(
        Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(84.dp).clip(CircleShape).background(sc.keepSoft), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Check, null, tint = sc.keep, modifier = Modifier.size(44.dp))
        }
        Text("Você revisou tudo!", color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(top = 16.dp))
        Text(
            if (pendingCount > 0) "$pendingCount marcadas para apagar" else "Nada marcado para apagar.",
            color = sc.dim, fontSize = 14.5.sp, modifier = Modifier.padding(top = 6.dp),
        )
        if (pendingCount > 0) {
            Spacer(Modifier.height(16.dp))
            FreedTally(text = formatBytes(pendingBytes), big = true)
            Spacer(Modifier.height(16.dp))
            com.felipe.swipeclean.ui.components.ScPrimaryButton(
                "Revisar e apagar", onReview, container = sc.delete, content = sc.deleteOn,
                icon = { Icon(Icons.Outlined.DeleteOutline, null, tint = sc.deleteOn, modifier = Modifier.size(20.dp)) },
            )
        }
        Spacer(Modifier.height(10.dp))
        com.felipe.swipeclean.ui.components.ScGhostButton(
            "Voltar aos álbuns", onBack,
            icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(18.dp)) },
        )
    }
}
