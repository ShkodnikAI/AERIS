package com.aeris.android.data.local.dao

import androidx.room.*
import com.aeris.android.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionById(id: String): SessionEntity?
    
    @Query("SELECT * FROM sessions WHERE protocolId = :protocolId ORDER BY startTime DESC")
    fun getSessionsForProtocol(protocolId: String): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions WHERE startTime >= :startTime ORDER BY startTime DESC")
    fun getSessionsSince(startTime: Long): Flow<List<SessionEntity>>
    
    @Query("SELECT * FROM sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<SessionEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)
    
    @Update
    suspend fun updateSession(session: SessionEntity)
    
    @Delete
    suspend fun deleteSession(session: SessionEntity)
    
    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)
    
    @Query("SELECT SUM(durationSeconds) FROM sessions")
    suspend fun getTotalDurationSeconds(): Int?
    
    @Query("SELECT COUNT(*) FROM sessions WHERE startTime >= :startOfDay")
    suspend fun getSessionCountForDay(startOfDay: Long): Int
    
    @Query("SELECT COUNT(*) FROM sessions")
    suspend fun getTotalSessionCount(): Int
    
    @Query("SELECT AVG(userRating) FROM sessions WHERE userRating IS NOT NULL")
    suspend fun getAverageRating(): Float?
}
