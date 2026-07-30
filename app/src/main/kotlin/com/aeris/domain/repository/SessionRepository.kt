package com.aeris.domain.repository

import com.aeris.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun insertSession(session: Session)
    fun getAllSessions(): Flow<List<Session>>
    fun getSessionsByProtocol(protocolId: String): Flow<List<Session>>
    fun getSessionCount(): Flow<Int>
    fun getLastSessions(n: Int): Flow<List<Session>>
    suspend fun deleteAllSessions()
}
