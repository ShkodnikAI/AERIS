import SwiftUI

struct ContentView: View {
    var body: some View {
        ZStack {
            Color(red: 0.06, green: 0.06, blue: 0.08)
                .ignoresSafeArea()
            
            VStack(spacing: 24) {
                Spacer()
                
                // Logo
                Circle()
                    .fill(Color(red: 0.05, green: 0.45, blue: 0.47))
                    .frame(width: 120, height: 120)
                    .overlay(
                        Text("A")
                            .font(.system(size: 60, weight: .light))
                            .foregroundColor(.white)
                    )
                
                Text("AERIS")
                    .font(.system(size: 36, weight: .bold))
                    .foregroundColor(.white)
                
                Text("Breathe Better. Live Better.")
                    .font(.system(size: 16))
                    .foregroundColor(.gray)
                
                Spacer()
                
                // Placeholder message
                VStack(spacing: 12) {
                    Image(systemName: "wrench.and.screwdriver")
                        .font(.system(size: 40))
                        .foregroundColor(.gray)
                    
                    Text("iOS version coming soon")
                        .font(.system(size: 18, weight: .medium))
                        .foregroundColor(.gray)
                    
                    Text("This is a KMP placeholder.\nFull iOS implementation requires\nXcode and CocoaPods setup.")
                        .font(.system(size: 14))
                        .foregroundColor(.gray.opacity(0.7))
                        .multilineTextAlignment(.center)
                }
                .padding()
                .background(Color.white.opacity(0.05))
                .cornerRadius(16)
                
                Spacer()
            }
            .padding()
        }
    }
}

#Preview {
    ContentView()
}
