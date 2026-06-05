package com.felipe.swipeclean.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.felipe.swipeclean.ui.components.FreedTally
import com.felipe.swipeclean.ui.components.ScPrimaryButton
import com.felipe.swipeclean.ui.theme.LocalSc
import com.felipe.swipeclean.util.formatBytes

/** Comemoração após o sistema confirmar a exclusão. */
@Composable
fun DoneScreen(freedBytes: Long, count: Int, onBack: () -> Unit) {
    val sc = LocalSc.current
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(Modifier.size(122.dp).clip(CircleShape).border(2.dp, sc.keep.copy(alpha = 0.27f), CircleShape))
            Box(Modifier.size(110.dp).clip(CircleShape).background(sc.keepSoft), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, null, tint = sc.keep, modifier = Modifier.size(56.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Faxina feita!", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 26.sp)
        Text("$count ${if (count == 1) "foto apagada" else "fotos apagadas"}.",
            color = sc.dim, fontSize = 15.sp, modifier = Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(18.dp))
        FreedTally(text = formatBytes(freedBytes), label = "você liberou", big = true)
        Spacer(Modifier.height(18.dp))
        ScPrimaryButton(
            "Voltar aos álbuns", onBack,
            icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = sc.accentOn, modifier = Modifier.size(20.dp)) },
        )
    }
}
