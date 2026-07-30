package com.aeris.domain.usecase

import com.aeris.domain.model.*

class CheckSafety {
    operator fun invoke(
        protocol: Protocol,
        userState: UserState,
        userProfile: UserProfile
    ): SafetyResult {
        if (protocol.safetyRules.minLevel > userState.level) {
            return SafetyResult.Blocked("safety_blocked_level")
        }
        val userContras = userProfile.contraindications.toSet()
        val protocolContras = protocol.safetyRules.contraindications.toSet()
        if (userContras.intersect(protocolContras).isNotEmpty()) {
            return SafetyResult.Blocked("safety_blocked_contraindication")
        }
        if (protocol.mechanisms.contains(Mechanism.HYPOXIC_ADAPTATION) && userState.level < 3) {
            return SafetyResult.Blocked("safety_blocked_hypoxic")
        }
        if (protocol.safetyRules.requiresConsent && !userProfile.hasGivenConsent) {
            return SafetyResult.Warning("safety_warning_consent")
        }
        return SafetyResult.Allowed
    }
}
