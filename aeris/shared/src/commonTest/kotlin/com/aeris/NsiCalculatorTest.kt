package com.aeris

import com.aeris.ai.NsiCalculator
import com.aeris.domain.model.HealthMetrics
import com.aeris.domain.model.NervousState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NsiCalculatorTest {
    
    private val calculator = NsiCalculator()
    
    @Test
    fun `calculate returns HYPERAROUSAL for stressed state`() {
        val metrics = HealthMetrics(
            heartRate = 95,
            hrv = 25,
            sleepQuality = 0.3f
        )
        val result = calculator.calculate(metrics, hourOfDay = 14)
        assertEquals(NervousState.HYPERAROUSAL, result)
    }
    
    @Test
    fun `calculate returns BALANCED for optimal state`() {
        val metrics = HealthMetrics(
            heartRate = 68,
            hrv = 55,
            sleepQuality = 0.8f
        )
        val result = calculator.calculate(metrics, hourOfDay = 10)
        assertEquals(NervousState.BALANCED, result)
    }
    
    @Test
    fun `calculate returns HYPOAROUSAL for fatigued state`() {
        val metrics = HealthMetrics(
            heartRate = 58,
            hrv = 30,
            sleepQuality = 0.4f
        )
        val result = calculator.calculate(metrics, hourOfDay = 15)
        assertEquals(NervousState.HYPOAROUSAL, result)
    }
    
    @Test
    fun `circadian factor is correct for morning`() {
        val factor = calculator.getCircadianFactor(8)
        assertEquals(1.1f, factor)
    }
    
    @Test
    fun `circadian factor is correct for evening`() {
        val factor = calculator.getCircadianFactor(20)
        assertEquals(0.9f, factor)
    }
    
    @Test
    fun `arousal score is within valid range`() {
        val score = calculator.calculateArousalScore(
            hr = 75,
            hrv = 50,
            sleepQuality = 0.7f
        )
        assertTrue(score >= 0f && score <= 100f)
    }
    
    @Test
    fun `high HR increases arousal score component`() {
        val lowHrScore = calculator.calculateArousalScore(hr = 60, hrv = 50, sleepQuality = 0.7f)
        val highHrScore = calculator.calculateArousalScore(hr = 90, hrv = 50, sleepQuality = 0.7f)
        assertTrue(lowHrScore > highHrScore) // Higher HR = lower calm score
    }
    
    @Test
    fun `high HRV decreases arousal`() {
        val lowHrvScore = calculator.calculateArousalScore(hr = 70, hrv = 30, sleepQuality = 0.7f)
        val highHrvScore = calculator.calculateArousalScore(hr = 70, hrv = 70, sleepQuality = 0.7f)
        assertTrue(highHrvScore > lowHrvScore) // Higher HRV = higher calm score
    }
}
