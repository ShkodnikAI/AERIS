package com.aeris.ui.screens.session

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeris.R
import com.aeris.ui.components.BreathingAnimation
import com.aeris.ui.components.RatingStars
import com.aeris.ui.model.SessionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    protocolId: String,
    onNavigateBack: () -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val phase by viewModel.currentPhase.collectAsState()
    val phaseText by viewModel.phaseText.collectAsState()
    val countdown by viewModel.countdown.collectAsState()
    val cycle by viewModel.currentCycle.collectAsState()
    val totalCycles by viewModel.totalCycles.collectAsState()
    val showStopDialog by viewModel.showStopDialog.collectAsState()
    val showSkipWarning by viewModel.showSkipWarning.collectAsState()
    val rating by viewModel.rating.collectAsState()

    LaunchedEffect(protocolId) { viewModel.loadProtocol(protocolId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_protocols)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.requestStop() }) {
                        Icon(Icons.Default.Close, contentDescription = "Stop")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is SessionUiState.Breathing -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        BreathingAnimation(phase = phase, progress = countdown / 10f)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(phaseText, style = MaterialTheme.typography.headlineMedium)
                        Text("$countdown", style = MaterialTheme.typography.displayLarge)
                        Text("Cycle $cycle of $totalCycles", style = MaterialTheme.typography.bodyLarge)
                        if (phase == com.aeris.domain.model.Phase.HOLD || phase == com.aeris.domain.model.Phase.HOLD_EMPTY) {
                            TextButton(onClick = { viewModel.skipHold() }) {
                                Text(stringResource(R.string.skip_hold_button))
                            }
                        }
                    }
                }
                is SessionUiState.Paused -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Paused", style = MaterialTheme.typography.headlineLarge)
                        Button(onClick = { viewModel.resume() }) { Text("Resume") }
                    }
                }
                is SessionUiState.Completed -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.session_complete), style = MaterialTheme.typography.headlineLarge)
                        Text("${state.durationSec}s", style = MaterialTheme.typography.titleLarge)
                        Text(stringResource(R.string.session_rating_label))
                        RatingStars(rating = rating, onRatingChange = { viewModel.setRating(it) })
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.saveSession()
                                onNavigateBack()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.save_button))
                        }
                    }
                }
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissStopDialog() },
            title = { Text(stringResource(R.string.stop_dialog_title)) },
            confirmButton = {
                TextButton(onClick = { viewModel.finishSession(); onNavigateBack() }) {
                    Text(stringResource(R.string.stop_dialog_finish))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissStopDialog() }) {
                    Text(stringResource(R.string.stop_dialog_continue))
                }
            }
        )
    }

    if (showSkipWarning) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSkipWarning() },
            title = { Text("Skip Hold") },
            text = { Text(stringResource(R.string.skip_hold_warning)) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissSkipWarning() }) {
                    Text("OK")
                }
            }
        )
    }
}
