package com.aeris.android.di

import android.content.Context
import com.aeris.android.data.local.dao.ProtocolDao
import com.aeris.android.data.local.dao.SessionDao
import com.aeris.android.data.local.dao.UserStateDao
import com.aeris.android.data.local.database.AerisDatabase
import com.aeris.android.data.datastore.SettingsManager
import com.aeris.android.data.health.HealthConnectManager
import com.aeris.android.data.health.MockHealthClient
import com.aeris.android.data.repository.ProtocolRepositoryImpl
import com.aeris.android.data.repository.SessionRepositoryImpl
import com.aeris.android.data.repository.UserRepositoryImpl
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.repository.SessionRepository
import com.aeris.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Database
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AerisDatabase {
        return AerisDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideSessionDao(database: AerisDatabase): SessionDao {
        return database.sessionDao()
    }
    
    @Provides
    @Singleton
    fun provideUserStateDao(database: AerisDatabase): UserStateDao {
        return database.userStateDao()
    }
    
    @Provides
    @Singleton
    fun provideProtocolDao(database: AerisDatabase): ProtocolDao {
        return database.protocolDao()
    }
    
    // DataStore
    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(context)
    }
    
    // Health
    @Provides
    @Singleton
    fun provideHealthConnectManager(@ApplicationContext context: Context): HealthConnectManager {
        return HealthConnectManager(context)
    }
    
    @Provides
    @Singleton
    fun provideMockHealthClient(): MockHealthClient {
        return MockHealthClient()
    }
    
    // Repositories
    @Provides
    @Singleton
    fun provideProtocolRepository(
        @ApplicationContext context: Context,
        protocolDao: ProtocolDao
    ): ProtocolRepository {
        return ProtocolRepositoryImpl(context, protocolDao)
    }
    
    @Provides
    @Singleton
    fun provideSessionRepository(sessionDao: SessionDao): SessionRepository {
        return SessionRepositoryImpl(sessionDao)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        userStateDao: UserStateDao,
        settingsManager: SettingsManager
    ): UserRepository {
        return UserRepositoryImpl(userStateDao, settingsManager)
    }
}
