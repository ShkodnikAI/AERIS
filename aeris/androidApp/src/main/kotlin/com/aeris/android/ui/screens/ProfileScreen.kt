package com.aeris.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.android.data.health.HealthConnectManager
import com.aeris.android.data.health.MockHealthClient
import com.aeris.android.ui.model.ProfileUiState
import com.aeris.android.ui.theme.*
import com.aeris.domain.model.NervousState
import com.aeris.domain.repository.SessionRepository
import com.aeris.domain.repository.UserRepository
import com.aeris.ai.NsiCalculator
import com.aeris.util.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
                .testTag("profile-screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Level Card
            item {
                LevelCard(
                    level = uiState.level,
                    experience = uiState.experience,
                    experienceToNext = uiState.experienceToNextLevel
                )
            }
            
            // Stats Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        title = "Sessions",
                        value = "${uiState.totalSessions}",
                        icon = Icons.Default.FitnessCenter,
                        color = CategoryEnergy,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        title = "Minutes",
                        value = "${uiState.totalMinutes}",
                        icon = Icons.Default.Timer,
                        color = CategoryTherapy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        title = "Streak",
                        value = "${uiState.currentStreak}",
                        icon = Icons.Default.LocalFireDepartment,
                        color = SafetyWarning,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        title = "Best Streak",
                        value = "${uiState.longestStreak}",
                        icon = Icons.Default.EmojiEvents,
                        color = CategorySpiritual,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Health Data
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Health Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HealthMetricItem(
                                label = "Heart Rate",
                                value = uiState.heartRate?.let { "$it BPM" } ?: "--",
                                icon = Icons.Default.Favorite,
                                color = SafetyDanger
                            )
                            HealthMetricItem(
                                label = "HRV",
                                value = uiState.hrv?.let { "$it ms" } ?: "--",
                                icon = Icons.Default.ShowChart,
                                color = CategoryTherapy
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Nervous state
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Nervous System: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.nervousState.getDescription("en"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = when (uiState.nervousState) {
                                    NervousState.HYPERAROUSAL -> SafetyWarning
                                    NervousState.BALANCED -> SafetySuccess
                                    NervousState.HYPOAROUSAL -> CategoryRelaxation
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "(Using simulated data)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Indices
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AERIS Indices",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        IndexProgressBar(
                            label = "NSI (Nervous System)",
                            value = uiState.nsiScore,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        IndexProgressBar(
                            label = "BCI (Breath Capacity)",
                            value = uiState.bciScore,
                            color = CategoryTherapy
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: Int,
    experience: Int,
    experienceToNext: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$level",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = getLevelName(level),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { (experience % 100) / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${100 - (experience % 100)} XP to Level ${level + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun HealthMetricItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun IndexProgressBar(
    label: String,
    value: Float,
    color: androidx.compose.ui.graphics.Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${value.toInt()}/100",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

private fun getLevelName(level: Int): String {
    return when (level) {
        1 -> "Novice"
        2 -> "Practitioner"
        3 -> "Adept"
        4 -> "Master"
        5 -> "Guide"
        else -> "Novice"
    }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val healthConnectManager: HealthConnectManager,
    private val mockHealthClient: MockHealthClient,
    private val nsiCalculator: NsiCalculator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val userState = userRepository.getUserState()
                val healthMetrics = healthConnectManager.getLatestMetrics(mockHealthClient)
                val hourOfDay = DateTimeHelper.currentHourOfDay()
                val nervousState = nsiCalculator.calculate(healthMetrics, hourOfDay)
                val nsiScore = nsiCalculator.calculateArousalScore(
                    healthMetrics.heartRate,
                    healthMetrics.hrv,
                    healthMetrics.sleepQuality
                )
                
                val totalMinutes = sessionRepository.getTotalPracticeMinutes()
                val streak = sessionRepository.getCurrentStreak()
                
                _uiState.value = ProfileUiState(
                    level = userState.level,
                    experience = userState.experience,
                    experienceToNextLevel = 100 - (userState.experience % 100),
                    totalSessions = userState.totalSessions,
                    totalMinutes = totalMinutes,
                    currentStreak = streak,
                    longestStreak = userState.longestStreak,
                    nsiScore = nsiScore,
                    bciScore = 50f, // TODO: Calculate from session history
                    nervousState = nervousState,
                    heartRate = healthMetrics.heartRate,
                    hrv = healthMetrics.hrv,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
