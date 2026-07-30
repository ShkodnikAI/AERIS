package com.aeris.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.data.datastore.SettingsManager
import com.aeris.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val userRepository: UserRepository
) : ViewModel() {

    val theme = settingsManager.theme.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val language = settingsManager.language.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")
    val vibration = settingsManager.vibrationEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setTheme(theme: String) {
        viewModelScope.launch { settingsManager.setTheme(theme) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { settingsManager.setLanguage(language) }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setVibration(enabled) }
    }

    fun resetProgress() {
        viewModelScope.launch { userRepository.resetProgress() }
    }
}
