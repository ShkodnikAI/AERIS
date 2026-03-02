package com.aeris.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing completed sessions.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey
    val id: String,
    val protocolId: String,
    val startTime: Long,
    val endTime: Long,
    val completedCycles: Int,
    val targetCycles: Int,
    val durationSeconds: Int,
    val averageHeartRate: Int? = null,
    val userRating: Int? = null,
    val notes: String? = null,
    val wasInterrupted: Boolean = false,
    val interruptionReason: String? = null
)
