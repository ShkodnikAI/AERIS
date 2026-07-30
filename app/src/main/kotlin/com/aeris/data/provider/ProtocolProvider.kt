package com.aeris.data.provider

import com.aeris.domain.model.*

object ProtocolProvider {
    val allProtocols: List<Protocol> = listOf(
        // ─── Phase 1: Core 8 Protocols ─────────────────────────────

        // 1. 4-7-8 Breathing
        Protocol(
            id = "four_seven_eight",
            nameKey = "protocol_four_seven_eight_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.RESONANCE_SYNC),
            descriptionKey = "protocol_four_seven_eight_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 4, "instruction_inhale_nose"),
                BreathStep(Phase.HOLD, 7, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 8, "instruction_exhale_mouth")
            ),
            defaultCycles = 4,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1)
        ),

        // 2. Box Breathing
        Protocol(
            id = "box_breathing",
            nameKey = "protocol_box_breathing_name",
            category = Category.ENERGY_FOCUS,
            mechanisms = listOf(Mechanism.SYMPATHETIC, Mechanism.RESONANCE_SYNC),
            descriptionKey = "protocol_box_breathing_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 4, "instruction_inhale_nose"),
                BreathStep(Phase.HOLD, 4, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_mouth"),
                BreathStep(Phase.HOLD_EMPTY, 4, "instruction_hold_breath")
            ),
            defaultCycles = 5,
            sessionDurationMin = 5,
            difficulty = Difficulty.INTERMEDIATE,
            safetyRules = SafetyRules(minLevel = 1)
        ),

        // 3. Buteyko Basic
        Protocol(
            id = "buteyko_basic",
            nameKey = "protocol_buteyko_basic_name",
            category = Category.THERAPY_HEALTH,
            mechanisms = listOf(Mechanism.CO2_TRAINING, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_buteyko_basic_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 3, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_mouth"),
                BreathStep(Phase.HOLD_EMPTY, 10, "instruction_hold_breath"),
                BreathStep(Phase.INHALE, 3, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_mouth")
            ),
            defaultCycles = 3,
            sessionDurationMin = 7,
            difficulty = Difficulty.ADVANCED,
            safetyRules = SafetyRules(
                minLevel = 2,
                contraindications = listOf(Contraindication.HYPERTENSION)
            )
        ),

        // 4. Kumbhaka Advanced
        Protocol(
            id = "kumbhaka_advanced",
            nameKey = "protocol_kumbhaka_advanced_name",
            category = Category.SPIRITUAL_ADVANCED,
            mechanisms = listOf(Mechanism.HYPOXIC_ADAPTATION, Mechanism.LUNG_CAPACITY),
            descriptionKey = "protocol_kumbhaka_advanced_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 5, "instruction_inhale_nose"),
                BreathStep(Phase.HOLD, 15, "instruction_hold_mulabandha"),
                BreathStep(Phase.EXHALE, 8, "instruction_exhale_mouth"),
                BreathStep(Phase.HOLD_EMPTY, 5, "instruction_hold_breath")
            ),
            defaultCycles = 5,
            sessionDurationMin = 10,
            difficulty = Difficulty.EXPERT,
            safetyRules = SafetyRules(
                minLevel = 4,
                contraindications = listOf(
                    Contraindication.HYPERTENSION,
                    Contraindication.PREGNANCY,
                    Contraindication.CARDIAC_ISSUES
                ),
                requiresConsent = true
            )
        ),

        // 5. Diaphragmatic Breathing
        Protocol(
            id = "diaphragmatic",
            nameKey = "protocol_diaphragmatic_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_diaphragmatic_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 5, "instruction_inhale_belly"),
                BreathStep(Phase.EXHALE, 5, "instruction_exhale_belly")
            ),
            defaultCycles = 15,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1)
        ),

        // 6. Kapalabhati
        Protocol(
            id = "kapalabhati",
            nameKey = "protocol_kapalabhati_name",
            category = Category.ENERGY_FOCUS,
            mechanisms = listOf(Mechanism.SYMPATHETIC, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_kapalabhati_desc",
            steps = listOf(
                BreathStep(Phase.EXHALE, 1, "instruction_exhale_forceful"),
                BreathStep(Phase.INHALE, 1, "instruction_inhale_passive")
            ),
            defaultCycles = 60,
            sessionDurationMin = 5,
            difficulty = Difficulty.INTERMEDIATE,
            safetyRules = SafetyRules(
                minLevel = 2,
                contraindications = listOf(
                    Contraindication.HYPERTENSION,
                    Contraindication.PREGNANCY
                )
            )
        ),

        // 7. Alternate Nostril (Nadi Shodhana)
        Protocol(
            id = "alternate_nostril",
            nameKey = "protocol_alternate_nostril_name",
            category = Category.THERAPY_HEALTH,
            mechanisms = listOf(Mechanism.RESONANCE_SYNC, Mechanism.PARASYMPATHETIC),
            descriptionKey = "protocol_alternate_nostril_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 4, "instruction_inhale_left"),
                BreathStep(Phase.HOLD, 4, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_right"),
                BreathStep(Phase.HOLD_EMPTY, 4, "instruction_hold_breath"),
                BreathStep(Phase.INHALE, 4, "instruction_inhale_right"),
                BreathStep(Phase.HOLD, 4, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_left"),
                BreathStep(Phase.HOLD_EMPTY, 4, "instruction_hold_breath")
            ),
            defaultCycles = 5,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1)
        ),

        // 8. Sitali Pranayama
        Protocol(
            id = "sitali",
            nameKey = "protocol_sitali_name",
            category = Category.SPIRITUAL_ADVANCED,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.CO2_TRAINING),
            descriptionKey = "protocol_sitali_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 5, "instruction_inhale_tongue"),
                BreathStep(Phase.HOLD, 5, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 5, "instruction_exhale_nose")
            ),
            defaultCycles = 10,
            sessionDurationMin = 5,
            difficulty = Difficulty.ADVANCED,
            safetyRules = SafetyRules(
                minLevel = 3,
                requiresConsent = true
            )
        ),

        // ─── Phase 2: Research-backed Protocols ──────────────────────

        // 9. Physiological Sigh (Stanford / Huberman Lab)
        // Double inhale (short + long) through nose, extended exhale through mouth
        // Rapidly reinflates collapsed alveoli, reduces stress within 1-3 breaths
        Protocol(
            id = "physiological_sigh",
            nameKey = "protocol_physiological_sigh_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_physiological_sigh_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 2, "instruction_inhale_nose"),
                BreathStep(Phase.INHALE, 1, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_mouth")
            ),
            defaultCycles = 8,
            sessionDurationMin = 3,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1)
        ),

        // 10. Resonance Breathing (0.1 Hz / 6 breaths per minute)
        // Maximizes HRV coherence and baroreflex sensitivity
        // Reference: Front. Physiol. 2021
        Protocol(
            id = "resonance_breathing",
            nameKey = "protocol_resonance_breathing_name",
            category = Category.THERAPY_HEALTH,
            mechanisms = listOf(Mechanism.RESONANCE_SYNC, Mechanism.PARASYMPATHETIC),
            descriptionKey = "protocol_resonance_breathing_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 5, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 5, "instruction_exhale_nose")
            ),
            defaultCycles = 30,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1)
        ),

        // 11. Wim Hof Method (Basic)
        // 30-40 deep breaths + breath hold. Increases epinephrine, reduces inflammation.
        // Reference: PNAS 2014, Psychosom Med 2023
        Protocol(
            id = "wim_hof_basic",
            nameKey = "protocol_wim_hof_basic_name",
            category = Category.ENERGY_FOCUS,
            mechanisms = listOf(Mechanism.SYMPATHETIC, Mechanism.CO2_TRAINING, Mechanism.LUNG_CAPACITY),
            descriptionKey = "protocol_wim_hof_basic_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 1, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 1, "instruction_exhale_mouth")
            ),
            defaultCycles = 30,
            sessionDurationMin = 5,
            difficulty = Difficulty.INTERMEDIATE,
            safetyRules = SafetyRules(
                minLevel = 2,
                contraindications = listOf(
                    Contraindication.HYPERTENSION,
                    Contraindication.PREGNANCY,
                    Contraindication.CARDIAC_ISSUES
                ),
                requiresConsent = true
            )
        ),

        // 12. Coherent Breathing (5 breaths per minute)
        // Clinically proven to reduce anxiety and depression scores
        // Reference: Appl Psychophysiol Biofeedback 2005
        Protocol(
            id = "coherent_breathing",
            nameKey = "protocol_coherent_breathing_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.RESONANCE_SYNC, Mechanism.PARASYMPATHETIC),
            descriptionKey = "protocol_coherent_breathing_desc",
            steps = listOf(
                BreathStep(Phase.INHALE, 6, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 6, "instruction_exhale_nose")
            ),
            defaultCycles = 25,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1)
        )
    )
}
