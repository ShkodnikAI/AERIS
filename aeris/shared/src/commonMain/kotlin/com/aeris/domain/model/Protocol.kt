package com.aeris.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a breathing protocol with all its configuration.
 * Core domain model - no platform-specific dependencies.
 */
@Serializable
data class Protocol(
    val id: String,
    val name: LocalizedString,
    val description: LocalizedString,
    val category: ProtocolCategory,
    val mechanisms: List<PhysiologicalMechanism>,
    val steps: List<BreathingStep>,
    val sessionDurationMinutes: Int,
    val difficulty: Difficulty,
    val safetyRules: SafetyRules,
    val animation: AnimationConfig
)

@Serializable
data class LocalizedString(
    val en: String,
    val ru: String
) {
    fun get(languageCode: String): String = when (languageCode) {
        "ru" -> ru
        else -> en
    }
}

@Serializable
enum class ProtocolCategory {
    RELAXATION_SLEEP,
    ENERGY_FOCUS,
    THERAPY_HEALTH,
    SPIRITUAL_ADVANCED
}

@Serializable
enum class PhysiologicalMechanism {
    PARASYMPATHETIC_ACTIVATION,
    SYMPATHETIC_STIMULATION,
    CO2_TRAINING,
    HYPOXIC_ADAPTATION,
    RESPIRATORY_MUSCLE_TRAINING,
    LUNG_CAPACITY_INCREASE,
    RESONANCE_SYNCHRONIZATION
}

@Serializable
enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

@Serializable
data class BreathingStep(
    val phase: BreathingPhase,
    val durationSeconds: Float,
    val instruction: LocalizedString
)

@Serializable
enum class BreathingPhase {
    INHALE,
    HOLD_IN,
    EXHALE,
    HOLD_OUT
}

@Serializable
data class SafetyRules(
    val minLevel: Int = 1,
    val contraindications: List<Contraindication> = emptyList(),
    val maxHoldForBeginners: Int = 30,
    val hrThreshold: Int = 100,
    val requiresConsent: Boolean = false
)

@Serializable
enum class Contraindication {
    HYPERTENSION,
    PREGNANCY,
    HEART_DISEASE,
    EPILEPSY,
    ASTHMA_SEVERE,
    PANIC_DISORDER
}

@Serializable
data class AnimationConfig(
    val type: AnimationType = AnimationType.CIRCLE,
    val soundEnabled: Boolean = true,
    val hapticFeedback: Boolean = true
)

@Serializable
enum class AnimationType {
    CIRCLE,
    SQUARE,
    WAVE,
    CUSTOM
}
