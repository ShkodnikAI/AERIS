import Foundation
import WatchConnectivity
import Combine

/// Manages Watch <-> iOS app communication
class WatchConnectivityManager: NSObject, ObservableObject {
    
    @Published var isSessionActive: Bool = false
    @Published var currentPhase: String = ""
    @Published var phaseProgress: Double = 0.0
    @Published var protocolName: String = ""
    @Published var isReachable: Bool = false
    
    private var session: WCSession?
    
    override init() {
        super.init()
        
        if WCSession.isSupported() {
            session = WCSession.default
            session?.delegate = self
            session?.activate()
        }
    }
    
    // MARK: - Send to iOS
    
    /// Send heart rate data to iOS app
    func sendHeartRateUpdate(heartRate: Int, hrv: Double?) {
        guard let session = session, session.isReachable else { return }
        
        var message: [String: Any] = [
            "type": "heartRateUpdate",
            "heartRate": heartRate,
            "timestamp": Date().timeIntervalSince1970
        ]
        
        if let hrv = hrv {
            message["hrv"] = hrv
        }
        
        session.sendMessage(message, replyHandler: nil) { error in
            print("AERIS Watch: Failed to send HR: \(error)")
        }
    }
    
    /// Request quick start from watch
    func sendQuickStartRequest() {
        guard let session = session, session.isReachable else {
            print("AERIS Watch: iOS app not reachable")
            return
        }
        
        let message: [String: Any] = [
            "type": "quickStart",
            "timestamp": Date().timeIntervalSince1970
        ]
        
        session.sendMessage(message, replyHandler: { response in
            print("AERIS Watch: Quick start response: \(response)")
        }) { error in
            print("AERIS Watch: Quick start failed: \(error)")
        }
    }
    
    /// Send session completion to iOS
    func sendSessionComplete(averageHeartRate: Int, duration: TimeInterval) {
        guard let session = session else { return }
        
        let data: [String: Any] = [
            "type": "sessionComplete",
            "averageHeartRate": averageHeartRate,
            "duration": duration,
            "timestamp": Date().timeIntervalSince1970
        ]
        
        // Use transferUserInfo for guaranteed delivery
        session.transferUserInfo(data)
    }
}

// MARK: - WCSessionDelegate

extension WatchConnectivityManager: WCSessionDelegate {
    
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        DispatchQueue.main.async {
            self.isReachable = session.isReachable
        }
    }
    
    func sessionReachabilityDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isReachable = session.isReachable
        }
    }
    
    // Receive messages from iOS
    func session(_ session: WCSession, didReceiveMessage message: [String : Any]) {
        handleMessage(message)
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String : Any], replyHandler: @escaping ([String : Any]) -> Void) {
        handleMessage(message)
        replyHandler(["status": "received"])
    }
    
    // Receive application context updates
    func session(_ session: WCSession, didReceiveApplicationContext applicationContext: [String : Any]) {
        handleMessage(applicationContext)
    }
    
    private func handleMessage(_ message: [String: Any]) {
        guard let type = message["type"] as? String else { return }
        
        DispatchQueue.main.async {
            switch type {
            case "sessionStart":
                self.isSessionActive = true
                self.protocolName = message["protocolName"] as? String ?? ""
                
            case "sessionUpdate":
                self.currentPhase = message["phase"] as? String ?? ""
                self.phaseProgress = message["progress"] as? Double ?? 0.0
                
            case "sessionEnd":
                self.isSessionActive = false
                self.currentPhase = ""
                self.phaseProgress = 0.0
                
            case "requestHeartRate":
                // iOS requesting current HR - handled by HealthKitManager
                break
                
            default:
                break
            }
        }
    }
}
