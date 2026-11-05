# SwipeClean

**Clean up your phone's photos one swipe at a time.** Swipe left to delete, right to keep, then confirm everything in one batch.

<!-- TODO: add 2–3 screenshots (albums, swipe, review) or a short GIF -->

Native Android app in **Kotlin + Jetpack Compose**. Free, no ads, no paywall.

## Why I built it

Popular swipe-to-delete apps hide features behind paywalls, and most of them **cannot see photos on the SD card**. SwipeClean was built to fix both:

- **Reads every storage volume.** It iterates all MediaStore volumes (`getExternalVolumeNames`), not only internal storage, so photos on the SD card show up too.
- **Real undo.** Nothing is deleted until you confirm, and any decision can be reversed before that.
- **Remembers what you kept.** Photos you already kept are skipped next time, and each album shows how many are still left to review.
- **Album view** with cover, photo count, size and how much space you will free.

## How it works

1. **Albums** — photos grouped by folder, with count and total size
2. **Swipe** — ⬅️ delete · ➡️ keep · ↩️ undo (gestures or buttons)
3. **Review** — see everything marked for deletion and the space to be freed
4. **Delete** — a single system confirmation (`MediaStore.createDeleteRequest`), which also works on the SD card

## Tech

| | |
|---|---|
| UI | Jetpack Compose, Material 3, custom dark theme |
| Architecture | MVVM (`SwipeViewModel` holds library, session, decisions and the undo stack) |
| Data | MediaStore multi-volume queries, `SharedPreferences` for kept photos |
| Images | Coil |
| Target | `minSdk 30` (Android 11, required for `createDeleteRequest` and per-volume access), `targetSdk 34` |

```
app/src/main/java/com/felipe/swipeclean/
├── MainActivity.kt          # entry point, navigation, permissions
├── data/
│   ├── MediaRepository.kt   # MediaStore queries across all volumes
│   ├── KeepStore.kt         # persists kept photos between sessions
│   └── Photo.kt             # Photo / Album models
├── ui/
│   ├── SwipeViewModel.kt
│   ├── screens/             # Welcome, Albums, Swipe, Review, Done
│   └── theme/
└── util/Format.kt
```

## Running

Open the project in Android Studio and run it on a device (a physical phone is needed to test the SD card). For a step-by-step guide from zero, see [SETUP.md](SETUP.md) (in Portuguese).

## Roadmap

MVP is done. Next up: group by month, video support, session statistics and duplicate detection. See [docs/ROADMAP.md](docs/ROADMAP.md).
