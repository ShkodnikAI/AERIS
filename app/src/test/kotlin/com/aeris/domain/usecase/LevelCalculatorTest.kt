package com.aeris.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class LevelCalculatorTest {
    private val calculator = CalculateLevel()

    @Test
    fun test_0_sessions_returns_level1() {
        assertEquals(1, calculator(0, 0f))
    }

    @Test
    fun test_10_sessions_returns_level2() {
        assertEquals(2, calculator(10, 30f))
    }

    @Test
    fun test_100_sessions_bci80_returns_level5() {
        assertEquals(5, calculator(100, 80f))
    }
}
