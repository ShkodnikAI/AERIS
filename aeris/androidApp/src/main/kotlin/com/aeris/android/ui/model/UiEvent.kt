package com.aeris.android.ui.model

/**
 * UI Events for handling user interactions.
 */
sealed class UiEvent {
    // Navigation
    data class NavigateTo(val route: String) : UiEvent()
    data object NavigateBack : UiEvent()
    
    // Session
    data object StartSession : UiEvent()
    data object PauseSession : UiEvent()
    data object ResumeSession : UiEvent()
    data object StopSession : UiEvent()
    data object SkipHold : UiEvent()
    data class RateSession(val rating: Int) : UiEvent()
    
    // Protocols
    data class SelectProtocol(val protocolId: String) : UiEvent()
    data class FilterByCategory(val category: String?) : UiEvent()
    
    // Settings
    data class ToggleDarkMode(val enabled: Boolean) : UiEvent()
    data class ToggleSound(val enabled: Boolean) : UiEvent()
    data class ToggleHaptic(val enabled: Boolean) : UiEvent()
    data class SetLanguage(val languageCode: String) : UiEvent()
    
    // Safety
    data object AcceptDisclaimer : UiEvent()
    data object AcceptAdvancedConsent : UiEvent()
    data object EmergencyCalm : UiEvent()
    
    // Health
    data object RequestHealthPermissions : UiEvent()
    data object RefreshHealthData : UiEvent()
    
    // Snackbar/Toast
    data class ShowSnackbar(val message: String) : UiEvent()
    data object DismissSnackbar : UiEvent()
}
