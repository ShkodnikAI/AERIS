# Contributing to AERIS

Thank you for your interest in contributing to AERIS! This document provides guidelines and instructions for contributing.

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md).

## How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](https://github.com/your-username/aeris/issues)
2. If not, create a new issue with:
   - Clear, descriptive title
   - Steps to reproduce
   - Expected vs actual behavior
   - Device/OS information
   - Screenshots if applicable

### Suggesting Features

1. Check existing issues and discussions
2. Create a new issue with the `enhancement` label
3. Describe the feature and its benefits
4. Include mockups if applicable

### Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes
4. Run tests: `./gradlew test`
5. Commit with clear messages
6. Push and create a Pull Request

## Development Setup

### Prerequisites

- JDK 17
- Android SDK (API 28-34)
- Android Studio Arctic Fox or later

### Building

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/aeris.git
cd aeris

# Create local.properties
echo "sdk.dir=/path/to/android/sdk" > local.properties

# Build
./gradlew assembleDebug
```

### Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device)
./gradlew connectedAndroidTest
```

## Adding a New Protocol

### Step 1: Create Protocol JSON

Create a new file in `shared/src/commonMain/resources/protocols/`:

```json
{
  "id": "your_protocol_id",
  "name": {
    "en": "Protocol Name",
    "ru": "Название протокола"
  },
  "description": {
    "en": "Description",
    "ru": "Описание"
  },
  "category": "RELAXATION_SLEEP",
  "mechanisms": ["PARASYMPATHETIC_ACTIVATION"],
  "steps": [
    {
      "phase": "INHALE",
      "durationSeconds": 4.0,
      "instruction": {
        "en": "Breathe in",
        "ru": "Вдохните"
      }
    }
  ],
  "sessionDurationMinutes": 5,
  "difficulty": "BEGINNER",
  "safetyRules": {
    "minLevel": 1,
    "contraindications": [],
    "maxHoldForBeginners": 30,
    "hrThreshold": 100,
    "requiresConsent": false
  },
  "animation": {
    "type": "CIRCLE",
    "soundEnabled": true,
    "hapticFeedback": true
  }
}
```

### Step 2: Update Registry

Add entry to `registry.json`:

```json
{
  "id": "your_protocol_id",
  "category": "RELAXATION_SLEEP",
  "mechanisms": ["PARASYMPATHETIC_ACTIVATION"],
  "difficulty": "BEGINNER"
}
```

### Step 3: Add Tests

Create tests in `shared/src/commonTest/kotlin/com/aeris/`.

### Step 4: Update Documentation

Update `docs/algorithms.md` if adding new mechanisms.

## Code Style

### Kotlin

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful names
- Add KDoc for public APIs
- Maximum line length: 120 characters

### Compose

- Keep composables small (< 50 lines)
- Use `Modifier` parameter for composables
- Add `testTag` for testable elements

### Architecture

- Domain layer: Pure Kotlin, no Android dependencies
- Data layer: Repository implementations
- UI layer: ViewModels + Compose

## Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add new breathing animation
fix: correct BCI calculation for edge cases
docs: update algorithm documentation
test: add NSI calculator tests
refactor: simplify protocol loading
```

## Review Process

1. All PRs require at least one review
2. CI must pass (tests, lint)
3. No decrease in test coverage
4. Documentation updated if needed

## Questions?

- Open a [Discussion](https://github.com/your-username/aeris/discussions)
- Tag maintainers in issues

Thank you for contributing! 🙏
