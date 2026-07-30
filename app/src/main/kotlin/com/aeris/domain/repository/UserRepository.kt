package com.aeris.domain.repository

import com.aeris.domain.model.UserProfile
import com.aeris.domain.model.UserState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile>
    fun getUserState(): Flow<UserState>
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun updateStreak(streak: Int)
    suspend fun updateLevel(level: Int)
    suspend fun earnBadge(badgeId: String)
    fun getEarnedBadges(): Flow<List<String>>
    suspend fun resetProgress()
}
