package com.aeris.data.repository

import com.aeris.data.provider.ProtocolProvider
import com.aeris.domain.model.Protocol
import com.aeris.domain.repository.ProtocolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProtocolRepositoryImpl : ProtocolRepository {
    override fun getAllProtocols(): Flow<List<Protocol>> = flow {
        emit(ProtocolProvider.allProtocols)
    }

    override fun getProtocolById(id: String): Flow<Protocol?> = flow {
        emit(ProtocolProvider.allProtocols.find { it.id == id })
    }
}
