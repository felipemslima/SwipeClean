package com.felipe.swipeclean.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.felipe.swipeclean.ui.Decision
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.theme.DeleteRed
import com.felipe.swipeclean.ui.theme.KeepGreen
import com.felipe.swipeclean.util.formatBytes
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    vm: SwipeViewModel,
    onBack: () -> Unit,
    onReview: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val total = vm.sessionPhotos.size
                    val pos = (vm.index + 1).coerceAtMost(total)
                    Text(if (total == 0) "Sem fotos" else "$pos de $total")
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Álbuns") }
                },
                actions = {
                    IconButton(onClick = { vm.undo() }, enabled = vm.canUndo) {
                        Icon(Icons.Default.Undo, contentDescription = "Desfazer")
                    }
                    TextButton(onClick = onReview, enabled = vm.pendingDelete.isNotEmpty()) {
                        Text("Apagar (${vm.pendingDelete.size})")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val photo = vm.current
            if (photo == null) {
                // Fim da sessão
                FinishedState(
                    pendingCount = vm.pendingDelete.size,
                    pendingBytes = vm.pendingBytes,
                    onReview = onReview,
                    onBack = onBack,
                )
                return@Column
            }

            BoxWithConstraints(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                val scope = rememberCoroutineScope()
                // Recriar o Animatable por índice zera a posição a cada nova foto.
                val offsetX = remember(vm.index) { Animatable(0f) }
                val threshold = 300f

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = offsetX.value
                            rotationZ = offsetX.value / 60f
                        }
                        .pointerInput(vm.index) {
                            val limit = size.width.toFloat()
                            detectHorizontalDragGestures(
                                onHorizontalDrag = { change, drag ->
                                    change.consume()
                                    scope.launch { offsetX.snapTo(offsetX.value + drag) }
                                },
                                onDragEnd = {
                                    when {
                                        offsetX.value > threshold -> scope.launch {
                                            offsetX.animateTo(limit * 1.5f)
                                            vm.decide(Decision.KEEP)
                                        }
                                        offsetX.value < -threshold -> scope.launch {
                                            offsetX.animateTo(-limit * 1.5f)
                                            vm.decide(Decision.DELETE)
                                        }
                                        else -> scope.launch { offsetX.animateTo(0f) }
                                    }
                                },
                            )
                        },
                ) {
                    Box(Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = photo.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        )
                        // Selos "MANTER" / "APAGAR" conforme arrasta
                        val keepAlpha = (offsetX.value / threshold).coerceIn(0f, 1f)
                        val delAlpha = (-offsetX.value / threshold).coerceIn(0f, 1f)
                        Stamp("MANTER", KeepGreen, Alignment.TopStart, keepAlpha)
                        Stamp("APAGAR", DeleteRed, Alignment.TopEnd, delAlpha)
                    }
                }
            }

            Text(
                "${photo.name} · ${formatBytes(photo.sizeBytes)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                FilledTonalButton(onClick = { vm.decide(Decision.DELETE) }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Text("  Apagar")
                }
                TextButton(onClick = { vm.undo() }, enabled = vm.canUndo) {
                    Icon(Icons.Default.Undo, contentDescription = null)
                    Text("  Desfazer")
                }
                Button(onClick = { vm.decide(Decision.KEEP) }) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                    Text("  Manter")
                }
            }
        }
    }
}

@Composable
private fun Stamp(text: String, color: Color, alignment: Alignment, alpha: Float) {
    Box(Modifier.fillMaxSize(), contentAlignment = alignment) {
        Card(
            modifier = Modifier.padding(24.dp).alpha(alpha),
        ) {
            Text(
                text,
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun FinishedState(
    pendingCount: Int,
    pendingBytes: Long,
    onReview: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Você revisou tudo!", style = MaterialTheme.typography.headlineSmall)
        if (pendingCount > 0) {
            Text(
                "$pendingCount marcadas para apagar · ${formatBytes(pendingBytes)}",
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Button(onClick = onReview) { Text("Revisar e apagar") }
        } else {
            Text("Nada marcado para apagar.", modifier = Modifier.padding(vertical = 16.dp))
        }
        TextButton(onClick = onBack) { Text("Voltar aos álbuns") }
    }
}
