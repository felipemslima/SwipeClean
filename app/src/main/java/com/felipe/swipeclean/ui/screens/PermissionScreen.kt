package com.felipe.swipeclean.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun PermissionScreen(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "SwipeClean",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            "Para limpar suas fotos (incluindo as do cartão SD), o app precisa de acesso às imagens do aparelho.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
        Button(onClick = onRequest) {
            Text("Permitir acesso às fotos")
        }
    }
}
