import Foundation
import WatchConnectivity
import Combine

/// iOS-side Watch Connectivity manager for receiving HR data from Apple Watch
class WatchConnectivityManager: NSObject, ObservableObject {
    
    static let shared = WatchConnectivityManager()
    
    @Published var isWatchReachable: Bool = false
    @Published var latestHeartRate: Int?
    @Published var latestHRV: Double?
    @Published var lastUpdateTime: Date?
    @Published var isWatchAppInstalled: Bool = false
    
    // Callbacks for session updates
    var onHeartRateUpdate: ((Int, Double?) -> Void)?
    var onQuickStartRequested: (() -> Void)?
    var onSessionComplete: ((Int, TimeInterval) -> Void)?
    
    private var session: WCSession?
    
    private override init() {
        super.init()
        
        if WCSession.isSupported() {
            session = WCSession.default
            session?.delegate = self
            session?.activate()
        }
    }
    
    // MARK: - Send to Watch
    
    /// Notify watch that session started
    func sendSessionStart(protocolName: String) {
        sendMessage([
            "type": "sessionStart",
            "protocolName": protocolName,
            "timestamp": Date().timeIntervalSince1970
        ])
    }
    
    /// Send breathing phase update to watch
    func sendSessionUpdate(phase: String, progress: Double) {
        // Use application context for guaranteed delivery
        try? session?.updateApplicationContext([
            "type": "sessionUpdate",
            "phase": phase,
            "progress": progress,
            "timestamp": Date().timeIntervalSince1970
        ])
    }
    
    /// Notify watch that session ended
    func sendSessionEnd() {
        sendMessage([
            "type": "sessionEnd",
            "timestamp": Date().timeIntervalSince1970
        ])
    }
    
    /// Request heart rate from watch
    func requestHeartRate() {
        sendMessage([
            "type": "requestHeartRate",
            "timestamp": Date().timeIntervalSince1970
        ])
    }
    
    private func sendMessage(_ message: [String: Any]) {
        guard let session = session, session.isReachable else { return }
        
        session.sendMessage(message, replyHandler: nil) { error in
            print("AERIS iOS: Failed to send to watch: \(error)")
        }
    }
    
    // MARK: - Status
    
    var watchStatus: String {
        if !WCSession.isSupported() {
            return "Watch not supported"
        }
        if !isWatchAppInstalled {
            return "AERIS Watch app not installed"
        }
        if !isWatchReachable {
            return "Watch not reachable"
        }
        return "Connected"
    }
}

// MARK: - WCSessionDelegate

extension WatchConnectivityManager: WCSessionDelegate {
    
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        DispatchQueue.main.async {
            self.isWatchReachable = session.isReachable
            self.isWatchAppInstalled = session.isWatchAppInstalled
        }
    }
    
    func sessionDidBecomeInactive(_ session: WCSession) {
        // Handle session becoming inactive
    }
    
    func sessionDidDeactivate(_ session: WCSession) {
        // Reactivate session
        session.activate()
    }
    
    func sessionReachabilityDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isWatchReachable = session.isReachable
        }
    }
    
    func sessionWatchStateDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isWatchAppInstalled = session.isWatchAppInstalled
        }
    }
    
    // Receive messages from Watch
    func session(_ session: WCSession, didReceiveMessage message: [String : Any]) {
        handleWatchMessage(message)
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String : Any], replyHandler: @escaping ([String : Any]) -> Void) {
        handleWatchMessage(message)
        replyHandler(["status": "received"])
    }
    
    // Receive user info transfers (guaranteed delivery)
    func session(_ session: WCSession, didReceiveUserInfo userInfo: [String : Any] = [:]) {
        handleWatchMessage(userInfo)
    }
    
    private func handleWatchMessage(_ message: [String: Any]) {
        guard let type = message["type"] as? String else { return }
        
        DispatchQueue.main.async {
            switch type {
            case "heartRateUpdate":
                if let hr = message["heartRate"] as? Int {
                    self.latestHeartRate = hr
                    self.latestHRV = message["hrv"] as? Double
                    self.lastUpdateTime = Date()
                    self.onHeartRateUpdate?(hr, self.latestHRV)
                }
                
            case "quickStart":
                self.onQuickStartRequested?()
                
            case "sessionComplete":
                if let avgHR = message["averageHeartRate"] as? Int,
                   let duration = message["duration"] as? TimeInterval {
                    self.onSessionComplete?(avgHR, duration)
                }
                
            default:
                break
            }
        }
    }
}
