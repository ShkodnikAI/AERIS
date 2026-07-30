package com.aeris.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val protocolId: String,
    val completedAt: Long,
    val durationSec: Int,
    val userRating: Int,
    val maxHoldAchieved: Float,
    val completed: Boolean
)
