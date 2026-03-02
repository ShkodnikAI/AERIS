package com.aeris.android.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aeris.android.data.local.dao.ProtocolDao
import com.aeris.android.data.local.dao.SessionDao
import com.aeris.android.data.local.dao.UserStateDao
import com.aeris.android.data.local.entity.ProtocolEntity
import com.aeris.android.data.local.entity.SessionEntity
import com.aeris.android.data.local.entity.UserStateEntity

/**
 * Main Room database for AERIS app.
 * Stores sessions, user state, and protocol metadata.
 */
@Database(
    entities = [
        SessionEntity::class,
        UserStateEntity::class,
        ProtocolEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AerisDatabase : RoomDatabase() {
    
    abstract fun sessionDao(): SessionDao
    abstract fun userStateDao(): UserStateDao
    abstract fun protocolDao(): ProtocolDao
    
    companion object {
        private const val DATABASE_NAME = "aeris_database"
        
        @Volatile
        private var INSTANCE: AerisDatabase? = null
        
        fun getInstance(context: Context): AerisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AerisDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
