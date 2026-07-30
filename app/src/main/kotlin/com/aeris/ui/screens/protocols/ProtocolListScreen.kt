package com.aeris.ui.screens.protocols

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeris.R
import com.aeris.domain.model.Category
import com.aeris.domain.model.Difficulty
import com.aeris.ui.components.*
import com.aeris.ui.model.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSession: (String) -> Unit,
    onNavigateToConsent: (String) -> Unit,
    viewModel: ProtocolListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_protocols)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            CategoryChips(
                selected = selectedCategory,
                onSelect = { viewModel.setCategory(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            DifficultyChips(
                selected = selectedDifficulty,
                onSelect = { viewModel.setDifficulty(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (val state = uiState) {
                is UiState.Loading -> LoadingShimmer()
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.data) { item ->
                            ProtocolCard(
                                protocol = item.protocol,
                                isLocked = item.isLocked,
                                hasWarning = item.hasWarning,
                                onClick = {
                                    if (item.requiresConsent) {
                                        onNavigateToConsent(item.protocol.id)
                                    } else {
                                        onNavigateToSession(item.protocol.id)
                                    }
                                }
                            )
                        }
                    }
                }
                is UiState.Error -> EmptyState(message = state.message)
            }
        }
    }
}
