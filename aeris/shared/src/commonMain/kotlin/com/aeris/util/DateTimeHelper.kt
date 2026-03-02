package com.aeris.util

import kotlinx.datetime.*

/**
 * Platform-agnostic date/time utilities.
 */
object DateTimeHelper {
    
    /**
     * Get current timestamp in milliseconds.
     */
    fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
    
    /**
     * Get current hour of day (0-23).
     */
    fun currentHourOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Int {
        return Clock.System.now()
            .toLocalDateTime(timeZone)
            .hour
    }
    
    /**
     * Format duration in seconds to mm:ss string.
     */
    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
    }
    
    /**
     * Format duration in seconds to human-readable string.
     */
    fun formatDurationHuman(seconds: Int, languageCode: String = "en"): String {
        val mins = seconds / 60
        val secs = seconds % 60
        
        return when {
            mins == 0 -> if (languageCode == "ru") "$secs сек" else "${secs}s"
            secs == 0 -> if (languageCode == "ru") "$mins мин" else "${mins}m"
            else -> if (languageCode == "ru") "$mins мин $secs сек" else "${mins}m ${secs}s"
        }
    }
    
    /**
     * Check if timestamp is from today.
     */
    fun isToday(timestampMillis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val dateOfTimestamp = instant.toLocalDateTime(timeZone).date
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        return dateOfTimestamp == today
    }
    
    /**
     * Check if timestamp is from yesterday.
     */
    fun isYesterday(timestampMillis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val dateOfTimestamp = instant.toLocalDateTime(timeZone).date
        val yesterday = Clock.System.now()
            .toLocalDateTime(timeZone)
            .date
            .minus(1, DateTimeUnit.DAY)
        return dateOfTimestamp == yesterday
    }
    
    /**
     * Get start of day timestamp for given date.
     */
    fun startOfDay(
        daysAgo: Int = 0,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): Long {
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val targetDate = today.minus(daysAgo, DateTimeUnit.DAY)
        return targetDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }
    
    /**
     * Format date for display.
     */
    fun formatDate(
        timestampMillis: Long,
        languageCode: String = "en",
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): String {
        val instant = Instant.fromEpochMilliseconds(timestampMillis)
        val localDate = instant.toLocalDateTime(timeZone).date
        
        return when {
            isToday(timestampMillis, timeZone) -> if (languageCode == "ru") "Сегодня" else "Today"
            isYesterday(timestampMillis, timeZone) -> if (languageCode == "ru") "Вчера" else "Yesterday"
            else -> {
                val month = localDate.month.name.lowercase().take(3)
                "${localDate.dayOfMonth} $month"
            }
        }
    }
    
    /**
     * Calculate days between two timestamps.
     */
    fun daysBetween(
        startMillis: Long,
        endMillis: Long,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): Int {
        val startDate = Instant.fromEpochMilliseconds(startMillis)
            .toLocalDateTime(timeZone).date
        val endDate = Instant.fromEpochMilliseconds(endMillis)
            .toLocalDateTime(timeZone).date
        return startDate.daysUntil(endDate)
    }
}
