package com.aeris.data.local.dao

import androidx.room.*
import com.aeris.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: BadgeEntity)

    @Query("SELECT * FROM badges")
    fun getAll(): Flow<List<BadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM badges WHERE badgeId = :id)")
    fun exists(id: String): Flow<Boolean>

    @Query("DELETE FROM badges")
    suspend fun deleteAll()
}
