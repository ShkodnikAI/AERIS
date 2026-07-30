package com.aeris.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.domain.model.*
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.repository.UserRepository
import com.aeris.domain.usecase.CalculateBCI
import com.aeris.domain.usecase.CalculateNSI
import com.aeris.domain.usecase.CalculateStreak
import com.aeris.domain.usecase.RecommendProtocols
import com.aeris.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeData(
    val nsi: NervousState,
    val bci: Float,
    val streak: Int,
    val recommendations: List<Protocol>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val protocolRepository: ProtocolRepository,
    private val calculateNSI: CalculateNSI,
    private val calculateBCI: CalculateBCI,
    private val calculateStreak: CalculateStreak,
    private val recommendProtocols: RecommendProtocols
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeData>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeData>> = _uiState.asStateFlow()

    private val _showUpdateSheet = MutableStateFlow(false)
    val showUpdateSheet: StateFlow<Boolean> = _showUpdateSheet.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                userRepository.getUserProfile(),
                userRepository.getUserState(),
                protocolRepository.getAllProtocols()
            ) { profile, state, protocols ->
                val nsi = calculateNSI(profile.heartRate, profile.hrv, profile.sleepQuality)
                val bci = calculateBCI(0f) // placeholder
                val streak = calculateStreak(emptyList()) // placeholder
                val recommendations = recommendProtocols(
                    UserState(level = 1, nsi = nsi, bci = bci, totalSessions = 0, currentStreak = streak),
                    profile,
                    protocols,
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                )
                UiState.Success(HomeData(nsi, bci, streak, recommendations))
            }.catch { e ->
                emit(UiState.Error(e.message ?: "Unknown error"))
            }.collect { _uiState.value = it }
        }
    }

    fun showUpdateSheet() { _showUpdateSheet.value = true }
    fun hideUpdateSheet() { _showUpdateSheet.value = false }

    fun updateState(hr: Int, hrv: Int, sleep: Float) {
        viewModelScope.launch {
            userRepository.getUserProfile().first().let { current ->
                userRepository.updateUserProfile(
                    current.copy(heartRate = hr, hrv = hrv, sleepQuality = sleep)
                )
            }
            _showUpdateSheet.value = false
        }
    }
}
