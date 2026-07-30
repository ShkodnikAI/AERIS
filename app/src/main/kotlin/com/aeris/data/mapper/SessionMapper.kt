package com.aeris.data.mapper

import com.aeris.data.local.entity.SessionEntity
import com.aeris.domain.model.Session

object SessionMapper {
    fun toDomain(entity: SessionEntity): Session = Session(
        id = entity.id,
        protocolId = entity.protocolId,
        completedAt = entity.completedAt,
        durationSec = entity.durationSec,
        userRating = entity.userRating,
        maxHoldAchieved = entity.maxHoldAchieved,
        completed = entity.completed
    )

    fun toEntity(domain: Session): SessionEntity = SessionEntity(
        id = domain.id,
        protocolId = domain.protocolId,
        completedAt = domain.completedAt,
        durationSec = domain.durationSec,
        userRating = domain.userRating,
        maxHoldAchieved = domain.maxHoldAchieved,
        completed = domain.completed
    )
}
