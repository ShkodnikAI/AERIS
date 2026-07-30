package com.aeris.data.local.dao

import androidx.room.*
import com.aeris.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insert(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY completedAt DESC")
    fun getAll(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE protocolId = :id ORDER BY completedAt DESC")
    fun getByProtocol(id: String): Flow<List<SessionEntity>>

    @Query("SELECT COUNT(*) FROM sessions")
    fun countAll(): Flow<Int>

    @Query("SELECT * FROM sessions ORDER BY completedAt DESC LIMIT :n")
    fun getLastN(n: Int): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE completedAt > :timestamp")
    fun getAfter(timestamp: Long): Flow<List<SessionEntity>>

    @Query("DELETE FROM sessions")
    suspend fun deleteAll()
}
