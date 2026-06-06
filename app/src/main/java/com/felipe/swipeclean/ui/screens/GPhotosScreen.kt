package com.felipe.swipeclean.ui.screens

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
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.felipe.swipeclean.data.Photo
import com.felipe.swipeclean.gphotos.gphotosViewIntent
import com.felipe.swipeclean.gphotos.isGooglePhotosInstalled
import com.felipe.swipeclean.gphotos.openGooglePhotosApp
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.components.ScPrimaryButton
import com.felipe.swipeclean.ui.theme.LocalSc

/**
 * Apagar do Google Fotos — assistido manual.
 *
 * A triagem (decidir o que apagar) já foi feita no swipe. Aqui o app mostra as
 * fotos marcadas e leva você direto a elas no Google Fotos:
 *  - tocar numa miniatura abre AQUELA foto no Google Fotos (você toca na lixeira);
 *  - "Abrir Google Fotos" abre o app para seleção em lote (segurar + marcar + lixeira).
 *
 * Não automatiza cliques (a auto-seleção das suas fotos na grade do Google Fotos
 * não é possível de forma confiável) — é manual, mas rápido e sem falhas.
 */
@Composable
fun GPhotosScreen(
    vm: SwipeViewModel,
    onBack: () -> Unit,
) {
    val sc = LocalSc.current
    val context = LocalContext.current
    val photos: List<Photo> = remember { vm.pendingDelete }
    val gphotosInstalled = remember { isGooglePhotosInstalled(context) }

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
            Text("Apagar do Google Fotos", color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(end = 8.dp))
        }

        if (!gphotosInstalled) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("O app do Google Fotos não está instalado.", color = sc.delete, fontSize = 14.sp)
            }
            return@Column
        }

        // como fazer
        Column(Modifier.padding(horizontal = 18.dp)) {
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(sc.accentSoft).padding(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Outlined.Warning, null, tint = sc.accent, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(12.dp))
                Column {
                    Text("Como apagar na nuvem (1 lote)", color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "1. Toque em \"Abrir Google Fotos\".\n" +
                            "2. Segure uma foto para entrar no modo seleção.\n" +
                            "3. Marque as fotos deste lote e toque na lixeira (apaga do aparelho e da nuvem de uma vez).\n\n" +
                            "Ou toque numa miniatura abaixo para abrir aquela foto direto e mandar pra lixeira.",
                        color = sc.dim, fontSize = 12.5.sp, modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text("${photos.size} ${if (photos.size == 1) "foto marcada" else "fotos marcadas"}",
                color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(Modifier.height(8.dp))

            ScPrimaryButton(
                text = "Abrir Google Fotos",
                onClick = { openGooglePhotosApp(context) },
                modifier = Modifier.fillMaxWidth(),
                icon = { Icon(Icons.Outlined.OpenInNew, null, tint = sc.accentOn, modifier = Modifier.size(20.dp)) },
            )

            Spacer(Modifier.height(12.dp))
            Text("Suas fotos marcadas (toque para abrir no Google Fotos):",
                color = sc.faint, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items(photos, key = { it.id }) { photo ->
                Box(
                    Modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).background(sc.elev)
                        .clickable { context.startActivity(gphotosViewIntent(photo.uri)) },
                ) {
                    AsyncImage(photo.uri, photo.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
        }
    }
}
