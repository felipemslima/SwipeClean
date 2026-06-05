package com.felipe.swipeclean.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.felipe.swipeclean.gphotos.GPhotosDeleteService
import com.felipe.swipeclean.gphotos.GPhotosQueue
import com.felipe.swipeclean.gphotos.gphotosViewIntent
import com.felipe.swipeclean.gphotos.isAccessibilityServiceEnabled
import com.felipe.swipeclean.gphotos.isGooglePhotosInstalled
import com.felipe.swipeclean.gphotos.openAccessibilitySettings
import com.felipe.swipeclean.ui.SwipeViewModel
import com.felipe.swipeclean.ui.components.ScGhostButton
import com.felipe.swipeclean.ui.components.ScPrimaryButton
import com.felipe.swipeclean.ui.theme.LocalSc

/**
 * Limpeza no Google Fotos via automação (AccessibilityService). EXPERIMENTAL.
 * Usa as fotos atualmente marcadas para apagar (pendingDelete) e dirige o app
 * do Google Fotos para apagá-las (device + nuvem).
 */
@Composable
fun GPhotosScreen(
    vm: SwipeViewModel,
    onBack: () -> Unit,
) {
    val sc = LocalSc.current
    val context = LocalContext.current
    val uris = remember { vm.pendingDelete.map { it.uri } }

    // Entrada nova na tela: se não há automação rodando, zera o estado anterior
    // (senão fica preso em "Concluído" do lote passado).
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (!GPhotosQueue.active) GPhotosQueue.reset()
    }

    val gphotosInstalled = remember { isGooglePhotosInstalled(context) }

    // Recheca se o serviço está habilitado toda vez que a tela volta ao primeiro plano.
    var serviceOn by remember {
        mutableStateOf(isAccessibilityServiceEnabled(context, GPhotosDeleteService::class.java))
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                serviceOn = isAccessibilityServiceEnabled(context, GPhotosDeleteService::class.java)
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
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
            Text("Google Fotos · beta", color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(end = 8.dp))
        }

        Column(Modifier.padding(horizontal = 18.dp)) {

            // aviso beta
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(sc.accentSoft).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Warning, null, tint = sc.accent, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(12.dp))
                Text(
                    "Automação experimental. O SwipeClean abre cada foto no Google Fotos e " +
                        "toca apagar/confirmar sozinho. Não desligue a tela durante o processo. " +
                        "Funciona em PT-BR/EN; se o Google Fotos estiver em outro idioma pode falhar.",
                    color = sc.dim, fontSize = 12.5.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            if (!gphotosInstalled) {
                Text("O app do Google Fotos não está instalado neste aparelho.",
                    color = sc.delete, fontSize = 14.sp)
                return@Column
            }

            Text("${uris.size} ${if (uris.size == 1) "foto marcada" else "fotos marcadas"} para apagar",
                color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(Modifier.height(16.dp))

            // status do serviço
            StatusRow(
                ok = serviceOn,
                okText = "Serviço de acessibilidade ativo",
                offText = "Serviço de acessibilidade desligado",
            )
            if (!serviceOn) {
                Spacer(Modifier.height(8.dp))
                ScGhostButton(
                    "Ativar serviço nas Configurações",
                    onClick = { openAccessibilitySettings(context) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Em Configurações ▸ Acessibilidade ▸ SwipeClean, ligue o serviço e volte.",
                    color = sc.faint, fontSize = 11.5.sp, modifier = Modifier.padding(top = 6.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            // estado da execução / controles
            when (GPhotosQueue.phase) {
                GPhotosQueue.Phase.Concluido -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, null, tint = sc.keep, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Concluído!", color = sc.keep, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Text(GPhotosQueue.log, color = sc.dim, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                }

                else -> {
                    if (GPhotosQueue.active) {
                        Text(
                            "Processando ${(GPhotosQueue.index + 1).coerceAtMost(GPhotosQueue.total)} / ${GPhotosQueue.total}",
                            color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                        )
                        Text(phaseLabel(GPhotosQueue.phase) + " · " + GPhotosQueue.log,
                            color = sc.dim, fontSize = 12.5.sp, modifier = Modifier.padding(top = 4.dp))
                        Spacer(Modifier.height(12.dp))
                        ScGhostButton("Parar", onClick = { GPhotosQueue.stop() }, modifier = Modifier.fillMaxWidth())
                    } else {
                        ScPrimaryButton(
                            text = "Apagar do Google Fotos",
                            onClick = {
                                GPhotosQueue.start(uris)
                                GPhotosQueue.currentUri?.let { context.startActivity(gphotosViewIntent(it)) }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            container = sc.delete, content = sc.deleteOn,
                        )
                        if (!serviceOn) {
                            Text("Ligue o serviço acima antes de iniciar.",
                                color = sc.faint, fontSize = 11.5.sp, modifier = Modifier.padding(top = 6.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatusRow(ok: Boolean, okText: String, offText: String) {
    val sc = LocalSc.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(10.dp).clip(CircleShape).background(if (ok) sc.keep else sc.delete),
        )
        Spacer(Modifier.size(10.dp))
        Text(if (ok) okText else offText,
            color = if (ok) sc.keep else sc.delete, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

private fun phaseLabel(p: GPhotosQueue.Phase): String = when (p) {
    GPhotosQueue.Phase.AbrindoFoto -> "abrindo foto"
    GPhotosQueue.Phase.ProcurandoApagar -> "procurando apagar"
    GPhotosQueue.Phase.ProcurandoConfirmar -> "confirmando"
    GPhotosQueue.Phase.Concluido -> "concluído"
    GPhotosQueue.Phase.Parado -> "parado"
    GPhotosQueue.Phase.Idle -> "aguardando"
}
