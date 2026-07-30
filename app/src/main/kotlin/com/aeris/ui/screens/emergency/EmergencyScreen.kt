package com.aeris.ui.screens.emergency

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aeris.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProtocols: () -> Unit
) {
    var remaining by remember { mutableIntStateOf(60) }
    var isInhale by remember { mutableStateOf(true) }
    var finished by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isInhale) 1.0f else 0.3f,
        animationSpec = tween(4000, easing = LinearEasing),
        label = "emergency_scale"
    )

    LaunchedEffect(Unit) {
        while (remaining > 0 && !finished) {
            isInhale = true
            delay(4000)
            isInhale = false
            delay(6000)
            remaining -= 10
        }
        finished = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_emergency)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (!finished) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (isInhale) stringResource(R.string.phase_inhale) else stringResource(R.string.phase_exhale),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text("$remaining", style = MaterialTheme.typography.displayLarge)
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(stringResource(R.string.emergency_feeling_question), style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = onNavigateToProtocols) {
                        Text(stringResource(R.string.emergency_continue_full))
                    }
                    OutlinedButton(onClick = onNavigateBack) {
                        Text(stringResource(R.string.stop_dialog_continue))
                    }
                }
            }
        }
    }
}
