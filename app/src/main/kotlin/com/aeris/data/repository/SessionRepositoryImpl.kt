package com.aeris.data.repository

import com.aeris.data.local.dao.SessionDao
import com.aeris.data.mapper.SessionMapper
import com.aeris.domain.model.Session
import com.aeris.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionRepositoryImpl(private val sessionDao: SessionDao) : SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insert(SessionMapper.toEntity(session))
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAll().map { list -> list.map { SessionMapper.toDomain(it) } }
    }

    override fun getSessionsByProtocol(protocolId: String): Flow<List<Session>> {
        return sessionDao.getByProtocol(protocolId).map { list -> list.map { SessionMapper.toDomain(it) } }
    }

    override fun getSessionCount(): Flow<Int> = sessionDao.countAll()

    override fun getLastSessions(n: Int): Flow<List<Session>> {
        return sessionDao.getLastN(n).map { list -> list.map { SessionMapper.toDomain(it) } }
    }

    override suspend fun deleteAllSessions() {
        sessionDao.deleteAll()
    }
}
