package com.aeris.ui.screens.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.audio.AudioService
import com.aeris.domain.model.*
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.usecase.CompleteSession
import com.aeris.ui.model.SessionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository,
    private val completeSession: CompleteSession,
    private val audioService: AudioService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SessionUiState>(SessionUiState.Breathing)
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private val _currentPhase = MutableStateFlow(Phase.INHALE)
    val currentPhase: StateFlow<Phase> = _currentPhase.asStateFlow()

    private val _phaseText = MutableStateFlow("")
    val phaseText: StateFlow<String> = _phaseText.asStateFlow()

    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    private val _currentCycle = MutableStateFlow(1)
    val currentCycle: StateFlow<Int> = _currentCycle.asStateFlow()

    private val _totalCycles = MutableStateFlow(1)
    val totalCycles: StateFlow<Int> = _totalCycles.asStateFlow()

    private val _showStopDialog = MutableStateFlow(false)
    val showStopDialog: StateFlow<Boolean> = _showStopDialog.asStateFlow()

    private val _showSkipWarning = MutableStateFlow(false)
    val showSkipWarning: StateFlow<Boolean> = _showSkipWarning.asStateFlow()

    private val _rating = MutableStateFlow(3)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private var protocol: Protocol? = null
    private var job: Job? = null
    private var skipCount = 0
    private var startTime = 0L

    fun loadProtocol(id: String) {
        viewModelScope.launch {
            protocolRepository.getProtocolById(id).first()?.let { p ->
                protocol = p
                _totalCycles.value = p.defaultCycles
                startAudio(p)
                startSession()
            }
        }
    }

    private fun startAudio(p: Protocol) {
        when (p.audioType) {
            AudioType.BINAURAL -> {
                val beatFreq = when {
                    p.mechanisms.contains(Mechanism.PARASYMPATHETIC) -> 8.0  // Alpha
                    p.mechanisms.contains(Mechanism.RESONANCE_SYNC) -> 6.0 // Theta
                    else -> 8.0
                }
                audioService.playBinaural(baseFreq = 432.0, beatFreq = beatFreq)
            }
            AudioType.MEDITATION -> audioService.playMeditationDrone(baseFreq = 110.0)
            AudioType.ENERGETIC -> audioService.playEnergetic(baseFreq = 220.0)
            AudioType.AFFIRMATION -> {
                audioService.speakAffirmation("Начинаем практику. Следуйте инструкциям на экране.")
            }
            AudioType.NONE -> { /* No audio */ }
        }
    }

    private fun announcePhase(phase: Phase) {
        if (protocol?.audioType == AudioType.AFFIRMATION) {
            val text = when (phase) {
                Phase.INHALE -> "Вдох"
                Phase.HOLD -> "Задержка"
                Phase.EXHALE -> "Выдох"
                Phase.HOLD_EMPTY -> "Пауза"
            }
            audioService.speakAffirmation(text)
        }
    }

    private fun startSession() {
        startTime = System.currentTimeMillis()
        _currentCycle.value = 1
        runCycle()
    }

    private fun runCycle() {
        val p = protocol ?: return
        val steps = p.steps
        job?.cancel()
        job = viewModelScope.launch {
            for (step in steps) {
                _currentPhase.value = step.phase
                _phaseText.value = step.phase.name
                _countdown.value = step.durationSec
                announcePhase(step.phase)
                for (i in step.durationSec downTo 1) {
                    _countdown.value = i
                    delay(1000)
                }
            }
            if (_currentCycle.value < _totalCycles.value) {
                _currentCycle.value += 1
                runCycle()
            } else {
                finishSession()
            }
        }
    }

    fun requestStop() { _showStopDialog.value = true }
    fun dismissStopDialog() { _showStopDialog.value = false }
    fun finishSession() {
        job?.cancel()
        audioService.stop()
        val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        _uiState.value = SessionUiState.Completed(
            cyclesDone = _currentCycle.value,
            durationSec = duration
        )
    }

    fun resume() {
        _uiState.value = SessionUiState.Breathing
        runCycle()
    }

    fun skipHold() {
        skipCount++
        if (skipCount >= 3) {
            _showSkipWarning.value = true
        }
        job?.cancel()
        runCycle()
    }

    fun dismissSkipWarning() { _showSkipWarning.value = false }
    fun setRating(r: Int) { _rating.value = r }

    fun saveSession() {
        viewModelScope.launch {
            val p = protocol ?: return@launch
            val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            val session = Session(
                protocolId = p.id,
                completedAt = System.currentTimeMillis(),
                durationSec = duration,
                userRating = _rating.value,
                maxHoldAchieved = 0f,
                completed = true
            )
            completeSession(session)
            audioService.stop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        audioService.release()
    }
}
