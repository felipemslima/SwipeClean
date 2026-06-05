# SwipeClean

App Android para limpar fotos por **swipe** (deslizar) — esquerda apaga, direita
mantém — com botões equivalentes. Você decide foto a foto e, no fim, confirma a
exclusão em lote.

## Por que esse e não o DeleteSwipe

- **Sem anúncios, sem paywall.** Tudo grátis.
- **Lê o cartão SD.** Itera todos os volumes do MediaStore
  (`getExternalVolumeNames`), não só o armazenamento interno — por isso enxerga
  fotos que o app comum ignora.
- **Undo de verdade.** Nada é apagado até você confirmar; qualquer decisão pode
  ser desfeita antes disso.
- **Organização por álbum** com contagem e tamanho, e quanto espaço você libera.

## Como funciona (fluxo)

1. **Álbuns** — lista por pasta (bucket), com capa, nº de fotos e tamanho.
2. **Swipe** — desliza/usa botões: ⬅️ apagar · ➡️ manter · ↩️ desfazer.
3. **Revisar** — vê as marcadas para apagar e o espaço a liberar.
4. **Apagar** — o sistema pede a confirmação final (vale também para o SD).

Nada é deletado antes do passo 4.

## Stack

- Kotlin + Jetpack Compose (Material 3)
- MediaStore (leitura multi-volume + `createDeleteRequest`)
- Coil (carregamento de imagens)
- Arquitetura MVVM (`SwipeViewModel`)
- `minSdk 30` (Android 11) · `targetSdk 34`

## Estrutura

```
app/src/main/
├── AndroidManifest.xml
├── java/com/felipe/swipeclean/
│   ├── MainActivity.kt              # entrada + navegação simples + permissões
│   ├── data/
│   │   ├── Photo.kt                 # modelos Photo e Album
│   │   └── MediaRepository.kt       # lê MediaStore (todos os volumes, incl. SD)
│   ├── ui/
│   │   ├── SwipeViewModel.kt        # estado: biblioteca, sessão, decisões, undo
│   │   ├── theme/Theme.kt
│   │   └── screens/
│   │       ├── PermissionScreen.kt
│   │       ├── AlbumsScreen.kt
│   │       ├── SwipeScreen.kt       # gesto de swipe + botões
│   │       └── ReviewScreen.kt      # exclusão em lote
│   └── util/Format.kt               # formata bytes
└── res/values/                      # strings, theme
```

## Rodar

Você não precisa entender Android para rodar. Siga **[SETUP.md](SETUP.md)** —
passo a passo do zero (instalar Android Studio → abrir → rodar no celular).

## Status

MVP. Próximos passos em `docs/ROADMAP.md`.
