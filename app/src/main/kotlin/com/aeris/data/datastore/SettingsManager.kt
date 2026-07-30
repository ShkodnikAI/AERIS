package com.aeris.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aeris_settings")

class SettingsManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val VIBRATION_KEY = booleanPreferencesKey("vibration")
    }

    val theme: Flow<String> = dataStore.data.map { it[THEME_KEY] ?: "system" }
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE_KEY] ?: "en" }
    val vibrationEnabled: Flow<Boolean> = dataStore.data.map { it[VIBRATION_KEY] ?: true }

    suspend fun setTheme(theme: String) {
        dataStore.edit { it[THEME_KEY] = theme }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun setVibration(enabled: Boolean) {
        dataStore.edit { it[VIBRATION_KEY] = enabled }
    }
}
