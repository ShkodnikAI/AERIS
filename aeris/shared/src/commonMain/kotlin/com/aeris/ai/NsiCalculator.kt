package com.aeris.ai

import com.aeris.domain.model.HealthMetrics
import com.aeris.domain.model.NervousState

/**
 * NSI Calculator - Nervous System Index
 * 
 * Based on research: Front. Physiol. 2021
 * DOI: https://doi.org/10.3389/fphys.2021.625789
 * 
 * Calculates the autonomic nervous system state from health metrics,
 * considering circadian rhythm and physiological indicators.
 */
class NsiCalculator {
    
    companion object {
        // Weights derived from research literature
        private const val HR_WEIGHT = -0.3f      // Higher HR = more arousal (negative correlation with calm)
        private const val HRV_WEIGHT = 0.5f      // Higher HRV = better parasympathetic tone
        private const val SLEEP_WEIGHT = 20f    // Good sleep = better baseline state
        
        // Thresholds for state classification
        private const val HYPERAROUSAL_THRESHOLD = 70f
        private const val HYPOAROUSAL_THRESHOLD = 30f
        
        // Circadian factors (based on cortisol curve research)
        private val CIRCADIAN_FACTORS = mapOf(
            (6..10) to 1.1f,    // Morning: natural activation
            (11..14) to 1.0f,   // Midday: baseline
            (15..17) to 0.95f,  // Afternoon: slight dip
            (18..22) to 0.9f,   // Evening: natural relaxation
            (23..23) to 0.85f,  // Late night: should be resting
            (0..5) to 0.8f      // Night: deep rest period
        )
    }
    
    /**
     * Calculate NSI and determine nervous system state.
     * 
     * @param metrics Current health metrics (HR, HRV, sleep quality)
     * @param hourOfDay Current hour (0-23) for circadian adjustment
     * @return NervousState indicating current autonomic balance
     */
    fun calculate(metrics: HealthMetrics, hourOfDay: Int): NervousState {
        val circadianFactor = getCircadianFactor(hourOfDay)
        
        val arousalScore = calculateArousalScore(
            hr = metrics.heartRate,
            hrv = metrics.hrv,
            sleepQuality = metrics.sleepQuality,
            circadianFactor = circadianFactor
        )
        
        return classifyState(arousalScore)
    }
    
    /**
     * Calculate raw arousal score.
     * Higher score = more parasympathetic (calm)
     * Lower score = more sympathetic (stressed)
     */
    fun calculateArousalScore(
        hr: Int,
        hrv: Int,
        sleepQuality: Float,
        circadianFactor: Float = 1.0f
    ): Float {
        // Normalize HR: typical range 50-100 BPM
        val normalizedHr = ((hr - 50).toFloat() / 50f).coerceIn(0f, 1f)
        
        // Normalize HRV: typical SDNN range 20-100ms
        val normalizedHrv = ((hrv - 20).toFloat() / 80f).coerceIn(0f, 1f)
        
        // Combine factors with circadian adjustment
        return ((normalizedHr * HR_WEIGHT * 100f) + 
                (normalizedHrv * HRV_WEIGHT * 100f) + 
                (sleepQuality * SLEEP_WEIGHT * circadianFactor) + 50f)
            .coerceIn(0f, 100f)
    }
    
    /**
     * Get circadian rhythm factor for given hour.
     */
    fun getCircadianFactor(hourOfDay: Int): Float {
        val normalizedHour = hourOfDay.coerceIn(0, 23)
        return CIRCADIAN_FACTORS.entries
            .firstOrNull { normalizedHour in it.key }
            ?.value ?: 1.0f
    }
    
    /**
     * Classify arousal score into discrete state.
     */
    private fun classifyState(arousalScore: Float): NervousState {
        return when {
            arousalScore > HYPERAROUSAL_THRESHOLD -> NervousState.HYPERAROUSAL
            arousalScore < HYPOAROUSAL_THRESHOLD -> NervousState.HYPOAROUSAL
            else -> NervousState.BALANCED
        }
    }
    
    /**
     * Get detailed analysis for UI display.
     */
    fun getDetailedAnalysis(
        metrics: HealthMetrics,
        hourOfDay: Int,
        languageCode: String = "en"
    ): NsiAnalysis {
        val circadianFactor = getCircadianFactor(hourOfDay)
        val arousalScore = calculateArousalScore(
            hr = metrics.heartRate,
            hrv = metrics.hrv,
            sleepQuality = metrics.sleepQuality,
            circadianFactor = circadianFactor
        )
        val state = classifyState(arousalScore)
        
        val hrContribution = when {
            metrics.heartRate < 60 -> if (languageCode == "ru") "ЧСС в норме (низкая)" else "HR normal (low)"
            metrics.heartRate < 80 -> if (languageCode == "ru") "ЧСС оптимальная" else "HR optimal"
            metrics.heartRate < 100 -> if (languageCode == "ru") "ЧСС повышена" else "HR elevated"
            else -> if (languageCode == "ru") "ЧСС высокая" else "HR high"
        }
        
        val hrvContribution = when {
            metrics.hrv < 30 -> if (languageCode == "ru") "ВСР снижена (стресс)" else "HRV low (stress)"
            metrics.hrv < 50 -> if (languageCode == "ru") "ВСР умеренная" else "HRV moderate"
            else -> if (languageCode == "ru") "ВСР хорошая" else "HRV good"
        }
        
        return NsiAnalysis(
            score = arousalScore,
            state = state,
            circadianFactor = circadianFactor,
            hrContribution = hrContribution,
            hrvContribution = hrvContribution
        )
    }
}

/**
 * Detailed NSI analysis for display.
 */
data class NsiAnalysis(
    val score: Float,
    val state: NervousState,
    val circadianFactor: Float,
    val hrContribution: String,
    val hrvContribution: String
)
