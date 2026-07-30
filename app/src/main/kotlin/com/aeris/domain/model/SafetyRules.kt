package com.aeris.domain.model

data class SafetyRules(
    val minLevel: Int = 1,
    val contraindications: List<Contraindication> = emptyList(),
    val maxHoldForBeginners: Int = 30,
    val hrThreshold: Int = 100,
    val requiresConsent: Boolean = false
)
