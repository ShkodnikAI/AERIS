package com.aeris.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.domain.model.*
import com.aeris.domain.repository.SessionRepository
import com.aeris.domain.repository.UserRepository
import com.aeris.domain.usecase.CalculateLevel
import com.aeris.domain.usecase.CalculateStreak
import com.aeris.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class ProfileData(
    val level: Int,
    val streak: Int,
    val hr: Int,
    val hrv: Int,
    val sleepQuality: Float,
    val boltScore: Int,
    val bci: Float,
    val hasHypertension: Boolean,
    val hasPregnancy: Boolean,
    val hasCardiac: Boolean,
    val weeklySessions: List<Int>,
    val allBadges: List<Badge>,
    val earnedBadges: List<String>
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val calculateLevel: CalculateLevel,
    private val calculateStreak: CalculateStreak
) : ViewModel() {

    val uiState: StateFlow<UiState<ProfileData>> = combine(
        userRepository.getUserProfile(),
        userRepository.getUserState(),
        sessionRepository.getAllSessions()
    ) { profile, state, sessions ->
        try {
            val timestamps = sessions.map { it.completedAt }
            val streak = calculateStreak(timestamps)
            val level = calculateLevel(sessions.size, state.bci)
            val weekly = getWeeklySessions(sessions)
            val badges = listOf(
                Badge("first_breath", "badge_first_breath_name", "badge_first_breath_desc"),
                Badge("week_warrior", "badge_week_warrior_name", "badge_week_warrior_desc"),
                Badge("co2_warrior", "badge_co2_warrior_name", "badge_co2_warrior_desc"),
                Badge("night_owl", "badge_night_owl_name", "badge_night_owl_desc"),
                Badge("early_bird", "badge_early_bird_name", "badge_early_bird_desc"),
                Badge("month_master", "badge_month_master_name", "badge_month_master_desc"),
                Badge("century", "badge_century_name", "badge_century_desc")
            )
            val earned = userRepository.getEarnedBadges().first()
            UiState.Success(
                ProfileData(
                    level = level,
                    streak = streak,
                    hr = profile.heartRate,
                    hrv = profile.hrv,
                    sleepQuality = profile.sleepQuality,
                    boltScore = profile.boltScore,
                    bci = state.bci,
                    hasHypertension = profile.contraindications.contains(Contraindication.HYPERTENSION),
                    hasPregnancy = profile.contraindications.contains(Contraindication.PREGNANCY),
                    hasCardiac = profile.contraindications.contains(Contraindication.CARDIAC_ISSUES),
                    weeklySessions = weekly,
                    allBadges = badges,
                    earnedBadges = earned
                )
            )
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Unknown error")
        }
    }.catch { e ->
        emit(UiState.Error(e.message ?: "Unknown error"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    private fun getWeeklySessions(sessions: List<Session>): List<Int> {
        val today = LocalDate.now(ZoneId.systemDefault())
        val days = (0..6).map { today.minusDays(it.toLong()) }.reversed()
        return days.map { day ->
            sessions.count {
                Instant.ofEpochMilli(it.completedAt).atZone(ZoneId.systemDefault()).toLocalDate() == day
            }
        }
    }

    fun updateHr(hr: Int) { updateProfile { it.copy(heartRate = hr) } }
    fun updateHrv(hrv: Int) { updateProfile { it.copy(hrv = hrv) } }
    fun updateSleep(sleep: Float) { updateProfile { it.copy(sleepQuality = sleep) } }
    fun updateBolt(bolt: Int) { updateProfile { it.copy(boltScore = bolt) } }
    fun toggleHypertension(checked: Boolean) { toggleContra(Contraindication.HYPERTENSION, checked) }
    fun togglePregnancy(checked: Boolean) { toggleContra(Contraindication.PREGNANCY, checked) }
    fun toggleCardiac(checked: Boolean) { toggleContra(Contraindication.CARDIAC_ISSUES, checked) }

    private fun toggleContra(contra: Contraindication, checked: Boolean) {
        updateProfile { current ->
            val contras = current.contraindications.toMutableList()
            if (checked && !contras.contains(contra)) contras.add(contra)
            if (!checked) contras.remove(contra)
            current.copy(contraindications = contras)
        }
    }

    private fun updateProfile(transform: (UserProfile) -> UserProfile) {
        viewModelScope.launch {
            userRepository.getUserProfile().first().let { current ->
                userRepository.updateUserProfile(transform(current))
            }
        }
    }
}
