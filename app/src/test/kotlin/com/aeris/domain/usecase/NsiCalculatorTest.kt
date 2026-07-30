package com.aeris.domain.usecase

import com.aeris.domain.model.NervousState
import org.junit.Assert.assertEquals
import org.junit.Test

class NsiCalculatorTest {
    private val calculator = CalculateNSI()

    @Test
    fun test_highStress_returns_Hyperarousal() {
        val result = calculator(hr = 120, hrv = 15, sleepQuality = 0.1f, hourOfDay = 8)
        assertEquals(NervousState.HYPERAROUSAL, result)
    }

    @Test
    fun test_normal_returns_Balanced() {
        val result = calculator(hr = 70, hrv = 50, sleepQuality = 0.7f, hourOfDay = 12)
        assertEquals(NervousState.BALANCED, result)
    }

    @Test
    fun test_lowStress_returns_Hypoarousal() {
        val result = calculator(hr = 55, hrv = 80, sleepQuality = 0.9f, hourOfDay = 20)
        assertEquals(NervousState.HYPOAROUSAL, result)
    }

    @Test
    fun test_circadian_morning_increases_stress() {
        val morning = calculator(hr = 80, hrv = 40, sleepQuality = 0.5f, hourOfDay = 8)
        val evening = calculator(hr = 80, hrv = 40, sleepQuality = 0.5f, hourOfDay = 20)
        assertEquals(NervousState.HYPERAROUSAL, morning)
        assertEquals(NervousState.BALANCED, evening)
    }
}
