import SwiftUI

struct ContentView: View {
    @EnvironmentObject var healthManager: HealthKitManager
    @EnvironmentObject var connectivityManager: WatchConnectivityManager
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                // Heart Rate Display
                HeartRateView(heartRate: healthManager.currentHeartRate)
                
                // Session Status
                if connectivityManager.isSessionActive {
                    SessionStatusView(
                        phase: connectivityManager.currentPhase,
                        progress: connectivityManager.phaseProgress
                    )
                } else {
                    QuickStartView()
                }
                
                // HRV if available
                if let hrv = healthManager.currentHRV {
                    HRVView(hrv: hrv)
                }
            }
            .padding()
            .navigationTitle("AERIS")
            .navigationBarTitleDisplayMode(.inline)
            .onAppear {
                healthManager.requestAuthorization()
            }
        }
    }
}

// MARK: - Heart Rate View
struct HeartRateView: View {
    let heartRate: Int?
    
    @State private var isAnimating = false
    
    var body: some View {
        VStack(spacing: 4) {
            ZStack {
                // Pulsing background
                Circle()
                    .fill(Color.red.opacity(0.2))
                    .frame(width: 80, height: 80)
                    .scaleEffect(isAnimating ? 1.1 : 1.0)
                    .animation(
                        .easeInOut(duration: 0.8)
                        .repeatForever(autoreverses: true),
                        value: isAnimating
                    )
                
                // Heart icon
                Image(systemName: "heart.fill")
                    .font(.system(size: 32))
                    .foregroundColor(.red)
                    .scaleEffect(isAnimating ? 1.05 : 1.0)
                    .animation(
                        .easeInOut(duration: 0.5)
                        .repeatForever(autoreverses: true),
                        value: isAnimating
                    )
            }
            .onAppear { isAnimating = true }
            
            if let hr = heartRate {
                Text("\(hr)")
                    .font(.system(size: 36, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
                
                Text("BPM")
                    .font(.caption2)
                    .foregroundColor(.gray)
            } else {
                Text("--")
                    .font(.system(size: 36, weight: .bold))
                    .foregroundColor(.gray)
                
                Text("Measuring...")
                    .font(.caption2)
                    .foregroundColor(.gray)
            }
        }
    }
}

// MARK: - Session Status View
struct SessionStatusView: View {
    let phase: String
    let progress: Double
    
    var phaseColor: Color {
        switch phase.lowercased() {
        case "inhale": return .teal
        case "hold", "hold_in", "hold_out": return .orange
        case "exhale": return .purple
        default: return .blue
        }
    }
    
    var body: some View {
        VStack(spacing: 8) {
            // Phase indicator
            Text(phase.capitalized)
                .font(.headline)
                .foregroundColor(phaseColor)
            
            // Progress ring
            ZStack {
                Circle()
                    .stroke(phaseColor.opacity(0.3), lineWidth: 6)
                
                Circle()
                    .trim(from: 0, to: progress)
                    .stroke(phaseColor, style: StrokeStyle(lineWidth: 6, lineCap: .round))
                    .rotationEffect(.degrees(-90))
                    .animation(.linear(duration: 0.1), value: progress)
            }
            .frame(width: 50, height: 50)
            
            Text("Session Active")
                .font(.caption2)
                .foregroundColor(.green)
        }
    }
}

// MARK: - Quick Start View
struct QuickStartView: View {
    @EnvironmentObject var connectivityManager: WatchConnectivityManager
    
    var body: some View {
        VStack(spacing: 8) {
            Text("Ready to breathe")
                .font(.caption)
                .foregroundColor(.gray)
            
            Button(action: {
                connectivityManager.sendQuickStartRequest()
            }) {
                HStack {
                    Image(systemName: "play.fill")
                    Text("Start")
                }
                .font(.caption)
            }
            .buttonStyle(.borderedProminent)
            .tint(.teal)
        }
    }
}

// MARK: - HRV View
struct HRVView: View {
    let hrv: Double
    
    var hrvStatus: (String, Color) {
        switch hrv {
        case ..<30: return ("Stressed", .red)
        case 30..<50: return ("Normal", .yellow)
        default: return ("Relaxed", .green)
        }
    }
    
    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: "waveform.path.ecg")
                .font(.caption2)
                .foregroundColor(hrvStatus.1)
            
            Text("HRV: \(Int(hrv))ms")
                .font(.caption2)
                .foregroundColor(.gray)
            
            Text(hrvStatus.0)
                .font(.caption2)
                .foregroundColor(hrvStatus.1)
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(HealthKitManager())
        .environmentObject(WatchConnectivityManager())
}
