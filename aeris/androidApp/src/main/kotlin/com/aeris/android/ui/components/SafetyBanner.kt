package com.aeris.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aeris.android.ui.theme.SafetyWarning
import com.aeris.android.ui.theme.SafetyDanger

/**
 * Safety warning banner displayed when health metrics indicate caution.
 */
@Composable
fun SafetyBanner(
    message: String,
    severity: SafetySeverity = SafetySeverity.WARNING,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (severity) {
        SafetySeverity.INFO -> MaterialTheme.colorScheme.primaryContainer
        SafetySeverity.WARNING -> SafetyWarning.copy(alpha = 0.15f)
        SafetySeverity.CRITICAL -> SafetyDanger.copy(alpha = 0.15f)
    }
    
    val contentColor = when (severity) {
        SafetySeverity.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
        SafetySeverity.WARNING -> SafetyWarning
        SafetySeverity.CRITICAL -> SafetyDanger
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            if (onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text("Dismiss", color = contentColor)
                }
            }
        }
    }
}

enum class SafetySeverity {
    INFO,
    WARNING,
    CRITICAL
}

/**
 * Emergency stop button - always visible during sessions.
 */
@Composable
fun EmergencyStopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SafetyDanger,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = "STOP",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Medical disclaimer dialog.
 */
@Composable
fun DisclaimerDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Medical Disclaimer",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "AERIS is not a medical device and should not be used as a substitute " +
                       "for professional medical advice, diagnosis, or treatment.\n\n" +
                       "Please consult with a healthcare provider before beginning any breathing " +
                       "exercise program, especially if you have cardiovascular, respiratory, " +
                       "or psychological conditions.\n\n" +
                       "Stop immediately if you experience dizziness, pain, or discomfort.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("I Understand")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
