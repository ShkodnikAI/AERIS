# Contributing to AERIS

Thank you for your interest in contributing!

## How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Adding a New Protocol (Phase 2)

1. Add protocol definition to `ProtocolProvider.kt`
2. Add string resources (EN + RU)
3. Add safety rules
4. Write unit tests
5. Update documentation

## Code Style

- Kotlin official code style
- All domain classes must be pure Kotlin (no Android imports)
- All UI strings must be in `strings.xml` with EN + RU translations
- Follow MVVM + Clean Architecture

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
