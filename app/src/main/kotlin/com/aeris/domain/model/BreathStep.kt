package com.aeris.domain.model

data class BreathStep(
    val phase: Phase,
    val durationSec: Int,
    val instructionKey: String
)
