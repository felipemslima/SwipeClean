package com.felipe.swipeclean.ui.screens

import android.app.Activity
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.felipe.swipeclean.ui.Decision
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.components.ScPrimaryButton
import com.felipe.swipeclean.ui.components.SdChip
import com.felipe.swipeclean.ui.theme.LocalSc
import com.felipe.swipeclean.util.formatBytes

private fun isSd(volume: String) = volume != "external_primary"

/**
 * Confirmar exclusão. Captura o espaço/contagem ANTES de apagar e repassa
 * para a tela de conclusão via onDeleted(freedBytes, count).
 */
@Composable
fun ReviewScreen(
    vm: SwipeViewModel,
    onBack: () -> Unit,
    onDeleted: (freedBytes: Long, count: Int) -> Unit,
    onGooglePhotos: () -> Unit = {},
) {
    val sc = LocalSc.current
    val context = LocalContext.current
    val pending = vm.pendingDelete

    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val freed = vm.pendingBytes
            val count = vm.pendingDelete.size
            vm.onDeleted()
            onDeleted(freed, count)
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // top bar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                Modifier.clip(CircleShape).clickable(onClick = onBack).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, tint = sc.dim, modifier = Modifier.size(18.dp))
                Text("Voltar", color = sc.dim, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp)
            }
            Box(Modifier.weight(1f))
            Text("Confirmar exclusão", color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(end = 8.dp))
        }

        // reward hero
        Box(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(30.dp)).background(sc.keepSoft)
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(50.dp).clip(CircleShape).background(sc.keep), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = sc.keepOn, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.size(14.dp))
                Column {
                    Text("Libera ${formatBytes(vm.pendingBytes)}", color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Text("${pending.size} ${if (pending.size == 1) "foto marcada" else "fotos marcadas"} para apagar",
                        color = sc.dim, fontSize = 13.sp)
                }
            }
        }
        Text("Toque no ✕ para tirar uma foto da lista antes de confirmar.",
            color = sc.faint, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))

        // grid 3 col com desmarcar individual
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items(pending, key = { it.id }) { photo ->
                Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(22.dp)).background(sc.elev)) {
                    AsyncImage(photo.uri, photo.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    Box(Modifier.fillMaxSize().background(
                        Brush.verticalGradient(0.55f to Color.Transparent, 1f to sc.delete.copy(alpha = 0.45f))
                    ))
                    if (isSd(photo.volumeName)) Box(Modifier.align(Alignment.BottomStart).padding(5.dp)) { SdChip(small = true) }
                    // "tirar da lista" => marca como MANTER (sai de pendingDelete)
                    Box(
                        Modifier.align(Alignment.TopEnd).padding(5.dp).size(26.dp).clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .clickable { vm.decisions[photo.id] = Decision.KEEP },
                        contentAlignment = Alignment.Center,
                    ) { Icon(Icons.Filled.Close, "Tirar da lista", tint = Color.White, modifier = Modifier.size(15.dp)) }
                }
            }
        }

        // ação destrutiva
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            ScPrimaryButton(
                text = if (pending.isNotEmpty())
                    "Apagar ${pending.size} ${if (pending.size == 1) "foto" else "fotos"} definitivamente"
                else "Nada para apagar",
                onClick = {
                    val uris = pending.map { it.uri }
                    if (uris.isNotEmpty()) {
                        val pi = MediaStore.createDeleteRequest(context.contentResolver, uris)
                        deleteLauncher.launch(IntentSenderRequest.Builder(pi.intentSender).build())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                container = sc.delete, content = sc.deleteOn,
                icon = { Icon(Icons.Outlined.DeleteOutline, null, tint = sc.deleteOn, modifier = Modifier.size(20.dp)) },
            )
            Text(
                "O Android vai pedir uma última confirmação · vale para o cartão SD também",
                color = sc.faint, fontSize = 11.5.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 9.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.height(10.dp))
            com.felipe.swipeclean.ui.components.ScGhostButton(
                text = "Apagar do Google Fotos (beta)",
                onClick = onGooglePhotos,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
