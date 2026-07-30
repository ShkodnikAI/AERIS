package com.aeris.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aeris.data.local.dao.BadgeDao
import com.aeris.data.local.dao.SessionDao
import com.aeris.data.local.dao.UserProfileDao
import com.aeris.data.local.entity.BadgeEntity
import com.aeris.data.local.entity.SessionEntity
import com.aeris.data.local.entity.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SessionEntity::class, BadgeEntity::class, UserProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AerisDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AerisDatabase? = null

        fun getDatabase(context: Context): AerisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AerisDatabase::class.java,
                    "aeris.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PrepopulateCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PrepopulateCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Default user profile will be inserted via repository on first access
    }
}
