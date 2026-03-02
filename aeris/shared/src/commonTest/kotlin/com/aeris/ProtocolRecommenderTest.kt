package com.aeris

import com.aeris.ai.NsiCalculator
import com.aeris.ai.BciCalculator
import com.aeris.ai.ProtocolRecommender
import com.aeris.domain.model.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class ProtocolRecommenderTest {
    
    private val recommender = ProtocolRecommender()
    
    private val relaxationProtocol = Protocol(
        id = "relax",
        name = LocalizedString("Relaxation", "Релаксация"),
        description = LocalizedString("Calming", "Успокоение"),
        category = ProtocolCategory.RELAXATION_SLEEP,
        mechanisms = listOf(PhysiologicalMechanism.PARASYMPATHETIC_ACTIVATION),
        steps = listOf(BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("In", "Вдох"))),
        sessionDurationMinutes = 5,
        difficulty = Difficulty.BEGINNER,
        safetyRules = SafetyRules(minLevel = 1),
        animation = AnimationConfig()
    )
    
    private val energyProtocol = Protocol(
        id = "energy",
        name = LocalizedString("Energy", "Энергия"),
        description = LocalizedString("Activating", "Активация"),
        category = ProtocolCategory.ENERGY_FOCUS,
        mechanisms = listOf(PhysiologicalMechanism.SYMPATHETIC_STIMULATION),
        steps = listOf(BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("In", "Вдох"))),
        sessionDurationMinutes = 5,
        difficulty = Difficulty.INTERMEDIATE,
        safetyRules = SafetyRules(minLevel = 1),
        animation = AnimationConfig()
    )
    
    private val advancedProtocol = Protocol(
        id = "advanced",
        name = LocalizedString("Advanced", "Продвинутый"),
        description = LocalizedString("Hypoxic", "Гипоксический"),
        category = ProtocolCategory.THERAPY_HEALTH,
        mechanisms = listOf(PhysiologicalMechanism.HYPOXIC_ADAPTATION),
        steps = listOf(BreathingStep(BreathingPhase.HOLD_IN, 30f, LocalizedString("Hold", "Задержка"))),
        sessionDurationMinutes = 10,
        difficulty = Difficulty.ADVANCED,
        safetyRules = SafetyRules(minLevel = 3, contraindications = listOf(Contraindication.HYPERTENSION)),
        animation = AnimationConfig()
    )
    
    private val basicUserState = UserState(level = 1)
    private val advancedUserState = UserState(level = 3)
    private val userWithHypertension = UserState(level = 3, contraindications = listOf(Contraindication.HYPERTENSION))
    
    @Test
    fun `isSafeForUser returns true for beginner protocol and level 1 user`() {
        val result = recommender.isSafeForUser(relaxationProtocol, basicUserState)
        assertTrue(result)
    }
    
    @Test
    fun `isSafeForUser returns false when user level is too low`() {
        val result = recommender.isSafeForUser(advancedProtocol, basicUserState)
        assertFalse(result)
    }
    
    @Test
    fun `isSafeForUser returns false when user has contraindication`() {
        val result = recommender.isSafeForUser(advancedProtocol, userWithHypertension)
        assertFalse(result)
    }
    
    @Test
    fun `hypoxic protocols blocked for low level users`() {
        val result = recommender.isSafeForUser(advancedProtocol, UserState(level = 2))
        assertFalse(result)
    }
    
    @Test
    fun `isCompatibleWithNervousState blocks sympathetic when hyperaroused`() {
        val result = recommender.isCompatibleWithNervousState(
            energyProtocol,
            NervousState.HYPERAROUSAL,
            userLevel = 1
        )
        assertFalse(result)
    }
    
    @Test
    fun `isCompatibleWithNervousState allows relaxation when hyperaroused`() {
        val result = recommender.isCompatibleWithNervousState(
            relaxationProtocol,
            NervousState.HYPERAROUSAL,
            userLevel = 1
        )
        assertTrue(result)
    }
    
    @Test
    fun `isCompatibleWithNervousState allows all when balanced`() {
        val result1 = recommender.isCompatibleWithNervousState(relaxationProtocol, NervousState.BALANCED, 1)
        val result2 = recommender.isCompatibleWithNervousState(energyProtocol, NervousState.BALANCED, 1)
        assertTrue(result1)
        assertTrue(result2)
    }
    
    @Test
    fun `isHeartRateSafe blocks non-relaxation protocols when HR elevated`() {
        val result = recommender.isHeartRateSafe(energyProtocol, heartRate = 110)
        assertFalse(result)
    }
    
    @Test
    fun `isHeartRateSafe allows relaxation protocols when HR elevated`() {
        val result = recommender.isHeartRateSafe(relaxationProtocol, heartRate = 110)
        assertTrue(result)
    }
    
    @Test
    fun `recommend filters unsafe protocols`() {
        val protocols = listOf(relaxationProtocol, energyProtocol, advancedProtocol)
        val recommendations = recommender.recommend(
            userState = basicUserState,
            healthMetrics = HealthMetrics(heartRate = 70, hrv = 50, sleepQuality = 0.7f),
            availableProtocols = protocols,
            hourOfDay = 10
        )
        
        // Advanced protocol should be filtered (level too low)
        assertFalse(recommendations.any { it.protocol.id == "advanced" })
    }
    
    @Test
    fun `recommend returns max 5 results`() {
        val manyProtocols = (1..10).map { i ->
            relaxationProtocol.copy(id = "protocol_$i")
        }
        
        val recommendations = recommender.recommend(
            userState = basicUserState,
            healthMetrics = HealthMetrics(),
            availableProtocols = manyProtocols,
            hourOfDay = 10,
            maxResults = 5
        )
        
        assertEquals(5, recommendations.size)
    }
    
    @Test
    fun `recommend prioritizes relaxation in evening`() {
        val protocols = listOf(relaxationProtocol, energyProtocol)
        val recommendations = recommender.recommend(
            userState = basicUserState,
            healthMetrics = HealthMetrics(heartRate = 70, hrv = 50, sleepQuality = 0.7f),
            availableProtocols = protocols,
            hourOfDay = 21 // Evening
        )
        
        // Relaxation should be first in evening
        assertEquals("relax", recommendations.first().protocol.id)
    }
}
