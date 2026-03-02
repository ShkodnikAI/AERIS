# AERIS

**Breathe Better. Live Better.**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%209%2B-green.svg)]()
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple.svg)]()

AERIS is an open-source breathing practices platform built with Kotlin Multiplatform (KMP). It provides science-based breathing protocols with AI-powered recommendations, health-grade safety features, and beautiful offline-first design.

---

## ⚠️ Medical Disclaimer

**AERIS is NOT a medical device.** This application is intended for general wellness purposes only and should not be used as a substitute for professional medical advice, diagnosis, or treatment.

Please consult with a healthcare provider before beginning any breathing exercise program, especially if you have:
- Cardiovascular conditions
- Respiratory conditions
- Psychological conditions
- High blood pressure
- Pregnancy

**Stop immediately** if you experience dizziness, pain, or discomfort.

---

## Features

### Core Features
- **3 Breathing Protocols** (MVP)
  - 4-7-8 Breathing (Relaxation)
  - Box Breathing (Focus)
  - Progressive Breath Hold (CO₂ Training)

- **AI Engine**
  - NSI (Nervous System Index) calculation
  - BCI (Breath Capacity Index) tracking
  - Personalized protocol recommendations

- **Safety System**
  - Heart rate monitoring integration
  - Automatic hold duration adjustment
  - Contraindication filtering
  - Emergency stop function

### Design
- Dark theme optimized for relaxation
- Smooth breathing animations (Circle, Square, Wave)
- Haptic feedback on phase changes
- Multi-language support (EN/RU)

---

## Installation

### Prerequisites

1. **JDK 17** (Eclipse Temurin or Amazon Corretto)
2. **Android SDK** with API 28-34
3. **PowerShell 5.1+** (Windows) or Terminal (macOS/Linux)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/aeris.git
cd aeris
```

2. Create `local.properties`:
```properties
sdk.dir=D:\\path\\to\\android-sdk
# Or for macOS/Linux:
# sdk.dir=/Users/USERNAME/Library/Android/sdk
```

3. Build the project:
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```

4. Install on device:
```bash
adb install androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

---

## Architecture

```
AERIS/
├── shared/                    # KMP Shared Module
│   ├── commonMain/           # Cross-platform code
│   │   ├── domain/           # Models, repositories, use cases
│   │   ├── ai/               # NSI/BCI calculators, recommender
│   │   └── di/               # Koin modules
│   ├── androidMain/          # Android-specific implementations
│   └── iosMain/              # iOS-specific implementations
│
├── androidApp/               # Android Application
│   ├── ui/                   # Compose screens & components
│   ├── data/                 # Room, DataStore, Health Connect
│   └── di/                   # Hilt modules
│
└── iosApp/                   # iOS Application (placeholder)
```

### Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| DI (Android) | Hilt |
| DI (Shared) | Koin |
| Database | Room |
| Preferences | DataStore + EncryptedSharedPreferences |
| Health | Health Connect API |
| Async | Kotlin Coroutines + Flow |
| Serialization | kotlinx.serialization |

---

## Protocols

### 4-7-8 Breathing
- **Category**: Relaxation & Sleep
- **Mechanism**: Parasympathetic activation
- **Pattern**: Inhale 4s → Hold 7s → Exhale 8s
- **Best for**: Evening, anxiety, sleep preparation

### Box Breathing
- **Category**: Energy & Focus
- **Mechanism**: Sympathetic balance
- **Pattern**: Inhale 4s → Hold 4s → Exhale 4s → Hold 4s
- **Best for**: Morning, focus, stress management

### Progressive Breath Hold
- **Category**: Therapy & Health
- **Mechanism**: CO₂ training
- **Pattern**: Progressive intervals (10s → 15s → 20s)
- **Best for**: Building respiratory capacity

---

## AI Engine

### NSI (Nervous System Index)
Calculates autonomic balance from:
- Heart rate
- HRV (Heart Rate Variability)
- Sleep quality
- Circadian rhythm

[See detailed documentation](docs/algorithms.md)

### BCI (Breath Capacity Index)
Measures respiratory fitness from:
- Maximum breath hold duration
- CO₂ tolerance
- Breathing rhythm stability
- Weekly progress

---

## Contributing

We welcome contributions! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Adding a New Protocol

1. Create JSON file in `shared/src/commonMain/resources/protocols/`
2. Follow the schema in `protocol_schema.json`
3. Add entry to `registry.json`
4. Run tests: `./gradlew test`

---

## Testing

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :shared:test
./gradlew :androidApp:test

# Run instrumented tests (requires device)
./gradlew :androidApp:connectedAndroidTest
```

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

- Dr. Andrew Weil for the 4-7-8 breathing technique
- Navy SEALs for Box Breathing methodology
- Buteyko method research community
- Material Design team for design guidelines
- Kotlin Multiplatform team for the amazing framework

---

## Support

- **Issues**: [GitHub Issues](https://github.com/your-username/aeris/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/aeris/discussions)

---

*Built with ❤️ for better breathing*
