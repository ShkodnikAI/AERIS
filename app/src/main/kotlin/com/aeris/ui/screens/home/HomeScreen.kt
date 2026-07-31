package com.aeris.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeris.R
import com.aeris.ui.components.*
import com.aeris.ui.model.UiState
import java.util.Calendar

@Composable
fun HomeScreen(
    onNavigateToProtocols: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToProtocolDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showSheet by viewModel.showUpdateSheet.collectAsState()
    val greeting = rememberGreeting()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(greeting) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is UiState.Loading -> LoadingShimmer(modifier = Modifier.padding(padding))
            is UiState.Success -> {
                val data = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NsiIndicator(state = data.nsi)
                        Text("${stringResource(R.string.streak_label)}: ${data.streak}", style = MaterialTheme.typography.bodyMedium)
                    }
                    BciProgressBar(bci = data.bci)
                    Button(
                        onClick = { viewModel.showUpdateSheet() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.update_state))
                    }
                    Text(
                        stringResource(R.string.recommended_for_you),
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(data.recommendations) { protocol ->
                            ProtocolCard(
                                protocol = protocol,
                                isLocked = false,
                                hasWarning = false,
                                onClick = { onNavigateToProtocolDetail(protocol.id) }
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onNavigateToEmergency,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.calm_in_60))
                    }
                }
            }
            is UiState.Error -> EmptyState(message = state.message)
        }
    }

    if (showSheet) {
        UpdateStateSheet(
            initialHr = 70,
            initialHrv = 50,
            initialSleep = 0.7f,
            onUpdate = { hr, hrv, sleep ->
                viewModel.updateState(hr, hrv, sleep)
            },
            onDismiss = { viewModel.hideUpdateSheet() }
        )
    }
}

@Composable
fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..11 -> stringResource(R.string.greeting_morning)
        in 12..17 -> stringResource(R.string.greeting_afternoon)
        in 18..23 -> stringResource(R.string.greeting_evening)
        else -> stringResource(R.string.greeting_night)
    }
}
