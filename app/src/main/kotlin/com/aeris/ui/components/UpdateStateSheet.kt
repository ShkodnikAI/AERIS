package com.aeris.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aeris.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateStateSheet(
    initialHr: Int,
    initialHrv: Int,
    initialSleep: Float,
    onUpdate: (hr: Int, hrv: Int, sleep: Float) -> Unit,
    onDismiss: () -> Unit
) {
    var hr by remember { mutableIntStateOf(initialHr) }
    var hrv by remember { mutableIntStateOf(initialHrv) }
    var sleep by remember { mutableFloatStateOf(initialSleep) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.update_state), style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = hr.toString(),
                onValueChange = { hr = it.toIntOrNull() ?: 70 },
                label = { Text(stringResource(R.string.hr_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = hrv.toString(),
                onValueChange = { hrv = it.toIntOrNull() ?: 50 },
                label = { Text(stringResource(R.string.hrv_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(stringResource(R.string.sleep_quality_label))
            Slider(
                value = sleep,
                onValueChange = { sleep = it },
                valueRange = 0f..1f,
                steps = 9
            )
            Button(
                onClick = { onUpdate(hr, hrv, sleep) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_button))
            }
        }
    }
}
