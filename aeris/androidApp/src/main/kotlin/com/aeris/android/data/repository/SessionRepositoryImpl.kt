package com.aeris.android.data.repository

import com.aeris.android.data.local.dao.SessionDao
import com.aeris.android.data.local.entity.SessionEntity
import com.aeris.domain.model.Session
import com.aeris.domain.model.InterruptionReason
import com.aeris.domain.repository.SessionRepository
import com.aeris.util.DateTimeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Implementation of SessionRepository using Room.
 */
class SessionRepositoryImpl(
    private val sessionDao: SessionDao
) : SessionRepository {
    
    override suspend fun saveSession(session: Session) = withContext(Dispatchers.IO) {
        sessionDao.insertSession(session.toEntity())
    }
    
    override suspend fun getAllSessions(): List<Session> = withContext(Dispatchers.IO) {
        sessionDao.getAllSessions().first().map { it.toDomain() }
    }
    
    override suspend fun getSessionsForProtocol(protocolId: String): List<Session> = withContext(Dispatchers.IO) {
        sessionDao.getSessionsForProtocol(protocolId).first().map { it.toDomain() }
    }
    
    override suspend fun getRecentSessions(days: Int): List<Session> = withContext(Dispatchers.IO) {
        val startTime = DateTimeHelper.startOfDay(daysAgo = days)
        sessionDao.getSessionsSince(startTime).first().map { it.toDomain() }
    }
    
    override suspend fun getSessionById(id: String): Session? = withContext(Dispatchers.IO) {
        sessionDao.getSessionById(id)?.toDomain()
    }
    
    override suspend fun deleteSession(id: String) = withContext(Dispatchers.IO) {
        sessionDao.deleteSessionById(id)
    }
    
    override suspend fun getTotalPracticeMinutes(): Int = withContext(Dispatchers.IO) {
        (sessionDao.getTotalDurationSeconds() ?: 0) / 60
    }
    
    override suspend fun getCurrentStreak(): Int = withContext(Dispatchers.IO) {
        val sessions = sessionDao.getAllSessions().first()
        if (sessions.isEmpty()) return@withContext 0
        
        var streak = 0
        var currentDay = DateTimeHelper.startOfDay(daysAgo = 0)
        
        // Check if there's a session today
        val todayCount = sessionDao.getSessionCountForDay(currentDay)
        if (todayCount == 0) {
            // Check yesterday to see if streak is still active
            val yesterdayStart = DateTimeHelper.startOfDay(daysAgo = 1)
            val yesterdayEnd = currentDay
            val yesterdayCount = sessions.count { it.startTime in yesterdayStart until yesterdayEnd }
            if (yesterdayCount == 0) return@withContext 0
            
            // Start counting from yesterday
            currentDay = yesterdayStart
        }
        
        // Count consecutive days with sessions
        for (daysAgo in 0..365) {
            val dayStart = DateTimeHelper.startOfDay(daysAgo = daysAgo)
            val dayEnd = DateTimeHelper.startOfDay(daysAgo = daysAgo - 1)
            
            val hasSession = sessions.any { it.startTime in dayStart until dayEnd }
            if (hasSession) {
                streak++
            } else if (daysAgo > 0) {
                break
            }
        }
        
        streak
    }
    
    override suspend fun getTodaySessionsCount(): Int = withContext(Dispatchers.IO) {
        val startOfToday = DateTimeHelper.startOfDay()
        sessionDao.getSessionCountForDay(startOfToday)
    }
    
    private fun Session.toEntity() = SessionEntity(
        id = id,
        protocolId = protocolId,
        startTime = startTime,
        endTime = endTime ?: System.currentTimeMillis(),
        completedCycles = completedCycles,
        targetCycles = targetCycles,
        durationSeconds = ((endTime ?: System.currentTimeMillis()) - startTime).toInt() / 1000,
        averageHeartRate = averageHeartRate,
        userRating = userRating,
        notes = notes,
        wasInterrupted = wasInterrupted,
        interruptionReason = interruptionReason?.name
    )
    
    private fun SessionEntity.toDomain() = Session(
        id = id,
        protocolId = protocolId,
        startTime = startTime,
        endTime = endTime,
        completedCycles = completedCycles,
        targetCycles = targetCycles,
        averageHeartRate = averageHeartRate,
        userRating = userRating,
        notes = notes,
        wasInterrupted = wasInterrupted,
        interruptionReason = interruptionReason?.let { 
            try { InterruptionReason.valueOf(it) } catch (e: Exception) { null }
        }
    )
}
