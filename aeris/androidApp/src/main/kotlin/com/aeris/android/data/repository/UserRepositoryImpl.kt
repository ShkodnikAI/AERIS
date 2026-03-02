package com.aeris.android.data.repository

import com.aeris.android.data.local.dao.UserStateDao
import com.aeris.android.data.local.entity.UserStateEntity
import com.aeris.android.data.datastore.SettingsManager
import com.aeris.domain.model.Contraindication
import com.aeris.domain.model.ProtocolCategory
import com.aeris.domain.model.UserState
import com.aeris.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of UserRepository using Room and DataStore.
 */
class UserRepositoryImpl(
    private val userStateDao: UserStateDao,
    private val settingsManager: SettingsManager
) : UserRepository {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun getUserState(): UserState = withContext(Dispatchers.IO) {
        val entity = userStateDao.getUserStateOnce() ?: createDefaultUserState()
        entity.toDomain()
    }
    
    override suspend fun updateUserState(userState: UserState) = withContext(Dispatchers.IO) {
        userStateDao.insertUserState(userState.toEntity())
    }
    
    override suspend fun addExperience(points: Int) = withContext(Dispatchers.IO) {
        val current = getUserState()
        val newExperience = current.experience + points
        
        // Level up calculation: 100 XP per level
        val experiencePerLevel = 100
        val newLevel = (newExperience / experiencePerLevel) + 1
        
        userStateDao.updateExperience(experience = newExperience)
        if (newLevel > current.level) {
            userStateDao.updateLevel(level = newLevel.coerceAtMost(5))
        }
    }
    
    override suspend fun updateLevel(newLevel: Int) = withContext(Dispatchers.IO) {
        userStateDao.updateLevel(level = newLevel.coerceIn(1, 5))
    }
    
    override suspend fun setContraindications(contraindications: List<Contraindication>) = withContext(Dispatchers.IO) {
        val current = getUserState()
        val updated = current.copy(contraindications = contraindications)
        updateUserState(updated)
    }
    
    override suspend fun acceptDisclaimer() = withContext(Dispatchers.IO) {
        userStateDao.acceptDisclaimer()
    }
    
    override suspend fun acceptAdvancedConsent() = withContext(Dispatchers.IO) {
        userStateDao.acceptAdvancedConsent()
    }
    
    override suspend fun updateStreak() = withContext(Dispatchers.IO) {
        val current = getUserState()
        val now = System.currentTimeMillis()
        
        val newStreak = if (current.lastSessionDate != null) {
            val daysSinceLastSession = ((now - current.lastSessionDate!!) / (24 * 60 * 60 * 1000)).toInt()
            when (daysSinceLastSession) {
                0 -> current.currentStreak // Same day, keep streak
                1 -> current.currentStreak + 1 // Next day, increment
                else -> 1 // Gap in days, reset to 1
            }
        } else {
            1 // First session
        }
        
        userStateDao.updateStreak(streak = newStreak)
        userStateDao.updateLongestStreak(streak = newStreak)
        userStateDao.incrementSessionCount(sessionDate = now)
    }
    
    override suspend fun markProtocolCompleted(protocolId: String) = withContext(Dispatchers.IO) {
        val current = getUserState()
        val updated = current.copy(
            completedProtocolIds = current.completedProtocolIds + protocolId
        )
        updateUserState(updated)
    }
    
    override suspend fun updatePreferences(
        darkMode: Boolean?,
        haptic: Boolean?,
        sound: Boolean?,
        language: String?
    ) = withContext(Dispatchers.IO) {
        darkMode?.let { 
            settingsManager.setDarkMode(it)
            userStateDao.setDarkMode(enabled = it)
        }
        haptic?.let { 
            settingsManager.setHapticEnabled(it)
            userStateDao.setHaptic(enabled = it)
        }
        sound?.let { 
            settingsManager.setSoundEnabled(it)
            userStateDao.setSound(enabled = it)
        }
        language?.let { 
            settingsManager.setLanguage(it)
            userStateDao.setLanguage(language = it)
        }
    }
    
    private suspend fun createDefaultUserState(): UserStateEntity {
        val default = UserStateEntity()
        userStateDao.insertUserState(default)
        return default
    }
    
    private fun UserStateEntity.toDomain(): UserState {
        return UserState(
            id = id,
            level = level,
            experience = experience,
            totalSessions = totalSessions,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastSessionDate = lastSessionDate,
            contraindications = try {
                json.decodeFromString<List<String>>(contraindicationsJson)
                    .mapNotNull { name -> 
                        try { Contraindication.valueOf(name) } catch (e: Exception) { null }
                    }
            } catch (e: Exception) { emptyList() },
            preferredCategory = preferredCategory?.let { 
                try { ProtocolCategory.valueOf(it) } catch (e: Exception) { null }
            },
            completedProtocolIds = try {
                json.decodeFromString<List<String>>(completedProtocolIdsJson).toSet()
            } catch (e: Exception) { emptySet() },
            hasAcceptedDisclaimer = hasAcceptedDisclaimer,
            hasAcceptedAdvancedConsent = hasAcceptedAdvancedConsent,
            preferredLanguage = preferredLanguage,
            darkModeEnabled = darkModeEnabled,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled
        )
    }
    
    private fun UserState.toEntity(): UserStateEntity {
        return UserStateEntity(
            id = id,
            level = level,
            experience = experience,
            totalSessions = totalSessions,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastSessionDate = lastSessionDate,
            contraindicationsJson = json.encodeToString(contraindications.map { it.name }),
            preferredCategory = preferredCategory?.name,
            completedProtocolIdsJson = json.encodeToString(completedProtocolIds.toList()),
            hasAcceptedDisclaimer = hasAcceptedDisclaimer,
            hasAcceptedAdvancedConsent = hasAcceptedAdvancedConsent,
            preferredLanguage = preferredLanguage,
            darkModeEnabled = darkModeEnabled,
            hapticEnabled = hapticEnabled,
            soundEnabled = soundEnabled
        )
    }
}
