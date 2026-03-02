package com.aeris.domain.model

import kotlinx.serialization.Serializable

/**
 * Safety rule configuration for protocol validation.
 */
@Serializable
data class SafetyRule(
    val id: String,
    val name: LocalizedString,
    val description: LocalizedString,
    val condition: SafetyCondition,
    val action: SafetyAction,
    val priority: Int = 0
)

@Serializable
sealed class SafetyCondition {
    @Serializable
    data class HeartRateAbove(val threshold: Int) : SafetyCondition()
    
    @Serializable
    data class HrvBelow(val threshold: Int) : SafetyCondition()
    
    @Serializable
    data class UserLevelBelow(val minLevel: Int) : SafetyCondition()
    
    @Serializable
    data class HasContraindication(val contraindication: Contraindication) : SafetyCondition()
    
    @Serializable
    data class ConsecutiveErrors(val count: Int) : SafetyCondition()
    
    @Serializable
    data object RequiresAdvancedConsent : SafetyCondition()
}

@Serializable
sealed class SafetyAction {
    @Serializable
    data object BlockProtocol : SafetyAction()
    
    @Serializable
    data class ReduceHoldDuration(val maxSeconds: Int) : SafetyAction()
    
    @Serializable
    data object PauseSession : SafetyAction()
    
    @Serializable
    data class ShowWarning(val message: LocalizedString) : SafetyAction()
    
    @Serializable
    data object SuggestEmergencyProtocol : SafetyAction()
    
    @Serializable
    data object RequireConsentDialog : SafetyAction()
}

/**
 * Safety check result for UI.
 */
data class SafetyCheckResult(
    val isAllowed: Boolean,
    val warnings: List<SafetyWarning> = emptyList(),
    val modifications: List<SafetyModification> = emptyList(),
    val blockedReason: String? = null
)

data class SafetyWarning(
    val message: LocalizedString,
    val severity: WarningSeverity
)

enum class WarningSeverity {
    INFO,
    CAUTION,
    CRITICAL
}

data class SafetyModification(
    val description: LocalizedString,
    val originalValue: String,
    val modifiedValue: String
)
