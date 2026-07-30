package com.aeris.domain.usecase

import com.aeris.domain.model.Session
import java.util.Calendar

class CheckBadges {
    operator fun invoke(
        totalSessions: Int,
        streak: Int,
        bci: Float,
        level: Int,
        lastSession: Session
    ): List<String> {
        val newBadges = mutableListOf<String>()
        if (totalSessions == 1) newBadges.add("first_breath")
        if (streak >= 7) newBadges.add("week_warrior")
        if (bci > 60) newBadges.add("co2_warrior")
        val hour = Calendar.getInstance().apply { timeInMillis = lastSession.completedAt }.get(Calendar.HOUR_OF_DAY)
        if (hour in 22..23) newBadges.add("night_owl")
        if (hour in 5..6) newBadges.add("early_bird")
        if (streak >= 30) newBadges.add("month_master")
        if (totalSessions >= 100) newBadges.add("century")
        return newBadges.distinct()
    }
}
