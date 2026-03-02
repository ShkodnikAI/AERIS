package com.aeris.android.data.repository

import android.content.Context
import com.aeris.android.data.local.dao.ProtocolDao
import com.aeris.domain.model.*
import com.aeris.domain.repository.ProtocolRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Implementation of ProtocolRepository.
 * Loads protocols from assets JSON files.
 */
class ProtocolRepositoryImpl(
    private val context: Context,
    private val protocolDao: ProtocolDao
) : ProtocolRepository {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    private var cachedProtocols: List<Protocol>? = null
    
    override suspend fun getAllProtocols(): List<Protocol> = withContext(Dispatchers.IO) {
        cachedProtocols ?: loadProtocolsFromAssets().also { cachedProtocols = it }
    }
    
    override suspend fun getProtocolById(id: String): Protocol? {
        return getAllProtocols().find { it.id == id }
    }
    
    override suspend fun getProtocolsByCategory(category: ProtocolCategory): List<Protocol> {
        return getAllProtocols().filter { it.category == category }
    }
    
    override suspend fun getProtocolsByDifficulty(difficulty: Difficulty): List<Protocol> {
        return getAllProtocols().filter { it.difficulty == difficulty }
    }
    
    override suspend fun getProtocolsForLevel(userLevel: Int): List<Protocol> {
        return getAllProtocols().filter { it.safetyRules.minLevel <= userLevel }
    }
    
    override suspend fun searchProtocols(query: String, languageCode: String): List<Protocol> {
        val lowerQuery = query.lowercase()
        return getAllProtocols().filter { protocol ->
            val name = protocol.name.get(languageCode).lowercase()
            val desc = protocol.description.get(languageCode).lowercase()
            name.contains(lowerQuery) || desc.contains(lowerQuery)
        }
    }
    
    private suspend fun loadProtocolsFromAssets(): List<Protocol> = withContext(Dispatchers.IO) {
        val protocols = mutableListOf<Protocol>()
        
        try {
            // Load registry to get protocol IDs
            val registryJson = context.assets.open("protocols/registry.json")
                .bufferedReader()
                .use { it.readText() }
            
            val registry = json.decodeFromString<ProtocolRegistry>(registryJson)
            
            // Load each protocol
            for (entry in registry.protocols) {
                try {
                    val protocolJson = context.assets.open("protocols/${entry.id}.json")
                        .bufferedReader()
                        .use { it.readText() }
                    
                    val protocol = json.decodeFromString<Protocol>(protocolJson)
                    protocols.add(protocol)
                } catch (e: Exception) {
                    Timber.tag("AERIS").w(e, "Failed to load protocol: ${entry.id}")
                }
            }
        } catch (e: Exception) {
            Timber.tag("AERIS").e(e, "Failed to load protocols from assets")
            // Return hardcoded fallback protocols
            protocols.addAll(getFallbackProtocols())
        }
        
        if (protocols.isEmpty()) {
            protocols.addAll(getFallbackProtocols())
        }
        
        protocols
    }
    
    private fun getFallbackProtocols(): List<Protocol> {
        return listOf(
            Protocol(
                id = "four_seven_eight",
                name = LocalizedString("4-7-8 Breathing", "Дыхание 4-7-8"),
                description = LocalizedString(
                    "Dr. Andrew Weil's relaxation technique for sleep and anxiety.",
                    "Техника релаксации доктора Эндрю Вейла для сна и снятия тревожности."
                ),
                category = ProtocolCategory.RELAXATION_SLEEP,
                mechanisms = listOf(
                    PhysiologicalMechanism.PARASYMPATHETIC_ACTIVATION,
                    PhysiologicalMechanism.RESONANCE_SYNCHRONIZATION
                ),
                steps = listOf(
                    BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("Breathe in through nose", "Вдохните через нос")),
                    BreathingStep(BreathingPhase.HOLD_IN, 7f, LocalizedString("Hold gently", "Мягко задержите")),
                    BreathingStep(BreathingPhase.EXHALE, 8f, LocalizedString("Exhale through mouth", "Выдохните через рот"))
                ),
                sessionDurationMinutes = 5,
                difficulty = Difficulty.BEGINNER,
                safetyRules = SafetyRules(minLevel = 1, hrThreshold = 100),
                animation = AnimationConfig(AnimationType.CIRCLE, true, true)
            ),
            Protocol(
                id = "box_breathing",
                name = LocalizedString("Box Breathing", "Квадратное дыхание"),
                description = LocalizedString(
                    "Navy SEALs technique for focus and stress management.",
                    "Техника морских котиков для концентрации и управления стрессом."
                ),
                category = ProtocolCategory.ENERGY_FOCUS,
                mechanisms = listOf(
                    PhysiologicalMechanism.SYMPATHETIC_STIMULATION,
                    PhysiologicalMechanism.RESONANCE_SYNCHRONIZATION
                ),
                steps = listOf(
                    BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("Breathe in", "Вдохните")),
                    BreathingStep(BreathingPhase.HOLD_IN, 4f, LocalizedString("Hold", "Задержите")),
                    BreathingStep(BreathingPhase.EXHALE, 4f, LocalizedString("Breathe out", "Выдохните")),
                    BreathingStep(BreathingPhase.HOLD_OUT, 4f, LocalizedString("Hold empty", "Пауза"))
                ),
                sessionDurationMinutes = 5,
                difficulty = Difficulty.INTERMEDIATE,
                safetyRules = SafetyRules(minLevel = 1, hrThreshold = 100),
                animation = AnimationConfig(AnimationType.SQUARE, true, true)
            ),
            Protocol(
                id = "progressive_hold",
                name = LocalizedString("Progressive Breath Hold", "Прогрессивная задержка"),
                description = LocalizedString(
                    "Gradually increasing breath hold intervals for CO2 tolerance.",
                    "Постепенно увеличивающиеся интервалы задержки для толерантности к CO2."
                ),
                category = ProtocolCategory.THERAPY_HEALTH,
                mechanisms = listOf(
                    PhysiologicalMechanism.CO2_TRAINING,
                    PhysiologicalMechanism.LUNG_CAPACITY_INCREASE
                ),
                steps = listOf(
                    BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("Breathe in", "Вдохните")),
                    BreathingStep(BreathingPhase.HOLD_IN, 10f, LocalizedString("Hold - first interval", "Задержка - первый интервал")),
                    BreathingStep(BreathingPhase.EXHALE, 6f, LocalizedString("Exhale slowly", "Медленно выдохните")),
                    BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("Recovery breath", "Восстановительный вдох")),
                    BreathingStep(BreathingPhase.EXHALE, 4f, LocalizedString("Normal exhale", "Обычный выдох")),
                    BreathingStep(BreathingPhase.INHALE, 4f, LocalizedString("Prepare", "Подготовьтесь")),
                    BreathingStep(BreathingPhase.HOLD_IN, 15f, LocalizedString("Hold - second interval (+5s)", "Задержка - второй интервал (+5с)")),
                    BreathingStep(BreathingPhase.EXHALE, 6f, LocalizedString("Exhale slowly", "Медленно выдохните"))
                ),
                sessionDurationMinutes = 7,
                difficulty = Difficulty.INTERMEDIATE,
                safetyRules = SafetyRules(
                    minLevel = 2,
                    contraindications = listOf(Contraindication.HYPERTENSION, Contraindication.HEART_DISEASE),
                    hrThreshold = 90
                ),
                animation = AnimationConfig(AnimationType.WAVE, true, true)
            )
        )
    }
}

@kotlinx.serialization.Serializable
private data class ProtocolRegistry(
    val protocols: List<ProtocolEntry>,
    val version: String = "1.0.0"
)

@kotlinx.serialization.Serializable
private data class ProtocolEntry(
    val id: String,
    val category: String,
    val mechanisms: List<String>,
    val difficulty: String
)
