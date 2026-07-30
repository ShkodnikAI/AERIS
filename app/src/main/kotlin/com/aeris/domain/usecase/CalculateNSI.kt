package com.aeris.domain.usecase

import com.aeris.domain.model.NervousState
import java.util.Calendar

class CalculateNSI {
    operator fun invoke(
        hr: Int,
        hrv: Int,
        sleepQuality: Float,
        hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ): NervousState {
        val circadianFactor = when (hourOfDay) {
            in 6..10 -> 5.0
            in 18..22 -> -5.0
            else -> 0.0
        }
        val stressScore =
            (hr / 200.0) * 40.0 +
            ((100 - hrv) / 100.0) * 30.0 +
            ((1.0 - sleepQuality) * 20.0) +
            circadianFactor

        return when {
            stressScore > 65 -> NervousState.HYPERAROUSAL
            stressScore < 35 -> NervousState.HYPOAROUSAL
            else -> NervousState.BALANCED
        }
    }
}
