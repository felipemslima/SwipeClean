package com.felipe.swipeclean.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.SdCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.swipeclean.ui.components.LogoMark
import com.felipe.swipeclean.ui.components.ScPrimaryButton
import com.felipe.swipeclean.ui.components.Wordmark
import com.felipe.swipeclean.ui.theme.LocalSc

private data class Benefit(val icon: ImageVector, val title: String, val body: String)

/**
 * Tela de boas-vindas / permissão. Substitui a antiga PermissionScreen —
 * mesma callback (onRequest -> onGrant).
 */
@Composable
fun WelcomeScreen(onGrant: () -> Unit) {
    val sc = LocalSc.current
    val benefits = listOf(
        Benefit(Icons.Outlined.AutoAwesome, "Sem anúncios, sem paywall", "Só você e suas fotos. Nada de banners ou travas."),
        Benefit(Icons.Outlined.SdCard, "Enxerga o cartão SD", "Lê todos os volumes — não só o armazenamento interno."),
        Benefit(Icons.AutoMirrored.Filled.Undo, "Undo de verdade", "Nada é apagado até você confirmar no fim."),
        Benefit(Icons.Outlined.AutoAwesome, "Veja o espaço liberado", "Cada deslize soma MB e GB de volta pra você."),
    )
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // hero: pilha de cartas (placeholder antes da permissão)
        Box(Modifier.height(190.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            listOf(2, 1, 0).forEach { s ->
                Box(
                    Modifier
                        .graphicsLayer {
                            translationX = (s - 1) * 8.dp.toPx()
                            translationY = s * 4.dp.toPx()
                            rotationZ = (s - 1) * -7f
                            scaleX = 1f - s * 0.05f; scaleY = 1f - s * 0.05f
                        }
                        .size(124.dp, 158.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(sc.card, if (s == 0) sc.deleteSoft else sc.elev)
                            )
                        ),
                ) {
                    if (s == 0) {
                        Box(
                            Modifier.padding(9.dp).size(34.dp).clip(RoundedCornerShape(50))
                                .background(sc.delete),
                            contentAlignment = Alignment.Center,
                        ) { Icon(Icons.Outlined.DeleteOutline, null, tint = sc.deleteOn, modifier = Modifier.size(18.dp)) }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LogoMark(size = 44)
            Wordmark(size = 28)
        }
        Text(
            "Limpe a galeria num gesto. Deslize, libere espaço, sinta a faxina.",
            color = sc.dim, textAlign = TextAlign.Center, fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp, start = 28.dp, end = 28.dp),
        )

        Column(Modifier.padding(top = 20.dp, start = 22.dp, end = 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            benefits.forEach { b ->
                Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    Box(
                        Modifier.size(38.dp).clip(RoundedCornerShape(22.dp)).background(sc.accentSoft),
                        contentAlignment = Alignment.Center,
                    ) { Icon(b.icon, null, tint = sc.accent, modifier = Modifier.size(19.dp)) }
                    Column {
                        Text(b.title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                        Text(b.body, color = sc.dim, fontSize = 12.5.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        ScPrimaryButton(
            text = "Permitir acesso às fotos",
            onClick = onGrant,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
            icon = { Icon(Icons.Outlined.PhotoLibrary, null, tint = sc.accentOn, modifier = Modifier.size(20.dp)) },
        )
        Text(
            "Inclui fotos do cartão SD · nada sai do aparelho",
            color = sc.faint, fontSize = 11.5.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}
