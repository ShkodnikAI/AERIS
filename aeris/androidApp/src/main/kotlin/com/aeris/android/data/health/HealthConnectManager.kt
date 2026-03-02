package com.aeris.android.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.aeris.domain.model.HealthMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Health Connect integration manager.
 * Provides access to health data with fallback to mock data.
 */
class HealthConnectManager(private val context: Context) {
    
    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.tag("AERIS").w(e, "Health Connect not available")
            null
        }
    }
    
    val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class)
    )
    
    fun isAvailable(): Boolean = healthConnectClient != null
    
    suspend fun hasAllPermissions(): Boolean = withContext(Dispatchers.IO) {
        try {
            val client = healthConnectClient ?: return@withContext false
            val granted = client.permissionController.getGrantedPermissions()
            permissions.all { it in granted }
        } catch (e: Exception) {
            Timber.tag("AERIS").w(e, "Error checking permissions")
            false
        }
    }
    
    suspend fun getLatestMetrics(mockClient: MockHealthClient): HealthMetrics = withContext(Dispatchers.IO) {
        if (!isAvailable() || !hasAllPermissions()) {
            return@withContext mockClient.getMetrics()
        }
        
        try {
            val client = healthConnectClient ?: return@withContext mockClient.getMetrics()
            val now = Instant.now()
            val oneDayAgo = now.minus(24, ChronoUnit.HOURS)
            
            val heartRate = getAverageHeartRate(client, oneDayAgo, now)
            val hrv = getLatestHrv(client, oneDayAgo, now)
            val sleepQuality = getSleepQuality(client)
            
            HealthMetrics(
                heartRate = heartRate ?: mockClient.getMetrics().heartRate,
                hrv = hrv ?: mockClient.getMetrics().hrv,
                sleepQuality = sleepQuality ?: mockClient.getMetrics().sleepQuality,
                lastMeasured = System.currentTimeMillis(),
                isFromHealthConnect = heartRate != null || hrv != null
            )
        } catch (e: Exception) {
            Timber.tag("AERIS").w(e, "Error reading health data, using mock")
            mockClient.getMetrics()
        }
    }
    
    private suspend fun getAverageHeartRate(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): Int? {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            
            val allSamples = response.records.flatMap { it.samples }
            if (allSamples.isNotEmpty()) {
                allSamples.map { it.beatsPerMinute }.average().toInt()
            } else null
        } catch (e: Exception) {
            Timber.tag("AERIS").w(e, "Error reading heart rate")
            null
        }
    }
    
    private suspend fun getLatestHrv(
        client: HealthConnectClient,
        start: Instant,
        end: Instant
    ): Int? {
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateVariabilityRmssdRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )
            
            response.records.lastOrNull()?.heartRateVariabilityMillis?.toInt()
        } catch (e: Exception) {
            Timber.tag("AERIS").w(e, "Error reading HRV")
            null
        }
    }
    
    private suspend fun getSleepQuality(client: HealthConnectClient): Float? {
        return try {
            val now = Instant.now()
            val yesterday = now.minus(24, ChronoUnit.HOURS)
            
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(yesterday, now)
                )
            )
            
            if (response.records.isNotEmpty()) {
                val totalSleepMinutes = response.records.sumOf { record ->
                    ChronoUnit.MINUTES.between(record.startTime, record.endTime)
                }
                // Normalize: 8 hours = 1.0, 4 hours = 0.5, etc.
                (totalSleepMinutes / 480f).coerceIn(0f, 1f)
            } else null
        } catch (e: Exception) {
            Timber.tag("AERIS").w(e, "Error reading sleep data")
            null
        }
    }
}
