package com.aeris.ui.screens.protocols

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.domain.model.*
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.repository.UserRepository
import com.aeris.domain.usecase.CheckSafety
import com.aeris.ui.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProtocolListItem(
    val protocol: Protocol,
    val isLocked: Boolean,
    val hasWarning: Boolean,
    val requiresConsent: Boolean
)

@HiltViewModel
class ProtocolListViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository,
    private val userRepository: UserRepository,
    private val checkSafety: CheckSafety
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<Difficulty?>(null)
    val selectedDifficulty: StateFlow<Difficulty?> = _selectedDifficulty.asStateFlow()

    val uiState: StateFlow<UiState<List<ProtocolListItem>>> = combine(
        protocolRepository.getAllProtocols(),
        userRepository.getUserProfile(),
        userRepository.getUserState(),
        _selectedCategory,
        _selectedDifficulty
    ) { protocols, profile, state, category, difficulty ->
        try {
            val filtered = protocols.filter { p ->
                (category == null || p.category == category) &&
                (difficulty == null || p.difficulty == difficulty)
            }
            val items = filtered.map { p ->
                val safety = checkSafety(p, state, profile)
                ProtocolListItem(
                    protocol = p,
                    isLocked = safety is SafetyResult.Blocked,
                    hasWarning = safety is SafetyResult.Warning,
                    requiresConsent = p.safetyRules.requiresConsent && !profile.hasGivenConsent
                )
            }
            UiState.Success(items)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Unknown error")
        }
    }.catch { e ->
        emit(UiState.Error(e.message ?: "Unknown error"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun setCategory(category: Category?) { _selectedCategory.value = category }
    fun setDifficulty(difficulty: Difficulty?) { _selectedDifficulty.value = difficulty }
}
