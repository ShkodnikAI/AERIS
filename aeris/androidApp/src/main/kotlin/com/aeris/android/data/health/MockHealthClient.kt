package com.aeris.android.data.health

import com.aeris.domain.model.HealthMetrics
import kotlin.random.Random

/**
 * Mock health client for testing and when Health Connect is unavailable.
 * Provides realistic simulated health data.
 */
class MockHealthClient {
    
    private var baseHeartRate = 70
    private var baseHrv = 50
    private var baseSleepQuality = 0.75f
    
    /**
     * Get simulated health metrics.
     * Adds slight variations to simulate real data.
     */
    fun getMetrics(): HealthMetrics {
        // Add slight random variation to simulate real measurements
        val hrVariation = Random.nextInt(-5, 6)
        val hrvVariation = Random.nextInt(-8, 9)
        val sleepVariation = Random.nextFloat() * 0.1f - 0.05f
        
        return HealthMetrics(
            heartRate = (baseHeartRate + hrVariation).coerceIn(55, 100),
            hrv = (baseHrv + hrvVariation).coerceIn(20, 100),
            sleepQuality = (baseSleepQuality + sleepVariation).coerceIn(0.3f, 1f),
            lastMeasured = System.currentTimeMillis(),
            isFromHealthConnect = false
        )
    }
    
    /**
     * Simulate stress state (higher HR, lower HRV).
     */
    fun simulateStress() {
        baseHeartRate = 85
        baseHrv = 30
        baseSleepQuality = 0.5f
    }
    
    /**
     * Simulate relaxed state (lower HR, higher HRV).
     */
    fun simulateRelaxed() {
        baseHeartRate = 62
        baseHrv = 65
        baseSleepQuality = 0.85f
    }
    
    /**
     * Reset to balanced state.
     */
    fun reset() {
        baseHeartRate = 70
        baseHrv = 50
        baseSleepQuality = 0.75f
    }
    
    /**
     * Get real-time heart rate simulation for active session.
     * Heart rate increases slightly during holds.
     */
    fun getSessionHeartRate(isHolding: Boolean): Int {
        val sessionVariation = if (isHolding) Random.nextInt(5, 15) else Random.nextInt(-3, 5)
        return (baseHeartRate + sessionVariation).coerceIn(55, 120)
    }
}
