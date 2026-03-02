package com.aeris.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents the complete user state including health metrics and progress.
 */
@Serializable
data class UserState(
    val id: String = "default_user",
    val level: Int = 1,
    val experience: Int = 0,
    val totalSessions: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastSessionDate: Long? = null,
    val contraindications: List<Contraindication> = emptyList(),
    val preferredCategory: ProtocolCategory? = null,
    val completedProtocolIds: Set<String> = emptySet(),
    val hasAcceptedDisclaimer: Boolean = false,
    val hasAcceptedAdvancedConsent: Boolean = false,
    val preferredLanguage: String = "en",
    val darkModeEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

/**
 * Health metrics for AI recommendations.
 */
@Serializable
data class HealthMetrics(
    val heartRate: Int = 70,
    val hrv: Int = 50,
    val sleepQuality: Float = 0.7f,
    val lastMeasured: Long = 0L,
    val isFromHealthConnect: Boolean = false
) {
    companion object {
        fun mock(): HealthMetrics = HealthMetrics(
            heartRate = 72,
            hrv = 48,
            sleepQuality = 0.75f,
            lastMeasured = System.currentTimeMillis(),
            isFromHealthConnect = false
        )
    }
}

/**
 * Represents nervous system state calculated from health metrics.
 */
enum class NervousState {
    HYPERAROUSAL,
    BALANCED,
    HYPOAROUSAL;
    
    fun getDescription(languageCode: String): String = when (this) {
        HYPERAROUSAL -> if (languageCode == "ru") "Повышенная активация" else "Heightened Arousal"
        BALANCED -> if (languageCode == "ru") "Сбалансированное" else "Balanced"
        HYPOAROUSAL -> if (languageCode == "ru") "Пониженная активация" else "Low Arousal"
    }
    
    fun getRecommendation(languageCode: String): String = when (this) {
        HYPERAROUSAL -> if (languageCode == "ru") 
            "Рекомендуем расслабляющие практики" 
        else "Relaxation practices recommended"
        BALANCED -> if (languageCode == "ru") 
            "Все практики доступны" 
        else "All practices available"
        HYPOAROUSAL -> if (languageCode == "ru") 
            "Рекомендуем энергизирующие практики" 
        else "Energizing practices recommended"
    }
}

/**
 * User progress summary for display.
 */
data class UserProgress(
    val level: Int,
    val levelProgress: Float,
    val sessionsToNextLevel: Int,
    val totalMinutesPracticed: Int,
    val currentStreak: Int,
    val nsiScore: Float,
    val bciScore: Float,
    val nervousState: NervousState,
    val badges: List<Badge>
)

@Serializable
data class Badge(
    val id: String,
    val name: LocalizedString,
    val description: LocalizedString,
    val iconName: String,
    val earnedDate: Long? = null
) {
    val isEarned: Boolean get() = earnedDate != null
}
