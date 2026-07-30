package com.aeris.domain.model

data class UserProfile(
    val heartRate: Int = 70,
    val hrv: Int = 50,
    val sleepQuality: Float = 0.7f,
    val boltScore: Int = 20,  // Body Oxygen Level Test (seconds), normal 20-40
    val contraindications: List<Contraindication> = emptyList(),
    val hasGivenConsent: Boolean = false,
    val hasSeenDisclaimer: Boolean = false,
    val preferredLanguage: String = "en"
)
