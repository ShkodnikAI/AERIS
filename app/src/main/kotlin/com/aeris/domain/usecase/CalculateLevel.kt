package com.aeris.domain.usecase

class CalculateLevel {
    operator fun invoke(totalSessions: Int, bci: Float): Int {
        return when {
            totalSessions >= 100 && bci > 75 -> 5
            totalSessions >= 50 && bci > 60 -> 4
            totalSessions >= 25 && bci > 40 -> 3
            totalSessions >= 10 -> 2
            else -> 1
        }
    }
}
