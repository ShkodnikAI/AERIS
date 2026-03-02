import Foundation

/// Manual dependency injection for iOS
/// In a full implementation, this would use Koin or manual injection
class IosDependencies {
    
    static let shared = IosDependencies()
    
    private init() {}
    
    // Placeholder for shared module integration
    // In full implementation:
    // - Initialize Koin
    // - Provide iOS-specific implementations
    // - Bridge to shared module
    
    func initialize() {
        print("AERIS iOS dependencies initialized")
    }
}
