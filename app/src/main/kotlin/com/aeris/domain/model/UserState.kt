package com.aeris.domain.model

data class UserState(
    val level: Int = 1,
    val nsi: NervousState = NervousState.BALANCED,
    val bci: Float = 0f,
    val totalSessions: Int = 0,
    val currentStreak: Int = 0,
    val contraindications: List<Contraindication> = emptyList(),
    val hasGivenConsent: Boolean = false
)
