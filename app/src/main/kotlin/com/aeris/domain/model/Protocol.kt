package com.aeris.domain.model

data class Protocol(
    val id: String,
    val nameKey: String,
    val category: Category,
    val mechanisms: List<Mechanism>,
    val descriptionKey: String,
    val steps: List<BreathStep>,
    val defaultCycles: Int,
    val sessionDurationMin: Int,
    val difficulty: Difficulty,
    val safetyRules: SafetyRules
)
