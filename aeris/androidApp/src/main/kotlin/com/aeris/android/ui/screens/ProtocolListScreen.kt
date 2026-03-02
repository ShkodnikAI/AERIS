package com.aeris.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.android.ui.components.ProtocolCard
import com.aeris.android.ui.components.getCategoryColor
import com.aeris.android.ui.model.ProtocolListUiState
import com.aeris.domain.model.Protocol
import com.aeris.domain.model.ProtocolCategory
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolListScreen(
    onProtocolSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProtocolListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProtocols()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Protocols") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("back-button")
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("protocol-list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category filters
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("All") },
                            modifier = Modifier.testTag("filter-all")
                        )
                    }
                    items(ProtocolCategory.entries) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category.name,
                            onClick = { viewModel.filterByCategory(category.name) },
                            label = { Text(getCategoryDisplayName(category)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = getCategoryColor(category).copy(alpha = 0.2f),
                                selectedLabelColor = getCategoryColor(category)
                            ),
                            modifier = Modifier.testTag("filter-${category.name.lowercase()}")
                        )
                    }
                }
            }
            
            // Protocol cards
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(uiState.filteredProtocols) { protocol ->
                    ProtocolCard(
                        protocol = protocol,
                        isLocked = protocol.safetyRules.minLevel > uiState.userLevel,
                        onClick = { onProtocolSelected(protocol.id) },
                        modifier = Modifier.testTag("protocol-${protocol.id}")
                    )
                }
            }
            
            if (uiState.filteredProtocols.isEmpty() && !uiState.isLoading) {
                item {
                    Text(
                        text = "No protocols found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
        }
    }
}

private fun getCategoryDisplayName(category: ProtocolCategory): String {
    return when (category) {
        ProtocolCategory.RELAXATION_SLEEP -> "Relaxation"
        ProtocolCategory.ENERGY_FOCUS -> "Energy"
        ProtocolCategory.THERAPY_HEALTH -> "Therapy"
        ProtocolCategory.SPIRITUAL_ADVANCED -> "Spiritual"
    }
}

@HiltViewModel
class ProtocolListViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProtocolListUiState())
    val uiState: StateFlow<ProtocolListUiState> = _uiState.asStateFlow()
    
    fun loadProtocols() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val userState = userRepository.getUserState()
                val protocols = protocolRepository.getAllProtocols()
                
                _uiState.value = ProtocolListUiState(
                    protocols = protocols,
                    filteredProtocols = protocols,
                    userLevel = userState.level,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun filterByCategory(category: String?) {
        val filtered = if (category == null) {
            _uiState.value.protocols
        } else {
            _uiState.value.protocols.filter { it.category.name == category }
        }
        
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredProtocols = filtered
        )
    }
}
