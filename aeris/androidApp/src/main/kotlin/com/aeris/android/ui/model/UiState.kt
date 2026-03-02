package com.aeris.android.ui.model

import com.aeris.domain.model.Protocol
import com.aeris.domain.model.NervousState

/**
 * Generic UI state wrapper for screens.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
    
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    
    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}

/**
 * Home screen state.
 */
data class HomeUiState(
    val userName: String = "",
    val currentLevel: Int = 1,
    val currentStreak: Int = 0,
    val totalSessions: Int = 0,
    val nervousState: NervousState = NervousState.BALANCED,
    val bciScore: Float = 50f,
    val recommendedProtocols: List<Protocol> = emptyList(),
    val hasAcceptedDisclaimer: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * Session screen state.
 */
data class SessionUiState(
    val protocol: Protocol? = null,
    val currentPhaseIndex: Int = 0,
    val currentCycle: Int = 1,
    val totalCycles: Int = 4,
    val phaseProgress: Float = 0f,
    val elapsedSeconds: Float = 0f,
    val isPaused: Boolean = false,
    val isComplete: Boolean = false,
    val currentHeartRate: Int? = null,
    val showSafetyWarning: Boolean = false,
    val safetyWarningMessage: String = ""
) {
    val currentPhase get() = protocol?.steps?.getOrNull(currentPhaseIndex)
    val phaseDuration get() = currentPhase?.durationSeconds ?: 4f
    val overallProgress: Float
        get() {
            val stepsPerCycle = protocol?.steps?.size ?: 1
            val totalSteps = totalCycles * stepsPerCycle
            val completedSteps = (currentCycle - 1) * stepsPerCycle + currentPhaseIndex
            return (completedSteps.toFloat() / totalSteps).coerceIn(0f, 1f)
        }
}

/**
 * Protocol list screen state.
 */
data class ProtocolListUiState(
    val protocols: List<Protocol> = emptyList(),
    val filteredProtocols: List<Protocol> = emptyList(),
    val selectedCategory: String? = null,
    val userLevel: Int = 1,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * Profile screen state.
 */
data class ProfileUiState(
    val level: Int = 1,
    val experience: Int = 0,
    val experienceToNextLevel: Int = 100,
    val totalSessions: Int = 0,
    val totalMinutes: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val nsiScore: Float = 50f,
    val bciScore: Float = 50f,
    val nervousState: NervousState = NervousState.BALANCED,
    val heartRate: Int? = null,
    val hrv: Int? = null,
    val isLoading: Boolean = true
)
