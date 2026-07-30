package com.aeris.di

import android.content.Context
import com.aeris.data.datastore.SettingsManager
import com.aeris.data.local.database.AerisDatabase
import com.aeris.data.repository.*
import com.aeris.domain.repository.*
import com.aeris.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AerisDatabase {
        return AerisDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideSessionDao(db: AerisDatabase) = db.sessionDao()

    @Provides
    @Singleton
    fun provideBadgeDao(db: AerisDatabase) = db.badgeDao()

    @Provides
    @Singleton
    fun provideUserProfileDao(db: AerisDatabase) = db.userProfileDao()

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(context)
    }

    @Provides
    @Singleton
    fun provideProtocolRepository(): ProtocolRepository = ProtocolRepositoryImpl()

    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: com.aeris.data.local.dao.SessionDao): SessionRepository {
        return SessionRepositoryImpl(sessionDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userProfileDao: com.aeris.data.local.dao.UserProfileDao,
        badgeDao: com.aeris.data.local.dao.BadgeDao
    ): UserRepository {
        return UserRepositoryImpl(userProfileDao, badgeDao)
    }

    @Provides
    @Singleton
    fun provideCalculateNSI(): CalculateNSI = CalculateNSI()

    @Provides
    @Singleton
    fun provideCalculateBCI(): CalculateBCI = CalculateBCI()

    @Provides
    @Singleton
    fun provideCalculateLevel(): CalculateLevel = CalculateLevel()

    @Provides
    @Singleton
    fun provideCalculateStreak(): CalculateStreak = CalculateStreak()

    @Provides
    @Singleton
    fun provideCheckSafety(): CheckSafety = CheckSafety()

    @Provides
    @Singleton
    fun provideCheckBadges(): CheckBadges = CheckBadges()

    @Provides
    @Singleton
    fun provideRecommendProtocols(checkSafety: CheckSafety): RecommendProtocols {
        return RecommendProtocols(checkSafety)
    }

    @Provides
    @Singleton
    fun provideCompleteSession(
        sessionRepository: SessionRepository,
        userRepository: UserRepository,
        calculateLevel: CalculateLevel,
        calculateStreak: CalculateStreak,
        checkBadges: CheckBadges
    ): CompleteSession {
        return CompleteSession(
            sessionRepository,
            userRepository,
            calculateLevel,
            calculateStreak,
            checkBadges
        )
    }
}
