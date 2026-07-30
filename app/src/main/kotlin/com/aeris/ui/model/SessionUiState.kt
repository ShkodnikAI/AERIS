package com.aeris.ui.model

sealed class SessionUiState {
    object Breathing : SessionUiState()
    object Paused : SessionUiState()
    data class Completed(val cyclesDone: Int, val durationSec: Int) : SessionUiState()
}
