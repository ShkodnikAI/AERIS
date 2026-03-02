package com.aeris.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing user state and progress.
 */
@Entity(tableName = "user_state")
data class UserStateEntity(
    @PrimaryKey
    val id: String = "default_user",
    val level: Int = 1,
    val experience: Int = 0,
    val totalSessions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastSessionDate: Long? = null,
    val contraindicationsJson: String = "[]",
    val preferredCategory: String? = null,
    val completedProtocolIdsJson: String = "[]",
    val hasAcceptedDisclaimer: Boolean = false,
    val hasAcceptedAdvancedConsent: Boolean = false,
    val preferredLanguage: String = "en",
    val darkModeEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)
