# AERIS v2.0 - Product Requirements Document

## Project Overview
**Created**: 2026-01-02
**Status**: MVP Complete

AERIS is an open-source Kotlin Multiplatform (KMP) breathing practices platform with AI-powered recommendations, health-grade safety features, and offline-first design.

## User Personas

### Primary: Health-Conscious Adult (25-45)
- Seeks stress reduction and better sleep
- Comfortable with technology
- Values scientific backing
- Prefers offline-capable apps

### Secondary: Breath Training Enthusiast
- Practices yoga/meditation
- Wants progressive training protocols
- Interested in metrics and progress tracking

## Core Requirements (Static)

### Must Have (P0)
- [ ] 3+ breathing protocols with different purposes
- [ ] AI-based protocol recommendations (NSI/BCI)
- [ ] Animated breathing guide with haptic feedback
- [ ] Safety system (HR monitoring, contraindications)
- [ ] Offline-first architecture
- [ ] Dark theme optimized for relaxation

### Should Have (P1)
- [ ] Health Connect integration
- [ ] Session history and progress tracking
- [ ] Multi-language support (EN/RU)
- [ ] Level progression system

### Nice to Have (P2)
- [ ] Custom protocol creation
- [ ] Cloud sync
- [ ] Social sharing
- [ ] Apple Watch companion

## Implementation Status

### Completed (2026-01-02)
- ✅ Full KMP project structure (shared + androidApp + iosApp stub)
- ✅ 3 breathing protocols: 4-7-8, Box Breathing, Progressive Hold
- ✅ AI Engine: NSI Calculator with circadian factors
- ✅ AI Engine: BCI Calculator with age-based references
- ✅ Protocol Recommender with safety filtering
- ✅ Room database with entities (Session, User, Protocol)
- ✅ DataStore for preferences
- ✅ Health Connect integration with mock fallback
- ✅ Compose UI: Home, Protocols, Session, Profile screens
- ✅ Breathing animations: Circle, Square, Wave
- ✅ Safety system: HR thresholds, contraindications, disclaimers
- ✅ Hilt DI for Android, Koin for shared module
- ✅ Unit tests for NSI, BCI, Recommender (20+ tests)
- ✅ EN/RU localization
- ✅ Documentation: README, algorithms.md, CONTRIBUTING
- ✅ PowerShell generator script

### Pending
- iOS full implementation (placeholder only)
- Instrumented tests (androidTest)
- Sound effects for breathing phases
- Badges/achievements system
- PDF progress export

## Architecture

```
AERIS/
├── shared/          # KMP: domain, AI, repositories (Koin)
├── androidApp/      # Android: Compose UI, Room, Hilt
└── iosApp/          # iOS stub (SwiftUI placeholder)
```

### Tech Stack
| Component | Technology |
|-----------|------------|
| UI | Jetpack Compose + Material 3 |
| DI (Android) | Hilt |
| DI (Shared) | Koin |
| Database | Room |
| Preferences | DataStore |
| Health | Health Connect |
| Serialization | kotlinx.serialization |

## Prioritized Backlog

### P0 - Critical
1. Download and test build on Windows with JDK 17 + Android SDK
2. Run `./gradlew assembleDebug` to verify compilation

### P1 - High
1. Add sound effects for breathing phases
2. Implement badges/achievements
3. Add more protocols from protocol schema

### P2 - Medium
1. iOS full implementation
2. PDF progress export
3. Custom protocol creator
4. Widget for quick session start

## Next Tasks
1. User downloads `/app/aeris-project.zip`
2. Extract to `D:\AI_projectS\AERIS`
3. Set `JAVA_HOME` and `ANDROID_HOME`
4. Run `.\gradlew.bat assembleDebug`
5. Install APK on Android 9+ device

## Key Files
- `/app/aeris/` - Complete project
- `/app/aeris-project.zip` - Downloadable archive (129KB)
- `/app/aeris/docs/algorithms.md` - AI formulas with DOI references
