package com.aeris.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aeris.android.ui.components.*
import com.aeris.android.ui.model.HomeUiState
import com.aeris.android.ui.theme.*
import com.aeris.domain.model.NervousState
import com.aeris.domain.model.Protocol
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.ai.NsiCalculator
import com.aeris.ai.ProtocolRecommender
import com.aeris.android.data.health.HealthConnectManager
import com.aeris.android.data.health.MockHealthClient
import com.aeris.domain.model.HealthMetrics
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.repository.SessionRepository
import com.aeris.domain.repository.UserRepository
import com.aeris.util.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun HomeScreen(
    onNavigateToProtocols: () -> Unit,
    onNavigateToSession: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDisclaimer by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    if (showDisclaimer) {
        DisclaimerDialog(
            onAccept = {
                viewModel.acceptDisclaimer()
                showDisclaimer = false
            },
            onDismiss = { showDisclaimer = false }
        )
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home-screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "AERIS",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.testTag("profile-button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Stats Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Streak",
                    value = "${uiState.currentStreak}",
                    subtitle = "days",
                    color = CategoryEnergy,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Level",
                    value = "${uiState.currentLevel}",
                    subtitle = "/ 5",
                    color = CategoryTherapy,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Sessions",
                    value = "${uiState.totalSessions}",
                    subtitle = "total",
                    color = CategorySpiritual,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Nervous State Indicator
        item {
            NervousStateCard(
                nervousState = uiState.nervousState,
                bciScore = uiState.bciScore
            )
        }
        
        // Quick Start Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Start",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(
                        onClick = onNavigateToProtocols,
                        modifier = Modifier.testTag("see-all-protocols")
                    ) {
                        Text("See All")
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (uiState.recommendedProtocols.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text(
                                    text = "Loading protocols...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Recommended Protocols
        items(uiState.recommendedProtocols.take(3)) { protocol ->
            ProtocolCard(
                protocol = protocol,
                isLocked = protocol.safetyRules.minLevel > uiState.currentLevel,
                onClick = { 
                    if (!uiState.hasAcceptedDisclaimer) {
                        showDisclaimer = true
                    } else {
                        onNavigateToSession(protocol.id) 
                    }
                },
                modifier = Modifier.testTag("protocol-card-${protocol.id}")
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun NervousStateCard(
    nervousState: NervousState,
    bciScore: Float
) {
    val (color, icon) = when (nervousState) {
        NervousState.HYPERAROUSAL -> SafetyWarning to Icons.Default.TrendingUp
        NervousState.BALANCED -> SafetySuccess to Icons.Default.Balance
        NervousState.HYPOAROUSAL -> CategoryRelaxation to Icons.Default.TrendingDown
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nervous System",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = nervousState.getDescription("en"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = nervousState.getRecommendation("en"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "BCI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${bciScore.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val healthConnectManager: HealthConnectManager,
    private val mockHealthClient: MockHealthClient,
    private val nsiCalculator: NsiCalculator,
    private val protocolRecommender: ProtocolRecommender
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val userState = userRepository.getUserState()
                val healthMetrics = healthConnectManager.getLatestMetrics(mockHealthClient)
                val hourOfDay = DateTimeHelper.currentHourOfDay()
                val nervousState = nsiCalculator.calculate(healthMetrics, hourOfDay)
                
                val allProtocols = protocolRepository.getAllProtocols()
                val recommendations = protocolRecommender.recommend(
                    userState = userState,
                    healthMetrics = healthMetrics,
                    availableProtocols = allProtocols,
                    hourOfDay = hourOfDay
                ).map { it.protocol }
                
                val streak = sessionRepository.getCurrentStreak()
                val totalSessions = sessionRepository.getTodaySessionsCount()
                
                _uiState.value = HomeUiState(
                    currentLevel = userState.level,
                    currentStreak = streak,
                    totalSessions = userState.totalSessions,
                    nervousState = nervousState,
                    bciScore = 50f, // TODO: Calculate from session history
                    recommendedProtocols = recommendations,
                    hasAcceptedDisclaimer = userState.hasAcceptedDisclaimer,
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
    
    fun acceptDisclaimer() {
        viewModelScope.launch {
            userRepository.acceptDisclaimer()
            _uiState.value = _uiState.value.copy(hasAcceptedDisclaimer = true)
        }
    }
}
