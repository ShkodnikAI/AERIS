package com.aeris

import com.aeris.ai.BciCalculator
import com.aeris.ai.BciLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BciCalculatorTest {
    
    private val calculator = BciCalculator()
    
    @Test
    fun `calculate returns score in valid range`() {
        val score = calculator.calculate(
            holdDuration = 30f,
            maxHoldReference = 35f,
            co2Tolerance = 0.7f,
            rhythmStability = 0.2f,
            progressDelta = 0.1f
        )
        assertTrue(score >= 0f && score <= 100f)
    }
    
    @Test
    fun `weights sum to 1_0`() {
        // Verify by checking that max possible score approaches 100
        val maxScore = calculator.calculate(
            holdDuration = 70f, // 2x reference
            maxHoldReference = 35f,
            co2Tolerance = 1.0f,
            rhythmStability = 0f, // Perfect stability
            progressDelta = 1.0f
        )
        assertTrue(maxScore <= 100f)
        assertTrue(maxScore >= 90f) // Should be close to max
    }
    
    @Test
    fun `excellent performance yields high score`() {
        val score = calculator.calculate(
            holdDuration = 50f,
            maxHoldReference = 35f,
            co2Tolerance = 0.9f,
            rhythmStability = 0.1f,
            progressDelta = 0.5f
        )
        assertTrue(score >= 70f)
    }
    
    @Test
    fun `beginner performance yields lower score`() {
        val score = calculator.calculate(
            holdDuration = 15f,
            maxHoldReference = 35f,
            co2Tolerance = 0.3f,
            rhythmStability = 0.5f,
            progressDelta = 0f
        )
        assertTrue(score < 50f)
    }
    
    @Test
    fun `age reference values are correct`() {
        assertEquals(40f, calculator.getReferenceForAge(25))
        assertEquals(35f, calculator.getReferenceForAge(45))
        assertEquals(28f, calculator.getReferenceForAge(65))
    }
    
    @Test
    fun `CO2 tolerance calculation is normalized`() {
        val lowTolerance = calculator.calculateCo2Tolerance(5f)
        val highTolerance = calculator.calculateCo2Tolerance(45f)
        
        assertTrue(lowTolerance < 0.5f)
        assertTrue(highTolerance > 0.8f)
        assertTrue(lowTolerance >= 0f && lowTolerance <= 1f)
        assertTrue(highTolerance >= 0f && highTolerance <= 1f)
    }
    
    @Test
    fun `rhythm instability calculation works correctly`() {
        val stableRhythm = calculator.calculateRhythmInstability(listOf(4f, 4f, 4f, 4f))
        val unstableRhythm = calculator.calculateRhythmInstability(listOf(2f, 6f, 3f, 7f))
        
        assertTrue(stableRhythm < unstableRhythm)
        assertTrue(stableRhythm >= 0f)
    }
    
    @Test
    fun `detailed analysis returns correct level`() {
        val analysis = calculator.getDetailedAnalysis(
            holdDuration = 45f,
            age = 30,
            co2Tolerance = 0.8f,
            rhythmStability = 0.15f,
            progressDelta = 0.3f,
            languageCode = "en"
        )
        
        assertTrue(analysis.score >= 60f)
        assertTrue(analysis.level in listOf(BciLevel.GOOD, BciLevel.EXCELLENT))
    }
}
