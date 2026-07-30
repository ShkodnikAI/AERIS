package com.aeris.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class StreakCalculatorTest {
    private val calculator = CalculateStreak()

    @Test
    fun test_empty_returns_0() {
        assertEquals(0, calculator(emptyList()))
    }

    @Test
    fun test_consecutive_days_counted() {
        val today = Calendar.getInstance().timeInMillis
        val yesterday = today - 86400000
        val result = calculator(listOf(today, yesterday))
        assertEquals(2, result)
    }

    @Test
    fun test_gap_resets_streak() {
        val today = Calendar.getInstance().timeInMillis
        val threeDaysAgo = today - 86400000 * 3
        val result = calculator(listOf(today, threeDaysAgo))
        assertEquals(1, result)
    }
}
