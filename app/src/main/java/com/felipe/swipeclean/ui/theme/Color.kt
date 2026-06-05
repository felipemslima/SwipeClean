package com.felipe.swipeclean.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────
// SwipeClean · direção "Sereno+" (dark forçado)
//   manter = azul · apagar = coral · accent = âmbar (do Pop)
// ARGB no formato 0xAARRGGBB. As versões "soft" são geradas em código
// com .copy(alpha = ...), então aqui só ficam as cores cheias.
// ─────────────────────────────────────────────────────────────────────

// Superfícies / texto
val ScBg      = Color(0xFF14141F) // fundo da tela
val ScSurface = Color(0xFF1C1C2B) // superfície elevada (cards de resumo, etc.)
val ScCard    = Color(0xFF21212F) // cards de conteúdo
val ScElev    = Color(0xFF262638) // botão fantasma / chips
val ScLine    = Color(0x12FFFFFF) // hairline ~7%
val ScLine2   = Color(0x1FFFFFFF) // borda ~12%
val ScText    = Color(0xFFECECF6) // texto principal
val ScDim     = Color(0xFFA9A9C2) // texto secundário
val ScFaint   = Color(0xFF6E6E86) // texto terciário / placeholders

// Cores de ação (semânticas)
val Keep     = Color(0xFF7CB1FF) // "manter"  → primary
val KeepOn   = Color(0xFF08203F)
val Delete   = Color(0xFFFF90A1) // "apagar"  → error
val DeleteOn = Color(0xFF3D0A14)

// Accent de marca (do Pop)
val Accent   = Color(0xFFFFB871) // âmbar → tertiary
val AccentOn = Color(0xFF3A2206)

// Compatibilidade com o código antigo (Theme.kt original expunha estes nomes)
val KeepGreen = Keep
val DeleteRed = Delete
