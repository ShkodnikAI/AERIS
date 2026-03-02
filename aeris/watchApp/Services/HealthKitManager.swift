import Foundation
import HealthKit
import Combine

/// Manages HealthKit integration for real-time heart rate and HRV monitoring
class HealthKitManager: NSObject, ObservableObject {
    
    private let healthStore = HKHealthStore()
    private var heartRateQuery: HKAnchoredObjectQuery?
    private var workoutSession: HKWorkoutSession?
    private var workoutBuilder: HKLiveWorkoutBuilder?
    
    @Published var currentHeartRate: Int?
    @Published var currentHRV: Double?
    @Published var isAuthorized: Bool = false
    @Published var isMonitoring: Bool = false
    @Published var errorMessage: String?
    
    // Heart rate samples for averaging
    private var recentHeartRates: [Int] = []
    private let maxSamples = 5
    
    // Types to read
    private let heartRateType = HKQuantityType.quantityType(forIdentifier: .heartRate)!
    private let hrvType = HKQuantityType.quantityType(forIdentifier: .heartRateVariabilitySDNN)!
    
    override init() {
        super.init()
    }
    
    // MARK: - Authorization
    
    func requestAuthorization() {
        guard HKHealthStore.isHealthDataAvailable() else {
            errorMessage = "HealthKit not available"
            return
        }
        
        let typesToRead: Set<HKObjectType> = [heartRateType, hrvType]
        let typesToWrite: Set<HKSampleType> = [HKQuantityType.workoutType()]
        
        healthStore.requestAuthorization(toShare: typesToWrite, read: typesToRead) { [weak self] success, error in
            DispatchQueue.main.async {
                self?.isAuthorized = success
                if success {
                    self?.startHeartRateMonitoring()
                } else {
                    self?.errorMessage = error?.localizedDescription ?? "Authorization denied"
                }
            }
        }
    }
    
    // MARK: - Heart Rate Monitoring
    
    func startHeartRateMonitoring() {
        guard isAuthorized else { return }
        
        // Stop existing query if any
        stopHeartRateMonitoring()
        
        // Start workout session for continuous monitoring
        startWorkoutSession()
        
        // Query for heart rate updates
        let query = HKAnchoredObjectQuery(
            type: heartRateType,
            predicate: nil,
            anchor: nil,
            limit: HKObjectQueryNoLimit
        ) { [weak self] query, samples, deletedObjects, anchor, error in
            self?.processHeartRateSamples(samples)
        }
        
        query.updateHandler = { [weak self] query, samples, deletedObjects, anchor, error in
            self?.processHeartRateSamples(samples)
        }
        
        heartRateQuery = query
        healthStore.execute(query)
        
        // Also fetch recent HRV
        fetchRecentHRV()
        
        DispatchQueue.main.async {
            self.isMonitoring = true
        }
    }
    
    func stopHeartRateMonitoring() {
        if let query = heartRateQuery {
            healthStore.stop(query)
            heartRateQuery = nil
        }
        
        stopWorkoutSession()
        
        DispatchQueue.main.async {
            self.isMonitoring = false
        }
    }
    
    // MARK: - Workout Session (for background HR)
    
    private func startWorkoutSession() {
        let configuration = HKWorkoutConfiguration()
        configuration.activityType = .mindAndBody
        configuration.locationType = .indoor
        
        do {
            workoutSession = try HKWorkoutSession(healthStore: healthStore, configuration: configuration)
            workoutBuilder = workoutSession?.associatedWorkoutBuilder()
            
            workoutBuilder?.dataSource = HKLiveWorkoutDataSource(
                healthStore: healthStore,
                workoutConfiguration: configuration
            )
            
            workoutSession?.delegate = self
            workoutBuilder?.delegate = self
            
            workoutSession?.startActivity(with: Date())
            workoutBuilder?.beginCollection(withStart: Date()) { success, error in
                if let error = error {
                    print("AERIS Watch: Workout collection error: \(error)")
                }
            }
        } catch {
            errorMessage = "Failed to start workout session: \(error.localizedDescription)"
        }
    }
    
    private func stopWorkoutSession() {
        workoutSession?.end()
        workoutBuilder?.endCollection(withEnd: Date()) { success, error in
            self.workoutBuilder?.finishWorkout { workout, error in
                // Workout finished
            }
        }
    }
    
    // MARK: - Data Processing
    
    private func processHeartRateSamples(_ samples: [HKSample]?) {
        guard let heartRateSamples = samples as? [HKQuantitySample] else { return }
        
        for sample in heartRateSamples {
            let heartRateUnit = HKUnit.count().unitDivided(by: .minute())
            let heartRate = Int(sample.quantity.doubleValue(for: heartRateUnit))
            
            DispatchQueue.main.async {
                self.recentHeartRates.append(heartRate)
                if self.recentHeartRates.count > self.maxSamples {
                    self.recentHeartRates.removeFirst()
                }
                
                // Use average for smoother display
                self.currentHeartRate = self.recentHeartRates.reduce(0, +) / self.recentHeartRates.count
            }
        }
    }
    
    private func fetchRecentHRV() {
        let sortDescriptor = NSSortDescriptor(key: HKSampleSortIdentifierStartDate, ascending: false)
        let query = HKSampleQuery(
            sampleType: hrvType,
            predicate: nil,
            limit: 1,
            sortDescriptors: [sortDescriptor]
        ) { [weak self] query, samples, error in
            guard let hrvSample = samples?.first as? HKQuantitySample else { return }
            
            let hrvValue = hrvSample.quantity.doubleValue(for: HKUnit.secondUnit(with: .milli))
            
            DispatchQueue.main.async {
                self?.currentHRV = hrvValue
            }
        }
        
        healthStore.execute(query)
    }
    
    // MARK: - Data Export for iOS App
    
    func getHealthData() -> [String: Any] {
        var data: [String: Any] = [:]
        
        if let hr = currentHeartRate {
            data["heartRate"] = hr
        }
        if let hrv = currentHRV {
            data["hrv"] = hrv
        }
        data["timestamp"] = Date().timeIntervalSince1970
        data["isMonitoring"] = isMonitoring
        
        return data
    }
}

// MARK: - Workout Session Delegate

extension HealthKitManager: HKWorkoutSessionDelegate {
    func workoutSession(_ workoutSession: HKWorkoutSession, didChangeTo toState: HKWorkoutSessionState, from fromState: HKWorkoutSessionState, date: Date) {
        // Handle state changes
    }
    
    func workoutSession(_ workoutSession: HKWorkoutSession, didFailWithError error: Error) {
        DispatchQueue.main.async {
            self.errorMessage = error.localizedDescription
        }
    }
}

// MARK: - Live Workout Builder Delegate

extension HealthKitManager: HKLiveWorkoutBuilderDelegate {
    func workoutBuilder(_ workoutBuilder: HKLiveWorkoutBuilder, didCollectDataOf collectedTypes: Set<HKSampleType>) {
        for type in collectedTypes {
            if type == heartRateType {
                let statistics = workoutBuilder.statistics(for: heartRateType)
                if let heartRate = statistics?.mostRecentQuantity() {
                    let heartRateUnit = HKUnit.count().unitDivided(by: .minute())
                    let hr = Int(heartRate.doubleValue(for: heartRateUnit))
                    
                    DispatchQueue.main.async {
                        self.currentHeartRate = hr
                    }
                }
            }
        }
    }
    
    func workoutBuilderDidCollectEvent(_ workoutBuilder: HKLiveWorkoutBuilder) {
        // Handle workout events
    }
}
