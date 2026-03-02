import SwiftUI

@main
struct AerisWatchApp: App {
    @StateObject private var healthManager = HealthKitManager()
    @StateObject private var connectivityManager = WatchConnectivityManager()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(healthManager)
                .environmentObject(connectivityManager)
        }
    }
}
