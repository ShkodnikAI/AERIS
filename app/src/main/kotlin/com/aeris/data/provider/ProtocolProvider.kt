package com.aeris.data.provider

import com.aeris.domain.model.*

object ProtocolProvider {
    val allProtocols: List<Protocol> = listOf(
        // ═══════════════════════════════════════════════════════════
        // PHASE 1: CORE 8 PROTOCOLS
        // ═══════════════════════════════════════════════════════════

        // 1. 4-7-8 Breathing (Andrew Weil)
        // Physics: Extended exhale (8s) activates vagus nerve via pulmonary stretch receptors.
        //         Increases parasympathetic tone, reduces HR and BP within 3-5 cycles.
        // Audio: Binaural alpha 8Hz for deep relaxation
        Protocol(
            id = "four_seven_eight",
            nameKey = "protocol_four_seven_eight_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.RESONANCE_SYNC),
            descriptionKey = "protocol_four_seven_eight_desc",
            physicsKey = "physics_four_seven_eight",
            rulesKey = "rules_four_seven_eight",
            steps = listOf(
                BreathStep(Phase.INHALE, 4, "instruction_inhale_nose"),
                BreathStep(Phase.HOLD, 7, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 8, "instruction_exhale_mouth")
            ),
            defaultCycles = 4,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.BINAURAL
        ),

        // 2. Box Breathing (Navy SEALs)
        // Physics: Equal phases (4-4-4-4) synchronize respiratory and cardiac rhythms.
        //         Maximizes baroreflex sensitivity and HRV coherence.
        // Audio: None (requires pure focus)
        Protocol(
            id = "box_breathing",
            nameKey = "protocol_box_breathing_name",
            category = Category.ENERGY_FOCUS,
            mechanisms = listOf(Mechanism.SYMPATHETIC, Mechanism.RESONANCE_SYNC),
            descriptionKey = "protocol_box_breathing_desc",
            physicsKey = "physics_box_breathing",
            rulesKey = "rules_box_breathing",
            steps = listOf(
                BreathStep(Phase.INHALE, 4, "instruction_inhale_nose"),
                BreathStep(Phase.HOLD, 4, "instruction_hold_breath"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_mouth"),
                BreathStep(Phase.HOLD_EMPTY, 4, "instruction_hold_breath")
            ),
            defaultCycles = 5,
            sessionDurationMin = 5,
            difficulty = Difficulty.INTERMEDIATE,
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.NONE
        ),

        // 3. Buteyko Basic
        // Physics: Controlled hypoventilation raises CO2 tolerance via Bohr effect.
        //         Higher CO2 improves O2 release from hemoglobin to tissues.
        // Audio: None (therapeutic precision required)
        Protocol(
            id = "buteyko_basic",
            nameKey = "protocol_buteyko_basic_name",
            category = Category.THERAPY_HEALTH,
            mechanisms = listOf(Mechanism.CO2_TRAINING, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_buteyko_basic_desc",
            physicsKey = "physics_buteyko_basic",
            rulesKey = "rules_buteyko_basic",
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
            ),
            audioType = AudioType.NONE
        ),

        // 4. Kumbhaka Advanced
        // Physics: Breath retention increases PaCO2, dilates cerebral vessels,
        //         stimulates erythropoietin production. Activates dive reflex.
        // Audio: Meditative drones for deep altered states
        Protocol(
            id = "kumbhaka_advanced",
            nameKey = "protocol_kumbhaka_advanced_name",
            category = Category.SPIRITUAL_ADVANCED,
            mechanisms = listOf(Mechanism.HYPOXIC_ADAPTATION, Mechanism.LUNG_CAPACITY),
            descriptionKey = "protocol_kumbhaka_advanced_desc",
            physicsKey = "physics_kumbhaka_advanced",
            rulesKey = "rules_kumbhaka_advanced",
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
            ),
            audioType = AudioType.MEDITATION
        ),

        // 5. Diaphragmatic Breathing
        // Physics: Diaphragm contraction provides 75% of tidal volume.
        //         Slowing to 6 breaths/min maximizes HRV amplitude via RSA.
        // Audio: Binaural theta 6Hz for parasympathetic activation
        Protocol(
            id = "diaphragmatic",
            nameKey = "protocol_diaphragmatic_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_diaphragmatic_desc",
            physicsKey = "physics_diaphragmatic",
            rulesKey = "rules_diaphragmatic",
            steps = listOf(
                BreathStep(Phase.INHALE, 5, "instruction_inhale_belly"),
                BreathStep(Phase.EXHALE, 5, "instruction_exhale_belly")
            ),
            defaultCycles = 15,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.BINAURAL
        ),

        // 6. Kapalabhati
        // Physics: Forceful exhalations create rhythmic abdominal pressure waves,
        //         stimulating sympathetic discharge. Increases metabolic rate 15-20%.
        // Audio: Energetic Tibetan bowls for activation
        Protocol(
            id = "kapalabhati",
            nameKey = "protocol_kapalabhati_name",
            category = Category.ENERGY_FOCUS,
            mechanisms = listOf(Mechanism.SYMPATHETIC, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_kapalabhati_desc",
            physicsKey = "physics_kapalabhati",
            rulesKey = "rules_kapalabhati",
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
            ),
            audioType = AudioType.ENERGETIC
        ),

        // 7. Alternate Nostril (Nadi Shodhana)
        // Physics: Left nostril breathing activates right hemisphere (parasympathetic).
        //         Right nostril activates left hemisphere (sympathetic). Balances ANS.
        // Audio: TTS affirmations for nostril switching guidance
        Protocol(
            id = "alternate_nostril",
            nameKey = "protocol_alternate_nostril_name",
            category = Category.THERAPY_HEALTH,
            mechanisms = listOf(Mechanism.RESONANCE_SYNC, Mechanism.PARASYMPATHETIC),
            descriptionKey = "protocol_alternate_nostril_desc",
            physicsKey = "physics_alternate_nostril",
            rulesKey = "rules_alternate_nostril",
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
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.AFFIRMATION
        ),

        // 8. Sitali Pranayama
        // Physics: Inhalation through curled tongue cools venous blood in carotid sinus,
        //         triggering parasympathetic dive reflex. Reduces core temperature.
        // Audio: Binaural alpha for cooling relaxation
        Protocol(
            id = "sitali",
            nameKey = "protocol_sitali_name",
            category = Category.SPIRITUAL_ADVANCED,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.CO2_TRAINING),
            descriptionKey = "protocol_sitali_desc",
            physicsKey = "physics_sitali",
            rulesKey = "rules_sitali",
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
            ),
            audioType = AudioType.BINAURAL
        ),

        // ═══════════════════════════════════════════════════════════
        // PHASE 2: RESEARCH-BACKED PROTOCOLS
        // ═══════════════════════════════════════════════════════════

        // 9. Physiological Sigh (Stanford / Huberman Lab)
        // Physics: Double inhale reinflates collapsed alveoli, increasing surface area
        //         for gas exchange by 20-30%. Rapidly offloads CO2 and resets lung volume.
        // Audio: TTS guided "inhale-inhale-exhale" cues
        Protocol(
            id = "physiological_sigh",
            nameKey = "protocol_physiological_sigh_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.PARASYMPATHETIC, Mechanism.RESPIRATORY_MUSCLE),
            descriptionKey = "protocol_physiological_sigh_desc",
            physicsKey = "physics_physiological_sigh",
            rulesKey = "rules_physiological_sigh",
            steps = listOf(
                BreathStep(Phase.INHALE, 2, "instruction_inhale_nose"),
                BreathStep(Phase.INHALE, 1, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 4, "instruction_exhale_mouth")
            ),
            defaultCycles = 8,
            sessionDurationMin = 3,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.AFFIRMATION
        ),

        // 10. Resonance Breathing (0.1 Hz / 6 breaths per minute)
        // Physics: Frequency 0.1 Hz matches baroreceptor resonance frequency.
        //          Maximizes HRV amplitude and baroreflex sensitivity (Front. Physiol. 2021).
        // Audio: Binaural theta 6Hz for resonance entrainment
        Protocol(
            id = "resonance_breathing",
            nameKey = "protocol_resonance_breathing_name",
            category = Category.THERAPY_HEALTH,
            mechanisms = listOf(Mechanism.RESONANCE_SYNC, Mechanism.PARASYMPATHETIC),
            descriptionKey = "protocol_resonance_breathing_desc",
            physicsKey = "physics_resonance_breathing",
            rulesKey = "rules_resonance_breathing",
            steps = listOf(
                BreathStep(Phase.INHALE, 5, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 5, "instruction_exhale_nose")
            ),
            defaultCycles = 30,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.BINAURAL
        ),

        // 11. Wim Hof Method (Basic)
        // Physics: Hyperventilation reduces PaCO2 causing respiratory alkalosis.
        //          Breath hold on exhale spikes adrenaline 2-3x (PNAS 2014).
        //          Increases anti-inflammatory IL-10, reduces TNF-alpha.
        // Audio: Energetic sounds for sympathetic activation
        Protocol(
            id = "wim_hof_basic",
            nameKey = "protocol_wim_hof_basic_name",
            category = Category.ENERGY_FOCUS,
            mechanisms = listOf(Mechanism.SYMPATHETIC, Mechanism.CO2_TRAINING, Mechanism.LUNG_CAPACITY),
            descriptionKey = "protocol_wim_hof_basic_desc",
            physicsKey = "physics_wim_hof_basic",
            rulesKey = "rules_wim_hof_basic",
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
            ),
            audioType = AudioType.ENERGETIC
        ),

        // 12. Coherent Breathing (5 breaths per minute)
        // Physics: 5-6 breaths/min creates stable cardiorespiratory coherence.
        //          Clinically proven to reduce anxiety and depression scores.
        // Audio: Binaural alpha 8Hz for coherence stabilization
        Protocol(
            id = "coherent_breathing",
            nameKey = "protocol_coherent_breathing_name",
            category = Category.RELAXATION_SLEEP,
            mechanisms = listOf(Mechanism.RESONANCE_SYNC, Mechanism.PARASYMPATHETIC),
            descriptionKey = "protocol_coherent_breathing_desc",
            physicsKey = "physics_coherent_breathing",
            rulesKey = "rules_coherent_breathing",
            steps = listOf(
                BreathStep(Phase.INHALE, 6, "instruction_inhale_nose"),
                BreathStep(Phase.EXHALE, 6, "instruction_exhale_nose")
            ),
            defaultCycles = 25,
            sessionDurationMin = 5,
            difficulty = Difficulty.BEGINNER,
            safetyRules = SafetyRules(minLevel = 1),
            audioType = AudioType.BINAURAL
        )
    )
}
