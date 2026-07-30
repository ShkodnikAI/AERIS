package com.aeris.domain.usecase

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class CalculateStreak {
    operator fun invoke(sessionTimestamps: List<Long>): Int {
        if (sessionTimestamps.isEmpty()) return 0
        val today = LocalDate.now(ZoneId.systemDefault())
        val dates = sessionTimestamps
            .map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
            .distinct()
            .sortedDescending()

        var streak = 0
        var expectedDate = today
        for (date in dates) {
            if (date == expectedDate || date == expectedDate.minusDays(1)) {
                if (date == expectedDate) {
                    streak++
                    expectedDate = date.minusDays(1)
                } else if (date == expectedDate.minusDays(1)) {
                    streak++
                    expectedDate = date.minusDays(1)
                }
            } else {
                break
            }
        }
        return streak
    }
}
