package com.aeris.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeris.R
import com.aeris.domain.model.Badge
import com.aeris.ui.components.*
import com.aeris.ui.model.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_profile)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> LoadingShimmer(modifier = Modifier.padding(padding))
            is UiState.Success -> {
                val data = state.data
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("A", style = MaterialTheme.typography.displayMedium)
                                }
                            }
                            Text("Level ${data.level}", style = MaterialTheme.typography.titleLarge)
                            Text("Streak: ${data.streak} days", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(R.string.profile_health_section), style = MaterialTheme.typography.titleMedium)
                                OutlinedTextField(
                                    value = data.hr.toString(),
                                    onValueChange = { viewModel.updateHr(it.toIntOrNull() ?: 70) },
                                    label = { Text(stringResource(R.string.hr_label)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = data.hrv.toString(),
                                    onValueChange = { viewModel.updateHrv(it.toIntOrNull() ?: 50) },
                                    label = { Text(stringResource(R.string.hrv_label)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = data.boltScore.toString(),
                                    onValueChange = { viewModel.updateBolt(it.toIntOrNull() ?: 20) },
                                    label = { Text(stringResource(R.string.bolt_label)) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(stringResource(R.string.sleep_quality_label))
                                Slider(
                                    value = data.sleepQuality,
                                    onValueChange = { viewModel.updateSleep(it) },
                                    valueRange = 0f..1f,
                                    steps = 9
                                )
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(stringResource(R.string.profile_contraindications), style = MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = data.hasHypertension, onCheckedChange = { viewModel.toggleHypertension(it) })
                                    Text(stringResource(R.string.contraindication_hypertension))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = data.hasPregnancy, onCheckedChange = { viewModel.togglePregnancy(it) })
                                    Text(stringResource(R.string.contraindication_pregnancy))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = data.hasCardiac, onCheckedChange = { viewModel.toggleCardiac(it) })
                                    Text(stringResource(R.string.contraindication_cardiac))
                                }
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(stringResource(R.string.profile_progress), style = MaterialTheme.typography.titleMedium)
                                BciProgressBar(bci = data.bci)
                                SimpleBarChart(data = data.weeklySessions)
                            }
                        }
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(stringResource(R.string.profile_badges), style = MaterialTheme.typography.titleMedium)
                                BadgeGrid(
                                    badges = data.allBadges,
                                    earnedIds = data.earnedBadges
                                )
                            }
                        }
                    }
                }
            }
            is UiState.Error -> EmptyState(message = state.message)
        }
    }
}
