package com.aeris.domain.repository

import com.aeris.domain.model.Session

/**
 * Repository interface for session data persistence.
 */
interface SessionRepository {
    
    /**
     * Save a completed session.
     */
    suspend fun saveSession(session: Session)
    
    /**
     * Get all sessions for a user.
     */
    suspend fun getAllSessions(): List<Session>
    
    /**
     * Get sessions for a specific protocol.
     */
    suspend fun getSessionsForProtocol(protocolId: String): List<Session>
    
    /**
     * Get recent sessions (last N days).
     */
    suspend fun getRecentSessions(days: Int = 7): List<Session>
    
    /**
     * Get session by ID.
     */
    suspend fun getSessionById(id: String): Session?
    
    /**
     * Delete a session.
     */
    suspend fun deleteSession(id: String)
    
    /**
     * Get total practice time in minutes.
     */
    suspend fun getTotalPracticeMinutes(): Int
    
    /**
     * Get current streak (consecutive days with practice).
     */
    suspend fun getCurrentStreak(): Int
    
    /**
     * Get sessions count for today.
     */
    suspend fun getTodaySessionsCount(): Int
}
