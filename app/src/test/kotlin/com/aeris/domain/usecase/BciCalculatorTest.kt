package com.aeris.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class BciCalculatorTest {
    private val calculator = CalculateBCI()

    @Test
    fun test_perfect_input_returns_100() {
        val result = calculator(holdDuration = 80f, maxHoldReference = 40f, co2Tolerance = 1f, rhythmStability = 0f, progressDelta = 1f)
        assertEquals(100f, result, 0.1f)
    }

    @Test
    fun test_zero_input_returns_0() {
        val result = calculator(holdDuration = 0f, maxHoldReference = 40f, co2Tolerance = 0f, rhythmStability = 1f, progressDelta = -1f)
        assertEquals(0f, result, 0.1f)
    }

    @Test
    fun test_weights_sum_to_1() {
        val result = calculator(holdDuration = 40f, maxHoldReference = 40f, co2Tolerance = 0.5f, rhythmStability = 0.5f, progressDelta = 0f)
        val expected = ((0.4 * 0.5 + 0.3 * 0.5 + 0.15 * 0.5 + 0.15 * 0.5) * 100).toFloat()
        assertEquals(expected, result, 0.1f)
    }

    @Test
    fun test_result_clamped_0_to_100() {
        val high = calculator(holdDuration = 200f, maxHoldReference = 40f, co2Tolerance = 2f, rhythmStability = -1f, progressDelta = 2f)
        assertEquals(100f, high, 0.1f)
        val low = calculator(holdDuration = -10f, maxHoldReference = 40f, co2Tolerance = -1f, rhythmStability = 2f, progressDelta = -2f)
        assertEquals(0f, low, 0.1f)
    }
}
