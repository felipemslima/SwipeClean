package com.felipe.swipeclean.ui.screens

import android.app.Activity
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.theme.DeleteRed
import com.felipe.swipeclean.util.formatBytes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    vm: SwipeViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val pending = vm.pendingDelete

    // Lançador da confirmação de exclusão do sistema (funciona p/ SD via MediaStore).
    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            vm.onDeleted()
            onDone()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar exclusão") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Text(
                "${pending.size} fotos · libera ${formatBytes(vm.pendingBytes)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            ) {
                items(pending, key = { it.id }) { photo ->
                    Box(Modifier.padding(2.dp).aspectRatio(1f)) {
                        AsyncImage(
                            model = photo.uri,
                            contentDescription = photo.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val uris = pending.map { it.uri }
                    if (uris.isNotEmpty()) {
                        val pendingIntent =
                            MediaStore.createDeleteRequest(context.contentResolver, uris)
                        deleteLauncher.launch(
                            IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                        )
                    }
                },
                enabled = pending.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = DeleteRed),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                Text("Apagar ${pending.size} fotos definitivamente")
            }
        }
    }
}
