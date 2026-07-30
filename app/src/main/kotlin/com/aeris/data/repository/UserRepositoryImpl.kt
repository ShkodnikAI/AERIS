package com.aeris.data.repository

import com.aeris.data.local.dao.BadgeDao
import com.aeris.data.local.dao.UserProfileDao
import com.aeris.data.local.entity.BadgeEntity
import com.aeris.data.local.entity.UserProfileEntity
import com.aeris.data.mapper.UserProfileMapper
import com.aeris.domain.model.UserProfile
import com.aeris.domain.model.UserState
import com.aeris.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userProfileDao: UserProfileDao,
    private val badgeDao: BadgeDao
) : UserRepository {

    override fun getUserProfile(): Flow<UserProfile> {
        return userProfileDao.get().map { entity ->
            entity?.let { UserProfileMapper.toDomain(it) }
                ?: UserProfile()
        }
    }

    override fun getUserState(): Flow<UserState> {
        return combine(
            userProfileDao.get(),
            badgeDao.getAll()
        ) { profile, badges ->
            val domainProfile = profile?.let { UserProfileMapper.toDomain(it) } ?: UserProfile()
            UserState(
                level = 1, // calculated dynamically
                nsi = com.aeris.domain.model.NervousState.BALANCED,
                bci = 0f,
                totalSessions = 0,
                currentStreak = 0,
                contraindications = domainProfile.contraindications,
                hasGivenConsent = domainProfile.hasGivenConsent
            )
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(UserProfileMapper.toEntity(profile))
    }

    override suspend fun updateStreak(streak: Int) {
        // Streak is calculated on-the-fly from sessions, not stored separately
    }

    override suspend fun updateLevel(level: Int) {
        // Level is calculated on-the-fly
    }

    override suspend fun earnBadge(badgeId: String) {
        badgeDao.insert(BadgeEntity(badgeId = badgeId, earnedAt = System.currentTimeMillis()))
    }

    override fun getEarnedBadges(): Flow<List<String>> {
        return badgeDao.getAll().map { list -> list.map { it.badgeId } }
    }

    override suspend fun resetProgress() {
        userProfileDao.deleteAll()
        badgeDao.deleteAll()
        userProfileDao.insertOrUpdate(UserProfileEntity())
    }
}
