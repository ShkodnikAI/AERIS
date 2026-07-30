package com.aeris.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val heartRate: Int = 70,
    val hrv: Int = 50,
    val sleepQuality: Float = 0.7f,
    val hasHypertension: Boolean = false,
    val hasPregnancy: Boolean = false,
    val hasCardiacIssues: Boolean = false,
    val hasGivenConsent: Boolean = false,
    val hasSeenDisclaimer: Boolean = false,
    val preferredLanguage: String = "en"
)
