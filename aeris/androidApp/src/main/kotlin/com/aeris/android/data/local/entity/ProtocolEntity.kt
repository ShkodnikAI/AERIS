package com.aeris.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing protocol metadata locally.
 * Full protocol data loaded from assets, this caches user-specific state.
 */
@Entity(tableName = "protocols")
data class ProtocolEntity(
    @PrimaryKey
    val id: String,
    val timesCompleted: Int = 0,
    val lastCompletedAt: Long? = null,
    val isFavorite: Boolean = false,
    val personalBestCycles: Int = 0,
    val averageRating: Float = 0f,
    val totalRatings: Int = 0
)
