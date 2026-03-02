package com.aeris.android.ui.screens

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeris.android.data.datastore.SettingsManager
import com.aeris.android.data.health.MockHealthClient
import com.aeris.android.ui.components.*
import com.aeris.android.ui.model.SessionUiState
import com.aeris.android.ui.theme.*
import com.aeris.domain.model.*
import com.aeris.domain.repository.ProtocolRepository
import com.aeris.domain.repository.SessionRepository
import com.aeris.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@Composable
fun SessionScreen(
    protocolId: String,
    onNavigateBack: () -> Unit,
    onSessionComplete: () -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService<Vibrator>() }
    
    LaunchedEffect(protocolId) {
        viewModel.loadProtocol(protocolId)
    }
    
    // Vibrate on phase change
    LaunchedEffect(uiState.currentPhaseIndex) {
        vibrator?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("session-screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.stopSession()
                        onNavigateBack()
                    },
                    modifier = Modifier.testTag("back-button")
                ) {
                    Icon(Icons.Default.Close, "Close")
                }
                
                Text(
                    text = uiState.protocol?.name?.get("en") ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Heart rate if available
                uiState.currentHeartRate?.let { hr ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Heart rate",
                            tint = SafetyDanger,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$hr",
                            style = MaterialTheme.typography.labelMedium,
                            color = SafetyDanger
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Safety warning if needed
            if (uiState.showSafetyWarning) {
                SafetyBanner(
                    message = uiState.safetyWarningMessage,
                    severity = SafetySeverity.WARNING,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Progress indicator
            LinearProgressIndicator(
                progress = { uiState.overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .testTag("progress-bar"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            
            // Cycle counter
            Text(
                text = "Cycle ${uiState.currentCycle} of ${uiState.totalCycles}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Breathing animation
            uiState.protocol?.let { protocol ->
                BreathingAnimation(
                    phase = uiState.currentPhase?.phase ?: BreathingPhase.INHALE,
                    progress = uiState.phaseProgress,
                    animationType = protocol.animation.type,
                    phaseLabel = getPhaseLabel(uiState.currentPhase?.phase),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .testTag("breathing-animation")
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timer
            Text(
                text = formatTime(uiState.phaseDuration * (1 - uiState.phaseProgress)),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Light,
                color = getPhaseColor(uiState.currentPhase?.phase ?: BreathingPhase.INHALE)
            )
            
            // Instruction
            uiState.currentPhase?.let { step ->
                Text(
                    text = step.instruction.get("en"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip hold button (only during holds)
                if (uiState.currentPhase?.phase in listOf(BreathingPhase.HOLD_IN, BreathingPhase.HOLD_OUT)) {
                    OutlinedButton(
                        onClick = { viewModel.skipHold() },
                        modifier = Modifier.testTag("skip-hold-button")
                    ) {
                        Text("Skip Hold")
                    }
                } else {
                    Spacer(modifier = Modifier.width(100.dp))
                }
                
                // Pause/Resume button
                FilledIconButton(
                    onClick = { 
                        if (uiState.isPaused) viewModel.resumeSession() 
                        else viewModel.pauseSession() 
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("pause-button")
                ) {
                    Icon(
                        imageVector = if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (uiState.isPaused) "Resume" else "Pause"
                    )
                }
                
                // Emergency stop
                EmergencyStopButton(
                    onClick = {
                        viewModel.stopSession()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .width(100.dp)
                        .testTag("stop-button")
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Session complete dialog
        if (uiState.isComplete) {
            SessionCompleteDialog(
                cyclesCompleted = uiState.currentCycle,
                onDismiss = onSessionComplete,
                onRate = { rating ->
                    viewModel.rateSession(rating)
                    onSessionComplete()
                }
            )
        }
    }
}

@Composable
private fun SessionCompleteDialog(
    cyclesCompleted: Int,
    onDismiss: () -> Unit,
    onRate: (Int) -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SafetySuccess,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Great Job!", style = MaterialTheme.typography.headlineSmall)
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "You completed $cyclesCompleted cycles",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "How do you feel?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Rate $i",
                                tint = if (i <= rating) CategoryEnergy else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onRate(rating) }) {
                Text("Done")
            }
        }
    )
}

private fun getPhaseLabel(phase: BreathingPhase?): String {
    return when (phase) {
        BreathingPhase.INHALE -> "Inhale"
        BreathingPhase.HOLD_IN -> "Hold"
        BreathingPhase.EXHALE -> "Exhale"
        BreathingPhase.HOLD_OUT -> "Hold"
        null -> ""
    }
}

private fun formatTime(seconds: Float): String {
    val totalSecs = seconds.toInt().coerceAtLeast(0)
    return "$totalSecs"
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val protocolRepository: ProtocolRepository,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val settingsManager: SettingsManager,
    private val mockHealthClient: MockHealthClient
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0
    private var currentSessionId: String = ""
    
    fun loadProtocol(protocolId: String) {
        viewModelScope.launch {
            val protocol = protocolRepository.getProtocolById(protocolId)
            protocol?.let {
                _uiState.value = SessionUiState(
                    protocol = it,
                    totalCycles = 4
                )
                startSession()
            }
        }
    }
    
    private fun startSession() {
        sessionStartTime = System.currentTimeMillis()
        currentSessionId = UUID.randomUUID().toString()
        startTimer()
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (!_uiState.value.isComplete && !_uiState.value.isPaused) {
                delay(50) // Update every 50ms for smooth animation
                
                val currentState = _uiState.value
                val stepDuration = currentState.phaseDuration
                val newProgress = currentState.phaseProgress + (0.05f / stepDuration)
                
                if (newProgress >= 1f) {
                    advanceToNextPhase()
                } else {
                    _uiState.value = currentState.copy(
                        phaseProgress = newProgress,
                        elapsedSeconds = currentState.elapsedSeconds + 0.05f,
                        currentHeartRate = mockHealthClient.getSessionHeartRate(
                            currentState.currentPhase?.phase in listOf(BreathingPhase.HOLD_IN, BreathingPhase.HOLD_OUT)
                        )
                    )
                    
                    // Safety check: HR too high
                    currentState.currentHeartRate?.let { hr ->
                        if (hr > 100 && currentState.currentPhase?.phase in listOf(BreathingPhase.HOLD_IN, BreathingPhase.HOLD_OUT)) {
                            _uiState.value = _uiState.value.copy(
                                showSafetyWarning = true,
                                safetyWarningMessage = "Heart rate elevated. Consider skipping the hold."
                            )
                        }
                    }
                }
            }
        }
    }
    
    private fun advanceToNextPhase() {
        val currentState = _uiState.value
        val protocol = currentState.protocol ?: return
        val steps = protocol.steps
        
        val nextPhaseIndex = currentState.currentPhaseIndex + 1
        
        if (nextPhaseIndex >= steps.size) {
            // End of cycle
            val nextCycle = currentState.currentCycle + 1
            if (nextCycle > currentState.totalCycles) {
                // Session complete
                completeSession()
            } else {
                // Start next cycle
                _uiState.value = currentState.copy(
                    currentPhaseIndex = 0,
                    currentCycle = nextCycle,
                    phaseProgress = 0f,
                    showSafetyWarning = false
                )
            }
        } else {
            // Next phase
            _uiState.value = currentState.copy(
                currentPhaseIndex = nextPhaseIndex,
                phaseProgress = 0f,
                showSafetyWarning = false
            )
        }
    }
    
    fun pauseSession() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isPaused = true)
    }
    
    fun resumeSession() {
        _uiState.value = _uiState.value.copy(isPaused = false)
        startTimer()
    }
    
    fun stopSession() {
        timerJob?.cancel()
        viewModelScope.launch {
            // Save interrupted session
            val session = Session(
                id = currentSessionId,
                protocolId = _uiState.value.protocol?.id ?: "",
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                completedCycles = _uiState.value.currentCycle - 1,
                targetCycles = _uiState.value.totalCycles,
                wasInterrupted = true,
                interruptionReason = InterruptionReason.USER_STOPPED
            )
            sessionRepository.saveSession(session)
        }
    }
    
    fun skipHold() {
        advanceToNextPhase()
    }
    
    private fun completeSession() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isComplete = true)
        
        viewModelScope.launch {
            val session = Session(
                id = currentSessionId,
                protocolId = _uiState.value.protocol?.id ?: "",
                startTime = sessionStartTime,
                endTime = System.currentTimeMillis(),
                completedCycles = _uiState.value.totalCycles,
                targetCycles = _uiState.value.totalCycles,
                wasInterrupted = false
            )
            sessionRepository.saveSession(session)
            
            // Update user progress
            userRepository.updateStreak()
            userRepository.addExperience(10 * _uiState.value.totalCycles)
            userRepository.markProtocolCompleted(_uiState.value.protocol?.id ?: "")
        }
    }
    
    fun rateSession(rating: Int) {
        viewModelScope.launch {
            sessionRepository.getSessionById(currentSessionId)?.let { session ->
                sessionRepository.saveSession(session.copy(userRating = rating))
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
