package com.aeris.ai

/**
 * BCI Calculator - Breath Capacity Index
 * 
 * Based on research: Respir. Physiol. Neurobiol. 2018
 * DOI: https://doi.org/10.1016/j.resp.2018.05.007
 * 
 * Calculates breathing capacity from hold duration, CO2 tolerance,
 * rhythm stability, and progress over time.
 */
class BciCalculator {
    
    companion object {
        // Dimension weights (sum = 1.0)
        private const val D1_HOLD_DURATION = 0.4f      // Max breath hold relative to reference
        private const val D2_CO2_TOLERANCE = 0.3f      // Tolerance to elevated CO2
        private const val D3_RHYTHM_STABILITY = 0.15f  // Consistency of breathing rhythm
        private const val D4_PROGRESS = 0.15f          // Improvement over time
        
        // Age-based reference values for max breath hold (seconds)
        // Based on clinical literature averages
        private val AGE_REFERENCES = mapOf(
            (18..29) to 40f,
            (30..39) to 38f,
            (40..49) to 35f,
            (50..59) to 32f,
            (60..69) to 28f,
            (70..100) to 25f
        )
        
        const val DEFAULT_REFERENCE = 35f
    }
    
    /**
     * Calculate BCI score (0-100).
     * 
     * @param holdDuration Maximum breath hold achieved (seconds)
     * @param maxHoldReference Reference value for user's age
     * @param co2Tolerance CO2 tolerance score (0.0-1.0), from control pause test
     * @param rhythmStability Standard deviation of breathing intervals (0.0-1.0, lower = better)
     * @param progressDelta Change from previous week (-1.0 to +1.0)
     * @return BCI score 0-100
     */
    fun calculate(
        holdDuration: Float,
        maxHoldReference: Float = DEFAULT_REFERENCE,
        co2Tolerance: Float,
        rhythmStability: Float,
        progressDelta: Float = 0f
    ): Float {
        // Normalize hold duration relative to reference (cap at 2x reference)
        val relativeHold = (holdDuration / maxHoldReference).coerceIn(0f, 2f) / 2f
        
        // CO2 tolerance already 0-1
        val normalizedCo2 = co2Tolerance.coerceIn(0f, 1f)
        
        // Stability: lower deviation = better, so invert
        val stabilityScore = (1f - rhythmStability.coerceIn(0f, 1f))
        
        // Progress: normalize from -1,+1 to 0,1
        val progressScore = ((progressDelta + 1f) / 2f).coerceIn(0f, 1f)
        
        // Weighted sum
        val rawScore = (D1_HOLD_DURATION * relativeHold) +
                      (D2_CO2_TOLERANCE * normalizedCo2) +
                      (D3_RHYTHM_STABILITY * stabilityScore) +
                      (D4_PROGRESS * progressScore)
        
        return (rawScore * 100f).coerceIn(0f, 100f)
    }
    
    /**
     * Get age-appropriate reference value for breath hold.
     */
    fun getReferenceForAge(age: Int): Float {
        return AGE_REFERENCES.entries
            .firstOrNull { age in it.key }
            ?.value ?: DEFAULT_REFERENCE
    }
    
    /**
     * Calculate CO2 tolerance from control pause test.
     * Control pause = time from end of normal exhale to first urge to breathe.
     * 
     * @param controlPauseSeconds Measured control pause in seconds
     * @return Normalized tolerance score 0-1
     */
    fun calculateCo2Tolerance(controlPauseSeconds: Float): Float {
        // Clinical ranges: <10s = poor, 10-20s = fair, 20-40s = good, >40s = excellent
        return when {
            controlPauseSeconds < 10f -> controlPauseSeconds / 20f
            controlPauseSeconds < 20f -> 0.25f + (controlPauseSeconds - 10f) / 40f
            controlPauseSeconds < 40f -> 0.5f + (controlPauseSeconds - 20f) / 40f
            else -> 0.9f + (controlPauseSeconds - 40f).coerceAtMost(20f) / 200f
        }.coerceIn(0f, 1f)
    }
    
    /**
     * Calculate rhythm stability from breathing intervals.
     * 
     * @param intervals List of breath cycle durations in seconds
     * @return Normalized instability score 0-1 (lower = more stable)
     */
    fun calculateRhythmInstability(intervals: List<Float>): Float {
        if (intervals.size < 2) return 0f
        
        val mean = intervals.average().toFloat()
        if (mean == 0f) return 0f
        
        // Calculate coefficient of variation (CV)
        val variance = intervals.map { (it - mean) * (it - mean) }.average().toFloat()
        val stdDev = kotlin.math.sqrt(variance)
        val cv = stdDev / mean
        
        // Normalize CV: 0-0.5 is typical range for breathing
        return (cv / 0.5f).coerceIn(0f, 1f)
    }
    
    /**
     * Get detailed BCI analysis for UI.
     */
    fun getDetailedAnalysis(
        holdDuration: Float,
        age: Int,
        co2Tolerance: Float,
        rhythmStability: Float,
        progressDelta: Float,
        languageCode: String = "en"
    ): BciAnalysis {
        val reference = getReferenceForAge(age)
        val score = calculate(holdDuration, reference, co2Tolerance, rhythmStability, progressDelta)
        
        val level = when {
            score >= 80f -> BciLevel.EXCELLENT
            score >= 60f -> BciLevel.GOOD
            score >= 40f -> BciLevel.MODERATE
            score >= 20f -> BciLevel.DEVELOPING
            else -> BciLevel.BEGINNER
        }
        
        val holdAnalysis = when {
            holdDuration >= reference * 1.5f -> if (languageCode == "ru") "Отличная задержка" else "Excellent hold"
            holdDuration >= reference -> if (languageCode == "ru") "Хорошая задержка" else "Good hold"
            holdDuration >= reference * 0.7f -> if (languageCode == "ru") "Задержка в норме" else "Normal hold"
            else -> if (languageCode == "ru") "Развивайте задержку" else "Developing hold"
        }
        
        return BciAnalysis(
            score = score,
            level = level,
            referenceHold = reference,
            actualHold = holdDuration,
            holdAnalysis = holdAnalysis,
            recommendations = getRecommendations(score, languageCode)
        )
    }
    
    private fun getRecommendations(score: Float, languageCode: String): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (score < 40f) {
            recommendations.add(
                if (languageCode == "ru") "Практикуйте базовые дыхательные упражнения ежедневно"
                else "Practice basic breathing exercises daily"
            )
        }
        if (score < 60f) {
            recommendations.add(
                if (languageCode == "ru") "Попробуйте упражнения на CO2-толерантность"
                else "Try CO2 tolerance exercises"
            )
        }
        if (score >= 60f) {
            recommendations.add(
                if (languageCode == "ru") "Готовы к продвинутым протоколам"
                else "Ready for advanced protocols"
            )
        }
        
        return recommendations
    }
}

/**
 * BCI level classification.
 */
enum class BciLevel {
    BEGINNER,
    DEVELOPING,
    MODERATE,
    GOOD,
    EXCELLENT;
    
    fun getDisplayName(languageCode: String): String = when (this) {
        BEGINNER -> if (languageCode == "ru") "Начинающий" else "Beginner"
        DEVELOPING -> if (languageCode == "ru") "Развивающийся" else "Developing"
        MODERATE -> if (languageCode == "ru") "Средний" else "Moderate"
        GOOD -> if (languageCode == "ru") "Хороший" else "Good"
        EXCELLENT -> if (languageCode == "ru") "Отличный" else "Excellent"
    }
}

/**
 * Detailed BCI analysis for display.
 */
data class BciAnalysis(
    val score: Float,
    val level: BciLevel,
    val referenceHold: Float,
    val actualHold: Float,
    val holdAnalysis: String,
    val recommendations: List<String>
)
