package com.aeris.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aeris.data.local.database.AerisDatabase
import com.aeris.data.local.entity.SessionEntity
import com.aeris.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
    private lateinit var db: AerisDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AerisDatabase::class.java
        ).build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun test_insert_session_and_query() = runBlocking {
        val session = SessionEntity(protocolId = "test", completedAt = 1000, durationSec = 60, userRating = 5, maxHoldAchieved = 10f, completed = true)
        db.sessionDao().insert(session)
        val sessions = db.sessionDao().getAll().first()
        assertEquals(1, sessions.size)
        assertEquals("test", sessions[0].protocolId)
    }

    @Test
    fun test_user_profile_singleton() = runBlocking {
        val profile = UserProfileEntity(id = 1, heartRate = 80)
        db.userProfileDao().insertOrUpdate(profile)
        val retrieved = db.userProfileDao().get().first()
        assertNotNull(retrieved)
        assertEquals(80, retrieved?.heartRate)
    }

    @Test
    fun test_badge_insert_and_exists() = runBlocking {
        val badge = com.aeris.data.local.entity.BadgeEntity(badgeId = "first_breath", earnedAt = 1000)
        db.badgeDao().insert(badge)
        val exists = db.badgeDao().exists("first_breath").first()
        assertEquals(true, exists)
    }
}
