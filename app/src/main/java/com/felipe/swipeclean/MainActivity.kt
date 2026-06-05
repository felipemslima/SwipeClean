package com.felipe.swipeclean

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.screens.AlbumsScreen
import com.felipe.swipeclean.ui.screens.DoneScreen
import com.felipe.swipeclean.ui.screens.GPhotosScreen
import com.felipe.swipeclean.ui.screens.ReviewScreen
import com.felipe.swipeclean.ui.screens.SwipeScreen
import com.felipe.swipeclean.ui.screens.WelcomeScreen
import com.felipe.swipeclean.ui.theme.SwipeCleanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SwipeCleanTheme {   // dark forçado
                App()
            }
        }
    }
}

private enum class Screen { Albums, Swipe, Review, Done, GPhotos }

private fun readPermission(): String =
    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
    else Manifest.permission.READ_EXTERNAL_STORAGE

@Composable
private fun App(vm: SwipeViewModel = viewModel()) {
    val context = LocalContext.current
    val permission = readPermission()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) vm.loadLibrary()
    }
    LaunchedEffect(hasPermission) {
        if (hasPermission && vm.albums.isEmpty() && !vm.loading) vm.loadLibrary()
    }

    if (!hasPermission) {
        // antes: PermissionScreen — agora a tela de boas-vindas com identidade
        WelcomeScreen(onGrant = { permLauncher.launch(permission) })
        return
    }

    var screen by remember { mutableStateOf(Screen.Albums) }
    // espaço/contagem capturados na confirmação, exibidos na tela de conclusão
    var freedBytes by remember { mutableLongStateOf(0L) }
    var deletedCount by remember { mutableIntStateOf(0) }

    when (screen) {
        Screen.Albums -> AlbumsScreen(
            vm = vm,
            onOpenAlbum = { bucketId -> vm.startAlbum(bucketId); screen = Screen.Swipe },
            onOpenAll = { vm.startAll(); screen = Screen.Swipe },
            onResetKept = { vm.clearKept(); vm.loadLibrary() },
        )
        Screen.Swipe -> SwipeScreen(
            vm = vm,
            onBack = { screen = Screen.Albums },
            onReview = { screen = Screen.Review },
        )
        Screen.Review -> ReviewScreen(
            vm = vm,
            onBack = { screen = Screen.Swipe },
            onDeleted = { freed, count ->
                freedBytes = freed; deletedCount = count; screen = Screen.Done
            },
            onGooglePhotos = { screen = Screen.GPhotos },
        )
        Screen.Done -> DoneScreen(
            freedBytes = freedBytes,
            count = deletedCount,
            onBack = { screen = Screen.Albums },
        )
        Screen.GPhotos -> GPhotosScreen(
            vm = vm,
            onBack = { screen = Screen.Review },
        )
    }
}
