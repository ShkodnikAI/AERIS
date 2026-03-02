package com.aeris.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a completed or ongoing breathing session.
 */
@Serializable
data class Session(
    val id: String,
    val protocolId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val completedCycles: Int = 0,
    val targetCycles: Int = 0,
    val averageHeartRate: Int? = null,
    val userRating: Int? = null,
    val notes: String? = null,
    val wasInterrupted: Boolean = false,
    val interruptionReason: InterruptionReason? = null
)

@Serializable
enum class InterruptionReason {
    USER_STOPPED,
    SAFETY_TRIGGERED,
    HR_TOO_HIGH,
    DIZZINESS_REPORTED,
    APP_BACKGROUNDED
}

/**
 * Active session state for UI.
 */
data class ActiveSession(
    val protocol: Protocol,
    val currentStep: Int = 0,
    val currentCycle: Int = 0,
    val totalCycles: Int = 4,
    val phaseProgress: Float = 0f,
    val elapsedSeconds: Float = 0f,
    val isPaused: Boolean = false,
    val currentHeartRate: Int? = null
) {
    val currentPhase: BreathingPhase
        get() = protocol.steps.getOrNull(currentStep)?.phase ?: BreathingPhase.INHALE
    
    val currentStepDuration: Float
        get() = protocol.steps.getOrNull(currentStep)?.durationSeconds ?: 4f
    
    val isComplete: Boolean
        get() = currentCycle >= totalCycles
    
    val progressPercent: Float
        get() {
            val totalSteps = totalCycles * protocol.steps.size
            val completedSteps = currentCycle * protocol.steps.size + currentStep
            return (completedSteps.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)
        }
}
