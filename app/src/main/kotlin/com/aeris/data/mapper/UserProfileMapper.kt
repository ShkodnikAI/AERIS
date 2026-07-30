package com.aeris.data.mapper

import com.aeris.data.local.entity.UserProfileEntity
import com.aeris.domain.model.Contraindication
import com.aeris.domain.model.UserProfile

object UserProfileMapper {
    fun toDomain(entity: UserProfileEntity): UserProfile {
        val contras = mutableListOf<Contraindication>()
        if (entity.hasHypertension) contras.add(Contraindication.HYPERTENSION)
        if (entity.hasPregnancy) contras.add(Contraindication.PREGNANCY)
        if (entity.hasCardiacIssues) contras.add(Contraindication.CARDIAC_ISSUES)
        return UserProfile(
            heartRate = entity.heartRate,
            hrv = entity.hrv,
            sleepQuality = entity.sleepQuality,
            boltScore = entity.boltScore,
            contraindications = contras,
            hasGivenConsent = entity.hasGivenConsent,
            hasSeenDisclaimer = entity.hasSeenDisclaimer,
            preferredLanguage = entity.preferredLanguage
        )
    }

    fun toEntity(domain: UserProfile): UserProfileEntity = UserProfileEntity(
        id = 1,
        heartRate = domain.heartRate,
        hrv = domain.hrv,
        sleepQuality = domain.sleepQuality,
        boltScore = domain.boltScore,
        hasHypertension = domain.contraindications.contains(Contraindication.HYPERTENSION),
        hasPregnancy = domain.contraindications.contains(Contraindication.PREGNANCY),
        hasCardiacIssues = domain.contraindications.contains(Contraindication.CARDIAC_ISSUES),
        hasGivenConsent = domain.hasGivenConsent,
        hasSeenDisclaimer = domain.hasSeenDisclaimer,
        preferredLanguage = domain.preferredLanguage
    )
}
