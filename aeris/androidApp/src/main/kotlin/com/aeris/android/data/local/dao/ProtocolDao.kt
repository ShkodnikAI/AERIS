package com.aeris.android.data.local.dao

import androidx.room.*
import com.aeris.android.data.local.entity.ProtocolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocolDao {
    
    @Query("SELECT * FROM protocols")
    fun getAllProtocols(): Flow<List<ProtocolEntity>>
    
    @Query("SELECT * FROM protocols WHERE id = :id")
    suspend fun getProtocolById(id: String): ProtocolEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocol(protocol: ProtocolEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocols(protocols: List<ProtocolEntity>)
    
    @Update
    suspend fun updateProtocol(protocol: ProtocolEntity)
    
    @Query("UPDATE protocols SET timesCompleted = timesCompleted + 1, lastCompletedAt = :completedAt WHERE id = :protocolId")
    suspend fun incrementCompletionCount(protocolId: String, completedAt: Long)
    
    @Query("UPDATE protocols SET isFavorite = :isFavorite WHERE id = :protocolId")
    suspend fun setFavorite(protocolId: String, isFavorite: Boolean)
    
    @Query("SELECT * FROM protocols WHERE isFavorite = 1")
    fun getFavoriteProtocols(): Flow<List<ProtocolEntity>>
    
    @Query("UPDATE protocols SET personalBestCycles = :cycles WHERE id = :protocolId AND personalBestCycles < :cycles")
    suspend fun updatePersonalBest(protocolId: String, cycles: Int)
    
    @Query("UPDATE protocols SET averageRating = (averageRating * totalRatings + :rating) / (totalRatings + 1), totalRatings = totalRatings + 1 WHERE id = :protocolId")
    suspend fun addRating(protocolId: String, rating: Float)
    
    @Delete
    suspend fun deleteProtocol(protocol: ProtocolEntity)
}
