package com.felipe.swipeclean.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.felipe.swipeclean.data.Album
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.components.LogoMark
import com.felipe.swipeclean.ui.components.ScGhostButton
import com.felipe.swipeclean.ui.components.ScPrimaryButton
import com.felipe.swipeclean.ui.components.Wordmark
import com.felipe.swipeclean.ui.theme.LocalSc
import com.felipe.swipeclean.util.formatBytes

@Composable
fun AlbumsScreen(
    vm: SwipeViewModel,
    onOpenAlbum: (String) -> Unit,
    onOpenAll: () -> Unit,
    onResetKept: () -> Unit,
) {
    val sc = LocalSc.current
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        // header
        Row(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LogoMark(size = 32)
            Row(Modifier.padding(start = 10.dp)) { Wordmark(size = 21) }
            Box(Modifier.weight(1f))
            var menu by remember { mutableStateOf(false) }
            IconButton(onClick = { menu = true }) {
                Icon(Icons.Filled.MoreVert, "Mais", tint = sc.dim)
            }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                DropdownMenuItem(text = { Text("Rever fotos mantidas") },
                    onClick = { menu = false; onResetKept() })
            }
        }

        if (vm.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = sc.accent)
            }
            return@Column
        }

        val pendingTotal = vm.albums.sumOf { vm.pendingCount(it.bucketId) }
        val totalPhotos = vm.albums.sumOf { it.photoCount }
        val totalBytes = vm.albums.sumOf { it.totalBytes }

        // resumo da biblioteca + CTA "revisar tudo"
        Box(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Column(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(Brush.linearGradient(listOf(sc.accentSoft, sc.elev)))
                    .padding(18.dp),
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$totalPhotos", color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    Text("  fotos · ${formatBytes(totalBytes)}", color = sc.dim,
                        fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                Text(
                    if (pendingTotal > 0) "$pendingTotal ainda a revisar" else "Tudo revisado por enquanto",
                    color = sc.dim, fontSize = 12.5.sp, modifier = Modifier.padding(top = 2.dp, bottom = 14.dp),
                )
                if (pendingTotal > 0) {
                    ScPrimaryButton(
                        "Revisar tudo ($pendingTotal)", onOpenAll, Modifier.fillMaxWidth(),
                        icon = { Icon(Icons.Outlined.Swipe, null, tint = sc.accentOn, modifier = Modifier.size(20.dp)) },
                    )
                } else {
                    ScGhostButton(
                        "Rever fotos mantidas", onResetKept, Modifier.fillMaxWidth(),
                        icon = { Icon(Icons.AutoMirrored.Filled.Undo, null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(18.dp)) },
                    )
                }
            }
        }

        Text("ÁLBUNS", color = sc.faint, fontWeight = FontWeight.Bold, fontSize = 13.sp,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp),
        ) {
            items(vm.albums, key = { it.bucketId }) { album ->
                AlbumCard(album, pending = vm.pendingCount(album.bucketId)) { onOpenAlbum(album.bucketId) }
            }
        }
    }
}

@Composable
private fun AlbumCard(album: Album, pending: Int, onClick: () -> Unit) {
    val sc = LocalSc.current
    val done = pending == 0
    Column(
        Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(sc.card)
            .clickable(onClick = onClick),
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(1f)) {
            AsyncImage(
                model = album.coverUri, contentDescription = album.name,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
            )
            Box(Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)))
            ))
            if (!done) {
                Box(
                    Modifier.align(Alignment.TopEnd).padding(8.dp)
                        .clip(CircleShape).background(sc.accent)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) { Text("$pending", color = sc.accentOn, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            }
            // Para o selo "SD" aqui, adicione volumeName ao data class Album
            // (1 linha em MediaRepository.groupIntoAlbums) e renderize SdChip().
        }
        Column(Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 12.dp)) {
            Text(album.name, color = MaterialTheme.colorScheme.onBackground, maxLines = 1,
                fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("${album.photoCount} fotos · ${formatBytes(album.totalBytes)}",
                color = sc.dim, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                if (done) {
                    Icon(Icons.Filled.Check, null, tint = sc.keep, modifier = Modifier.size(13.dp))
                    Text(" tudo revisado", color = sc.keep, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                } else {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(sc.accent))
                    Text("  $pending a revisar", color = sc.accent, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp)
                }
            }
        }
    }
}
