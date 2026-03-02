package com.aeris.domain.repository

import com.aeris.domain.model.Protocol
import com.aeris.domain.model.ProtocolCategory
import com.aeris.domain.model.Difficulty

/**
 * Repository interface for accessing breathing protocols.
 * Implementation provided by platform-specific modules.
 */
interface ProtocolRepository {
    
    /**
     * Get all available protocols.
     */
    suspend fun getAllProtocols(): List<Protocol>
    
    /**
     * Get protocol by unique ID.
     */
    suspend fun getProtocolById(id: String): Protocol?
    
    /**
     * Get protocols filtered by category.
     */
    suspend fun getProtocolsByCategory(category: ProtocolCategory): List<Protocol>
    
    /**
     * Get protocols filtered by difficulty level.
     */
    suspend fun getProtocolsByDifficulty(difficulty: Difficulty): List<Protocol>
    
    /**
     * Get protocols safe for given user level.
     */
    suspend fun getProtocolsForLevel(userLevel: Int): List<Protocol>
    
    /**
     * Search protocols by name or description.
     */
    suspend fun searchProtocols(query: String, languageCode: String): List<Protocol>
}
