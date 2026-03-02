package com.aeris.domain.repository

import com.aeris.domain.model.HealthMetrics

/**
 * Repository interface for health data (HR, HRV).
 * Implementation uses Health Connect on Android with fallback to mock data.
 */
interface HealthRepository {
    
    /**
     * Check if health data source is available.
     */
    suspend fun isHealthConnectAvailable(): Boolean
    
    /**
     * Request health permissions.
     */
    suspend fun requestPermissions(): Boolean
    
    /**
     * Check if permissions are granted.
     */
    suspend fun hasPermissions(): Boolean
    
    /**
     * Get latest health metrics.
     * Returns mock data if Health Connect unavailable.
     */
    suspend fun getLatestMetrics(): HealthMetrics
    
    /**
     * Get average heart rate for time range.
     */
    suspend fun getAverageHeartRate(startTime: Long, endTime: Long): Int?
    
    /**
     * Get HRV (SDNN) for time range.
     */
    suspend fun getHrv(startTime: Long, endTime: Long): Int?
    
    /**
     * Get sleep quality score (0-1) for last night.
     */
    suspend fun getSleepQuality(): Float?
    
    /**
     * Subscribe to real-time heart rate updates during session.
     */
    suspend fun subscribeToHeartRate(onUpdate: (Int) -> Unit)
    
    /**
     * Unsubscribe from heart rate updates.
     */
    fun unsubscribeFromHeartRate()
}
