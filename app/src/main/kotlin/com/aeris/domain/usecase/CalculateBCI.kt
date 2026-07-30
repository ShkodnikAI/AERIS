package com.aeris.domain.usecase

import kotlin.math.max
import kotlin.math.min

class CalculateBCI {
    operator fun invoke(
        holdDuration: Float,
        maxHoldReference: Float = 40f,
        co2Tolerance: Float = 0.5f,
        rhythmStability: Float = 0.5f,
        progressDelta: Float = 0f
    ): Float {
        val d1 = 0.4
        val d2 = 0.3
        val d3 = 0.15
        val d4 = 0.15

        val relativeHold = (min(holdDuration / maxHoldReference, 2f) / 2).toDouble()
        val normalizedCo2 = co2Tolerance.coerceIn(0f, 1f).toDouble()
        val stabilityScore = (1f - rhythmStability).coerceIn(0f, 1f).toDouble()
        val progressScore = ((progressDelta + 1f) / 2f).coerceIn(0f, 1f).toDouble()

        val bci = ((d1 * relativeHold + d2 * normalizedCo2 + d3 * stabilityScore + d4 * progressScore) * 100)
        return bci.coerceIn(0.0, 100.0).toFloat()
    }
}
