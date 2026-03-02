package com.aeris.android.data.local.dao

import androidx.room.*
import com.aeris.android.data.local.entity.UserStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStateDao {
    
    @Query("SELECT * FROM user_state WHERE id = :id")
    fun getUserState(id: String = "default_user"): Flow<UserStateEntity?>
    
    @Query("SELECT * FROM user_state WHERE id = :id")
    suspend fun getUserStateOnce(id: String = "default_user"): UserStateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserState(userState: UserStateEntity)
    
    @Update
    suspend fun updateUserState(userState: UserStateEntity)
    
    @Query("UPDATE user_state SET level = :level WHERE id = :id")
    suspend fun updateLevel(id: String = "default_user", level: Int)
    
    @Query("UPDATE user_state SET experience = :experience WHERE id = :id")
    suspend fun updateExperience(id: String = "default_user", experience: Int)
    
    @Query("UPDATE user_state SET currentStreak = :streak WHERE id = :id")
    suspend fun updateStreak(id: String = "default_user", streak: Int)
    
    @Query("UPDATE user_state SET longestStreak = :streak WHERE id = :id AND longestStreak < :streak")
    suspend fun updateLongestStreak(id: String = "default_user", streak: Int)
    
    @Query("UPDATE user_state SET totalSessions = totalSessions + 1, lastSessionDate = :sessionDate WHERE id = :id")
    suspend fun incrementSessionCount(id: String = "default_user", sessionDate: Long)
    
    @Query("UPDATE user_state SET hasAcceptedDisclaimer = 1 WHERE id = :id")
    suspend fun acceptDisclaimer(id: String = "default_user")
    
    @Query("UPDATE user_state SET hasAcceptedAdvancedConsent = 1 WHERE id = :id")
    suspend fun acceptAdvancedConsent(id: String = "default_user")
    
    @Query("UPDATE user_state SET darkModeEnabled = :enabled WHERE id = :id")
    suspend fun setDarkMode(id: String = "default_user", enabled: Boolean)
    
    @Query("UPDATE user_state SET hapticEnabled = :enabled WHERE id = :id")
    suspend fun setHaptic(id: String = "default_user", enabled: Boolean)
    
    @Query("UPDATE user_state SET soundEnabled = :enabled WHERE id = :id")
    suspend fun setSound(id: String = "default_user", enabled: Boolean)
    
    @Query("UPDATE user_state SET preferredLanguage = :language WHERE id = :id")
    suspend fun setLanguage(id: String = "default_user", language: String)
}
