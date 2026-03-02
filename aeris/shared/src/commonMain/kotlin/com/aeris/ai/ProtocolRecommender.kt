package com.aeris.ai

import com.aeris.domain.model.*

/**
 * Protocol Recommender - AI engine for recommending breathing protocols
 * 
 * Uses NSI and BCI scores along with user profile to filter and rank
 * protocols for optimal safety and relevance.
 */
class ProtocolRecommender(
    private val nsiCalculator: NsiCalculator = NsiCalculator(),
    private val bciCalculator: BciCalculator = BciCalculator()
) {
    
    /**
     * Recommend protocols based on user state and available protocols.
     * 
     * Algorithm:
     * 1. Filter by safety (level, contraindications, mechanisms)
     * 2. Filter by NSI compatibility (no sympathetic if hyperaroused)
     * 3. Sort by relevance (BCI, time of day, history)
     * 4. Return top 5 for UI
     */
    fun recommend(
        userState: UserState,
        healthMetrics: HealthMetrics,
        availableProtocols: List<Protocol>,
        hourOfDay: Int,
        maxResults: Int = 5
    ): List<RecommendedProtocol> {
        val nervousState = nsiCalculator.calculate(healthMetrics, hourOfDay)
        
        return availableProtocols
            // Step 1: Safety filter
            .filter { protocol -> isSafeForUser(protocol, userState) }
            // Step 2: NSI compatibility filter
            .filter { protocol -> isCompatibleWithNervousState(protocol, nervousState, userState.level) }
            // Step 3: HR-based safety check
            .filter { protocol -> isHeartRateSafe(protocol, healthMetrics.heartRate) }
            // Step 4: Calculate relevance score and sort
            .map { protocol ->
                val relevanceScore = calculateRelevanceScore(
                    protocol = protocol,
                    userState = userState,
                    nervousState = nervousState,
                    hourOfDay = hourOfDay
                )
                RecommendedProtocol(
                    protocol = protocol,
                    relevanceScore = relevanceScore,
                    matchReason = getMatchReason(protocol, nervousState, hourOfDay, userState.preferredLanguage)
                )
            }
            .sortedByDescending { it.relevanceScore }
            .take(maxResults)
    }
    
    /**
     * Check if protocol is safe for user based on level and contraindications.
     */
    fun isSafeForUser(protocol: Protocol, userState: UserState): Boolean {
        // Level check
        if (protocol.safetyRules.minLevel > userState.level) {
            return false
        }
        
        // Contraindication check
        val hasContraindication = userState.contraindications.any { 
            it in protocol.safetyRules.contraindications 
        }
        if (hasContraindication) {
            return false
        }
        
        // Advanced consent check
        if (protocol.safetyRules.requiresConsent && !userState.hasAcceptedAdvancedConsent) {
            return false
        }
        
        // Hypoxic adaptation only for level 3+
        if (PhysiologicalMechanism.HYPOXIC_ADAPTATION in protocol.mechanisms && userState.level < 3) {
            return false
        }
        
        return true
    }
    
    /**
     * Check if protocol is compatible with current nervous state.
     * Prevents recommending stimulating exercises when hyperaroused,
     * or sedating exercises when hypoaroused.
     */
    fun isCompatibleWithNervousState(
        protocol: Protocol,
        nervousState: NervousState,
        userLevel: Int
    ): Boolean {
        return when (nervousState) {
            NervousState.HYPERAROUSAL -> {
                // Don't recommend sympathetic stimulation when already aroused
                PhysiologicalMechanism.SYMPATHETIC_STIMULATION !in protocol.mechanisms
            }
            NervousState.HYPOAROUSAL -> {
                // Allow more flexibility at higher levels
                if (userLevel >= 3) true
                else PhysiologicalMechanism.PARASYMPATHETIC_ACTIVATION !in protocol.mechanisms
            }
            NervousState.BALANCED -> true
        }
    }
    
    /**
     * Check heart rate safety for protocol.
     */
    fun isHeartRateSafe(protocol: Protocol, heartRate: Int): Boolean {
        if (heartRate > protocol.safetyRules.hrThreshold) {
            // Only allow relaxation protocols when HR is elevated
            return protocol.category == ProtocolCategory.RELAXATION_SLEEP &&
                   PhysiologicalMechanism.PARASYMPATHETIC_ACTIVATION in protocol.mechanisms
        }
        return true
    }
    
    /**
     * Calculate relevance score for ranking.
     */
    private fun calculateRelevanceScore(
        protocol: Protocol,
        userState: UserState,
        nervousState: NervousState,
        hourOfDay: Int
    ): Float {
        var score = 0.5f // Base score
        
        // Category preference boost
        if (userState.preferredCategory == protocol.category) {
            score += 0.2f
        }
        
        // Time of day boost
        score += getTimeOfDayBoost(protocol.category, hourOfDay)
        
        // Nervous state alignment boost
        score += getNervousStateBoost(protocol, nervousState)
        
        // Experience with protocol (slight preference for tried protocols)
        if (protocol.id in userState.completedProtocolIds) {
            score += 0.05f
        }
        
        // Difficulty match (prefer appropriate difficulty for level)
        score += getDifficultyBoost(protocol.difficulty, userState.level)
        
        return score.coerceIn(0f, 1f)
    }
    
    /**
     * Get boost based on time of day and protocol category.
     */
    private fun getTimeOfDayBoost(category: ProtocolCategory, hourOfDay: Int): Float {
        return when (category) {
            ProtocolCategory.RELAXATION_SLEEP -> {
                when (hourOfDay) {
                    in 20..23, in 0..6 -> 0.15f  // Evening/night: strong preference
                    in 12..15 -> 0.05f          // Post-lunch: slight preference
                    else -> 0f
                }
            }
            ProtocolCategory.ENERGY_FOCUS -> {
                when (hourOfDay) {
                    in 6..10 -> 0.15f           // Morning: strong preference
                    in 14..16 -> 0.1f           // Afternoon dip: moderate preference
                    in 20..23, in 0..5 -> -0.1f // Night: avoid
                    else -> 0f
                }
            }
            ProtocolCategory.THERAPY_HEALTH -> 0f // Neutral throughout day
            ProtocolCategory.SPIRITUAL_ADVANCED -> {
                when (hourOfDay) {
                    in 5..7 -> 0.1f             // Early morning: traditional preference
                    in 20..22 -> 0.05f          // Evening: slight preference
                    else -> 0f
                }
            }
        }
    }
    
    /**
     * Get boost based on nervous state alignment with protocol mechanisms.
     */
    private fun getNervousStateBoost(protocol: Protocol, nervousState: NervousState): Float {
        return when (nervousState) {
            NervousState.HYPERAROUSAL -> {
                if (PhysiologicalMechanism.PARASYMPATHETIC_ACTIVATION in protocol.mechanisms) 0.15f
                else 0f
            }
            NervousState.HYPOAROUSAL -> {
                if (PhysiologicalMechanism.SYMPATHETIC_STIMULATION in protocol.mechanisms) 0.15f
                else 0f
            }
            NervousState.BALANCED -> 0f
        }
    }
    
    /**
     * Get boost for difficulty matching user level.
     */
    private fun getDifficultyBoost(difficulty: Difficulty, userLevel: Int): Float {
        val idealDifficulty = when (userLevel) {
            1 -> Difficulty.BEGINNER
            2 -> Difficulty.INTERMEDIATE
            3, 4 -> Difficulty.ADVANCED
            else -> Difficulty.EXPERT
        }
        
        return when {
            difficulty == idealDifficulty -> 0.1f
            difficulty.ordinal == idealDifficulty.ordinal - 1 -> 0.05f  // Slightly easier
            difficulty.ordinal == idealDifficulty.ordinal + 1 -> 0.05f  // Slightly harder (challenge)
            else -> 0f
        }
    }
    
    /**
     * Generate match reason for UI display.
     */
    private fun getMatchReason(
        protocol: Protocol,
        nervousState: NervousState,
        hourOfDay: Int,
        languageCode: String
    ): String {
        // Check nervous state match
        val nervousMatch = when {
            nervousState == NervousState.HYPERAROUSAL && 
            PhysiologicalMechanism.PARASYMPATHETIC_ACTIVATION in protocol.mechanisms ->
                if (languageCode == "ru") "Поможет расслабиться" else "Helps with relaxation"
            nervousState == NervousState.HYPOAROUSAL && 
            PhysiologicalMechanism.SYMPATHETIC_STIMULATION in protocol.mechanisms ->
                if (languageCode == "ru") "Повысит энергию" else "Boosts energy"
            else -> null
        }
        if (nervousMatch != null) return nervousMatch
        
        // Check time of day match
        val timeMatch = when (protocol.category) {
            ProtocolCategory.RELAXATION_SLEEP -> if (hourOfDay in 20..23 || hourOfDay in 0..6) {
                if (languageCode == "ru") "Идеально для вечера" else "Perfect for evening"
            } else null
            ProtocolCategory.ENERGY_FOCUS -> if (hourOfDay in 6..10) {
                if (languageCode == "ru") "Отлично для утра" else "Great for morning"
            } else null
            else -> null
        }
        if (timeMatch != null) return timeMatch
        
        // Default
        return if (languageCode == "ru") "Рекомендовано для вас" else "Recommended for you"
    }
    
    /**
     * Get safety modifications for a protocol based on user state.
     */
    fun getSafetyModifications(
        protocol: Protocol,
        userState: UserState,
        healthMetrics: HealthMetrics
    ): List<SafetyModification> {
        val modifications = mutableListOf<SafetyModification>()
        
        // Reduce hold duration for beginners or elevated HR
        if (userState.level <= 2 || healthMetrics.heartRate > 90) {
            val maxHold = if (healthMetrics.heartRate > 100) 15 else protocol.safetyRules.maxHoldForBeginners
            val holdsToModify = protocol.steps.filter { 
                (it.phase == BreathingPhase.HOLD_IN || it.phase == BreathingPhase.HOLD_OUT) &&
                it.durationSeconds > maxHold 
            }
            
            holdsToModify.forEach { step ->
                modifications.add(
                    SafetyModification(
                        description = LocalizedString(
                            en = "Hold duration reduced for safety",
                            ru = "Задержка уменьшена для безопасности"
                        ),
                        originalValue = "${step.durationSeconds.toInt()}s",
                        modifiedValue = "${maxHold}s"
                    )
                )
            }
        }
        
        return modifications
    }
}

/**
 * Protocol with relevance score and match reason.
 */
data class RecommendedProtocol(
    val protocol: Protocol,
    val relevanceScore: Float,
    val matchReason: String
)
