package com.aeris.domain.usecase

import com.aeris.domain.model.*
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyCheckerTest {
    private val checker = CheckSafety()
    private val userState = UserState(level = 1)
    private val userProfile = UserProfile()

    @Test
    fun test_kumbhaka_blocked_at_level1() {
        val protocol = ProtocolProvider.allProtocols.find { it.id == "kumbhaka_advanced" }!!
        val result = checker(protocol, userState, userProfile)
        assertTrue(result is SafetyResult.Blocked)
    }

    @Test
    fun test_buteyko_blocked_with_hypertension() {
        val protocol = ProtocolProvider.allProtocols.find { it.id == "buteyko_basic" }!!
        val profile = UserProfile(contraindications = listOf(Contraindication.HYPERTENSION))
        val result = checker(protocol, userState, profile)
        assertTrue(result is SafetyResult.Blocked)
    }

    @Test
    fun test_consent_warning_when_not_given() {
        val protocol = ProtocolProvider.allProtocols.find { it.id == "sitali" }!!
        val state = UserState(level = 3)
        val result = checker(protocol, state, userProfile)
        assertTrue(result is SafetyResult.Warning)
    }

    @Test
    fun test_hypoxic_blocked_below_level3() {
        val protocol = ProtocolProvider.allProtocols.find { it.id == "kumbhaka_advanced" }!!
        val state = UserState(level = 2)
        val result = checker(protocol, state, userProfile)
        assertTrue(result is SafetyResult.Blocked)
    }

    @Test
    fun test_basic_protocol_always_allowed() {
        val protocol = ProtocolProvider.allProtocols.find { it.id == "four_seven_eight" }!!
        val result = checker(protocol, userState, userProfile)
        assertTrue(result is SafetyResult.Allowed)
    }
}
