package com.aeris.domain.usecase

import com.aeris.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class RecommenderTest {
    private val checker = CheckSafety()
    private val recommender = RecommendProtocols(checker)
    private val protocols = ProtocolProvider.allProtocols

    @Test
    fun test_hyperarousal_excludes_sympathetic() {
        val state = UserState(level = 1, nsi = NervousState.HYPERAROUSAL)
        val profile = UserProfile()
        val result = recommender(state, profile, protocols, 12)
        result.forEach { p ->
            assertFalse(p.mechanisms.contains(Mechanism.SYMPATHETIC))
        }
    }

    @Test
    fun test_hypoarousal_excludes_parasympathetic() {
        val state = UserState(level = 1, nsi = NervousState.HYPOAROUSAL)
        val profile = UserProfile()
        val result = recommender(state, profile, protocols, 12)
        result.forEach { p ->
            assertFalse(p.mechanisms.contains(Mechanism.PARASYMPATHETIC))
        }
    }

    @Test
    fun test_level1_gets_only_allowed_protocols() {
        val state = UserState(level = 1)
        val profile = UserProfile()
        val result = recommender(state, profile, protocols, 12)
        result.forEach { p ->
            assertTrue(p.safetyRules.minLevel <= 1)
        }
    }

    @Test
    fun test_max_5_recommendations() {
        val state = UserState(level = 5)
        val profile = UserProfile(hasGivenConsent = true)
        val result = recommender(state, profile, protocols, 12)
        assertTrue(result.size <= 5)
    }
}
