package com.aeris.android.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aeris_settings")

/**
 * DataStore manager for user preferences and settings.
 */
class SettingsManager(private val context: Context) {
    
    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val LANGUAGE = stringPreferencesKey("language")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LAST_SESSION_DATE = longPreferencesKey("last_session_date")
    }
    
    val settingsFlow: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserSettings(
                darkMode = preferences[PreferencesKeys.DARK_MODE] ?: true,
                soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
                hapticEnabled = preferences[PreferencesKeys.HAPTIC_ENABLED] ?: true,
                language = preferences[PreferencesKeys.LANGUAGE] ?: "en",
                reminderEnabled = preferences[PreferencesKeys.REMINDER_ENABLED] ?: false,
                reminderHour = preferences[PreferencesKeys.REMINDER_HOUR] ?: 20,
                reminderMinute = preferences[PreferencesKeys.REMINDER_MINUTE] ?: 0,
                onboardingCompleted = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false,
                lastSessionDate = preferences[PreferencesKeys.LAST_SESSION_DATE]
            )
        }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
    
    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }
    
    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_ENABLED] = enabled
        }
    }
    
    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }
    
    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_ENABLED] = enabled
        }
    }
    
    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_HOUR] = hour
            preferences[PreferencesKeys.REMINDER_MINUTE] = minute
        }
    }
    
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
    
    suspend fun setLastSessionDate(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SESSION_DATE] = timestamp
        }
    }
}

/**
 * Data class representing user settings.
 */
data class UserSettings(
    val darkMode: Boolean = true,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val language: String = "en",
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val onboardingCompleted: Boolean = false,
    val lastSessionDate: Long? = null
)
