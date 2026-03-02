package com.aeris.domain.repository

import com.aeris.domain.model.UserState
import com.aeris.domain.model.Contraindication

/**
 * Repository interface for user state and preferences.
 */
interface UserRepository {
    
    /**
     * Get current user state.
     */
    suspend fun getUserState(): UserState
    
    /**
     * Update user state.
     */
    suspend fun updateUserState(userState: UserState)
    
    /**
     * Add experience points and update level.
     */
    suspend fun addExperience(points: Int)
    
    /**
     * Update user level.
     */
    suspend fun updateLevel(newLevel: Int)
    
    /**
     * Set user contraindications.
     */
    suspend fun setContraindications(contraindications: List<Contraindication>)
    
    /**
     * Accept medical disclaimer.
     */
    suspend fun acceptDisclaimer()
    
    /**
     * Accept advanced protocols consent.
     */
    suspend fun acceptAdvancedConsent()
    
    /**
     * Update streak after completed session.
     */
    suspend fun updateStreak()
    
    /**
     * Mark protocol as completed.
     */
    suspend fun markProtocolCompleted(protocolId: String)
    
    /**
     * Update user preferences.
     */
    suspend fun updatePreferences(
        darkMode: Boolean? = null,
        haptic: Boolean? = null,
        sound: Boolean? = null,
        language: String? = null
    )
}
