package com.aeris.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aeris.R
import com.aeris.domain.model.Protocol

@Composable
fun ProtocolCard(
    protocol: Protocol,
    isLocked: Boolean,
    hasWarning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = !isLocked,
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = getStringResId(protocol.nameKey)),
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    if (isLocked) {
                        Text("[L]", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                    }
                    if (hasWarning) {
                        Text("[!]", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(id = getStringResId("filter_" + protocol.category.name.lowercase()))) },
                    enabled = false
                )
                AssistChip(
                    onClick = {},
                    label = { Text(protocol.difficulty.name) },
                    enabled = false
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${protocol.sessionDurationMin} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
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
        "filter_relaxation_sleep" -> R.string.filter_relaxation
        "filter_energy_focus" -> R.string.filter_energy
        "filter_therapy_health" -> R.string.filter_therapy
        "filter_spiritual_advanced" -> R.string.filter_spiritual
        else -> R.string.app_name
    }
}
