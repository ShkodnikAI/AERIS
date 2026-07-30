package com.aeris.domain.model

data class Session(
    val id: Long = 0,
    val protocolId: String,
    val completedAt: Long,
    val durationSec: Int,
    val userRating: Int,
    val maxHoldAchieved: Float,
    val completed: Boolean
)
