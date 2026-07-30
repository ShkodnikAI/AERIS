package com.aeris.domain.repository

import com.aeris.domain.model.Protocol
import kotlinx.coroutines.flow.Flow

interface ProtocolRepository {
    fun getAllProtocols(): Flow<List<Protocol>>
    fun getProtocolById(id: String): Flow<Protocol?>
}
