package com.aeris.domain.usecase

import com.aeris.domain.model.Session
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeCheckerTest {
    private val checker = CheckBadges()

    @Test
    fun test_first_session_earns_first_breath() {
        val session = Session(protocolId = "test", completedAt = System.currentTimeMillis(), durationSec = 60, userRating = 5, maxHoldAchieved = 0f, completed = true)
        val result = checker(totalSessions = 1, streak = 0, bci = 0f, level = 1, lastSession = session)
        assertTrue(result.contains("first_breath"))
    }

    @Test
    fun test_7day_streak_earns_week_warrior() {
        val session = Session(protocolId = "test", completedAt = System.currentTimeMillis(), durationSec = 60, userRating = 5, maxHoldAchieved = 0f, completed = true)
        val result = checker(totalSessions = 10, streak = 7, bci = 0f, level = 1, lastSession = session)
        assertTrue(result.contains("week_warrior"))
    }
}
