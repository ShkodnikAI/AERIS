package com.aeris.data.mapper

import com.aeris.data.local.entity.BadgeEntity
import com.aeris.domain.model.Badge

object BadgeMapper {
    fun toDomain(entity: BadgeEntity): Badge = Badge(
        id = entity.badgeId,
        nameKey = "badge_${entity.badgeId}_name",
        descriptionKey = "badge_${entity.badgeId}_desc",
        earnedAt = entity.earnedAt
    )

    fun toEntity(domain: Badge): BadgeEntity = BadgeEntity(
        badgeId = domain.id,
        earnedAt = domain.earnedAt ?: System.currentTimeMillis()
    )
}
