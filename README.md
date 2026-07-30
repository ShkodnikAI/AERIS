# AERIS v5.0 — Breathing Training Platform

**Breathe Better. Live Better.**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%209%2B-green.svg)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)]()

AERIS is an open-source Android breathing practices platform built with Kotlin and Jetpack Compose. It provides science-based breathing protocols with AI-powered recommendations, health-grade safety features, and offline-first design.

## Features

- **8 Breathing Protocols**: 4-7-8, Box Breathing, Buteyko, Kumbhaka, Diaphragmatic, Kapalabhati, Alternate Nostril, Sitali
- **AI Engine**: NSI (Nervous System Index) + BCI (Breath Capacity Index) with personalized recommendations
- **Safety System**: Contraindication filtering, level-based access, health consent for advanced protocols
- **Progression**: Levels, streaks, and achievement badges
- **Dark Theme**: Optimized for relaxation
- **Offline-First**: Room database, no internet required
- **EN/RU Localization**

## Architecture

```
MVVM + Clean Architecture + Repository + Single Source of Truth

app/src/main/kotlin/com/aeris/
├── domain/     # Models, Use Cases, Repository Interfaces
├── data/       # Room, DataStore, Repository Implementations
├── di/         # Hilt Modules
├── ui/         # Compose Screens, ViewModels, Components
└── navigation/ # NavGraph, Routes
```

## Build

```powershell
cd D:\AI_projectS\AERIS
.\gradlew.bat assembleDebug
```

## Prerequisites

- JDK 17 (Eclipse Temurin or Amazon Corretto)
- Android SDK API 28-34
- PowerShell 5.1+

## Installation

1. Clone the repository
2. Create `local.properties` with your SDK path
3. Run `.\gradlew.bat assembleDebug`
4. Install APK from `app\build\outputs\apk\debug\app-debug.apk`

## Safety

See [docs/safety.md](docs/safety.md)

## Algorithms

See [docs/algorithms.md](docs/algorithms.md)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md)

## License

MIT License — see [LICENSE](LICENSE)
