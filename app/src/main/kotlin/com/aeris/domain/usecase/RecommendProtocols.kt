package com.aeris.domain.usecase

import com.aeris.domain.model.*

class RecommendProtocols(private val checkSafety: CheckSafety) {
    operator fun invoke(
        userState: UserState,
        userProfile: UserProfile,
        protocols: List<Protocol>,
        hourOfDay: Int
    ): List<Protocol> {
        val completedIds = emptySet<String>() // TODO: inject from session history

        return protocols
            .filter { protocol ->
                val safety = checkSafety(protocol, userState, userProfile)
                safety !is SafetyResult.Blocked
            }
            .filter { protocol ->
                when (userState.nsi) {
                    NervousState.HYPERAROUSAL -> !protocol.mechanisms.contains(Mechanism.SYMPATHETIC)
                    NervousState.HYPOAROUSAL -> !protocol.mechanisms.contains(Mechanism.PARASYMPATHETIC)
                    NervousState.BALANCED -> true
                }
            }
            .map { protocol ->
                var score = 0.0
                val categoryMatch = when {
                    hourOfDay in 6..12 && protocol.category == Category.ENERGY_FOCUS -> true
                    hourOfDay in 18..23 && protocol.category == Category.RELAXATION_SLEEP -> true
                    else -> false
                }
                if (categoryMatch) score += 0.2
                if (userState.bci < 40f && protocol.difficulty == Difficulty.BEGINNER) score += 0.1
                if (!completedIds.contains(protocol.id)) score += 0.05
                protocol to score
            }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(5)
    }
}
