package com.felipe.swipeclean.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.felipe.swipeclean.data.Album
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.util.formatBytes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumsScreen(
    vm: SwipeViewModel,
    onOpenAlbum: (String) -> Unit,
    onOpenAll: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("SwipeClean") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (vm.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            OutlinedButton(
                onClick = onOpenAll,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                val total = vm.albums.sumOf { it.photoCount }
                Text("Revisar todas as fotos ($total)")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(vm.albums, key = { it.bucketId }) { album ->
                    AlbumCard(album) { onOpenAlbum(album.bucketId) }
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(album: Album, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                AsyncImage(
                    model = album.coverUri,
                    contentDescription = album.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    album.name,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "${album.photoCount} fotos · ${formatBytes(album.totalBytes)}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
