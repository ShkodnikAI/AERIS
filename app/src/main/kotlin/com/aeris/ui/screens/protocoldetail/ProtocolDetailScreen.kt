package com.aeris.ui.screens.protocoldetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeris.R
import com.aeris.domain.model.AudioType
import com.aeris.domain.model.Protocol
import com.aeris.ui.components.SafetyBanner
import com.aeris.ui.theme.SoftCyan
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolDetailScreen(
    protocolId: String,
    onNavigateBack: () -> Unit,
    onStartSession: (String) -> Unit,
    onNavigateToConsent: (String) -> Unit,
    viewModel: ProtocolDetailViewModel = hiltViewModel()
) {
    val protocol by viewModel.protocol.collectAsState()

    LaunchedEffect(protocolId) {
        viewModel.loadProtocol(protocolId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.protocol_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        protocol?.let { p ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = stringResource(id = getStringResId(p.nameKey)),
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = stringResource(id = getStringResId(p.descriptionKey)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Meta chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(p.difficulty.name) },
                        enabled = false
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("${p.sessionDurationMin} мин") },
                        enabled = false
                    )
                }

                // Audio type indicator
                AudioTypeIndicator(p.audioType)

                // Physics section
                SectionCard(
                    title = stringResource(R.string.physics_section),
                    content = stringResource(id = getStringResId(p.physicsKey))
                )

                // Rules section
                SectionCard(
                    title = stringResource(R.string.rules_section),
                    content = stringResource(id = getStringResId(p.rulesKey))
                )

                // Safety warning if applicable
                if (p.safetyRules.requiresConsent) {
                    SafetyBanner(
                        message = stringResource(R.string.safety_warning_consent)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Start button
                Button(
                    onClick = {
                        if (p.safetyRules.requiresConsent) {
                            onNavigateToConsent(p.id)
                        } else {
                            onStartSession(p.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCyan)
                ) {
                    Text(
                        stringResource(R.string.start_session),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SectionCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AudioTypeIndicator(audioType: AudioType) {
    val (label, color) = when (audioType) {
        AudioType.BINAURAL -> stringResource(R.string.audio_binaural) to MaterialTheme.colorScheme.tertiary
        AudioType.MEDITATION -> stringResource(R.string.audio_meditation) to MaterialTheme.colorScheme.secondary
        AudioType.AFFIRMATION -> stringResource(R.string.audio_affirmation) to MaterialTheme.colorScheme.primary
        AudioType.ENERGETIC -> stringResource(R.string.audio_energetic) to MaterialTheme.colorScheme.error
        AudioType.NONE -> stringResource(R.string.audio_none) to MaterialTheme.colorScheme.outline
    }
    AssistChip(
        onClick = {},
        label = { Text(label, color = color) },
        enabled = false,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
    )
}

fun getStringResId(key: String): Int {
    return when (key) {
        "protocol_four_seven_eight_name" -> R.string.protocol_four_seven_eight_name
        "protocol_box_breathing_name" -> R.string.protocol_box_breathing_name
        "protocol_buteyko_basic_name" -> R.string.protocol_buteyko_basic_name
        "protocol_kumbhaka_advanced_name" -> R.string.protocol_kumbhaka_advanced_name
        "protocol_diaphragmatic_name" -> R.string.protocol_diaphragmatic_name
        "protocol_kapalabhati_name" -> R.string.protocol_kapalabhati_name
        "protocol_alternate_nostril_name" -> R.string.protocol_alternate_nostril_name
        "protocol_sitali_name" -> R.string.protocol_sitali_name
        "protocol_physiological_sigh_name" -> R.string.protocol_physiological_sigh_name
        "protocol_resonance_breathing_name" -> R.string.protocol_resonance_breathing_name
        "protocol_wim_hof_basic_name" -> R.string.protocol_wim_hof_basic_name
        "protocol_coherent_breathing_name" -> R.string.protocol_coherent_breathing_name
        "protocol_four_seven_eight_desc" -> R.string.protocol_four_seven_eight_desc
        "protocol_box_breathing_desc" -> R.string.protocol_box_breathing_desc
        "protocol_buteyko_basic_desc" -> R.string.protocol_buteyko_basic_desc
        "protocol_kumbhaka_advanced_desc" -> R.string.protocol_kumbhaka_advanced_desc
        "protocol_diaphragmatic_desc" -> R.string.protocol_diaphragmatic_desc
        "protocol_kapalabhati_desc" -> R.string.protocol_kapalabhati_desc
        "protocol_alternate_nostril_desc" -> R.string.protocol_alternate_nostril_desc
        "protocol_sitali_desc" -> R.string.protocol_sitali_desc
        "protocol_physiological_sigh_desc" -> R.string.protocol_physiological_sigh_desc
        "protocol_resonance_breathing_desc" -> R.string.protocol_resonance_breathing_desc
        "protocol_wim_hof_basic_desc" -> R.string.protocol_wim_hof_basic_desc
        "protocol_coherent_breathing_desc" -> R.string.protocol_coherent_breathing_desc
        "physics_four_seven_eight" -> R.string.physics_four_seven_eight
        "physics_box_breathing" -> R.string.physics_box_breathing
        "physics_buteyko_basic" -> R.string.physics_buteyko_basic
        "physics_kumbhaka_advanced" -> R.string.physics_kumbhaka_advanced
        "physics_diaphragmatic" -> R.string.physics_diaphragmatic
        "physics_kapalabhati" -> R.string.physics_kapalabhati
        "physics_alternate_nostril" -> R.string.physics_alternate_nostril
        "physics_sitali" -> R.string.physics_sitali
        "physics_physiological_sigh" -> R.string.physics_physiological_sigh
        "physics_resonance_breathing" -> R.string.physics_resonance_breathing
        "physics_wim_hof_basic" -> R.string.physics_wim_hof_basic
        "physics_coherent_breathing" -> R.string.physics_coherent_breathing
        "rules_four_seven_eight" -> R.string.rules_four_seven_eight
        "rules_box_breathing" -> R.string.rules_box_breathing
        "rules_buteyko_basic" -> R.string.rules_buteyko_basic
        "rules_kumbhaka_advanced" -> R.string.rules_kumbhaka_advanced
        "rules_diaphragmatic" -> R.string.rules_diaphragmatic
        "rules_kapalabhati" -> R.string.rules_kapalabhati
        "rules_alternate_nostril" -> R.string.rules_alternate_nostril
        "rules_sitali" -> R.string.rules_sitali
        "rules_physiological_sigh" -> R.string.rules_physiological_sigh
        "rules_resonance_breathing" -> R.string.rules_resonance_breathing
        "rules_wim_hof_basic" -> R.string.rules_wim_hof_basic
        "rules_coherent_breathing" -> R.string.rules_coherent_breathing
        else -> R.string.app_name
    }
}
