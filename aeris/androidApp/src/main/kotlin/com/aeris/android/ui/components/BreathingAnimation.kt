package com.aeris.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeris.android.ui.theme.*
import com.aeris.domain.model.AnimationType
import com.aeris.domain.model.BreathingPhase

/**
 * Main breathing animation component.
 * Supports circle, square, and wave animation types.
 */
@Composable
fun BreathingAnimation(
    phase: BreathingPhase,
    progress: Float,
    animationType: AnimationType,
    modifier: Modifier = Modifier,
    phaseLabel: String = ""
) {
    val phaseColor = getPhaseColor(phase)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .semantics { 
                contentDescription = "Breathing animation - $phaseLabel"
            },
        contentAlignment = Alignment.Center
    ) {
        when (animationType) {
            AnimationType.CIRCLE -> CircleAnimation(phase, progress, phaseColor)
            AnimationType.SQUARE -> SquareAnimation(phase, progress, phaseColor)
            AnimationType.WAVE -> WaveAnimation(phase, progress, phaseColor)
            AnimationType.CUSTOM -> CircleAnimation(phase, progress, phaseColor)
        }
        
        // Phase label in center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = phaseLabel,
                style = MaterialTheme.typography.headlineMedium,
                color = phaseColor,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
private fun CircleAnimation(
    phase: BreathingPhase,
    progress: Float,
    color: Color
) {
    val targetScale = when (phase) {
        BreathingPhase.INHALE -> 0.3f + (progress * 0.7f)
        BreathingPhase.HOLD_IN -> 1f
        BreathingPhase.EXHALE -> 1f - (progress * 0.7f)
        BreathingPhase.HOLD_OUT -> 0.3f
    }
    
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(100, easing = LinearEasing),
        label = "circle_scale"
    )
    
    Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
        val radius = size.minDimension / 2 * animatedScale
        
        // Outer glow
        drawCircle(
            color = color.copy(alpha = 0.1f),
            radius = radius + 20.dp.toPx(),
            center = center
        )
        
        // Main circle
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = radius,
            center = center
        )
        
        // Inner ring
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Progress arc
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = progress * 360f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun SquareAnimation(
    phase: BreathingPhase,
    progress: Float,
    color: Color
) {
    // Calculate which side of the square we're on
    val sideProgress = when (phase) {
        BreathingPhase.INHALE -> 0 to progress      // Left side (bottom to top)
        BreathingPhase.HOLD_IN -> 1 to progress     // Top side (left to right)
        BreathingPhase.EXHALE -> 2 to progress      // Right side (top to bottom)
        BreathingPhase.HOLD_OUT -> 3 to progress    // Bottom side (right to left)
    }
    
    Canvas(modifier = Modifier.fillMaxSize(0.7f)) {
        val squareSize = size.minDimension * 0.8f
        val strokeWidth = 6.dp.toPx()
        val offset = (size.minDimension - squareSize) / 2
        
        // Draw full square outline
        drawRect(
            color = color.copy(alpha = 0.2f),
            topLeft = Offset(offset, offset),
            size = Size(squareSize, squareSize),
            style = Stroke(width = strokeWidth)
        )
        
        // Draw progress indicator (dot moving along edge)
        val dotRadius = 12.dp.toPx()
        val (side, prog) = sideProgress
        
        val dotPosition = when (side) {
            0 -> Offset(offset, offset + squareSize * (1 - prog)) // Left: bottom to top
            1 -> Offset(offset + squareSize * prog, offset) // Top: left to right
            2 -> Offset(offset + squareSize, offset + squareSize * prog) // Right: top to bottom
            else -> Offset(offset + squareSize * (1 - prog), offset + squareSize) // Bottom: right to left
        }
        
        // Glowing dot
        drawCircle(
            color = color.copy(alpha = 0.3f),
            radius = dotRadius * 2,
            center = dotPosition
        )
        drawCircle(
            color = color,
            radius = dotRadius,
            center = dotPosition
        )
    }
}

@Composable
private fun WaveAnimation(
    phase: BreathingPhase,
    progress: Float,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )
    
    val amplitude = when (phase) {
        BreathingPhase.INHALE -> progress * 0.3f
        BreathingPhase.HOLD_IN -> 0.3f
        BreathingPhase.EXHALE -> 0.3f * (1 - progress)
        BreathingPhase.HOLD_OUT -> 0f
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        val waveHeight = height * amplitude
        
        // Draw multiple wave layers
        for (layer in 0..2) {
            val layerAlpha = 0.3f - (layer * 0.1f)
            val layerOffset = waveOffset + (layer * 30f)
            
            val path = androidx.compose.ui.graphics.Path()
            path.moveTo(0f, centerY)
            
            for (x in 0..width.toInt() step 10) {
                val angle = Math.toRadians((x.toFloat() / width * 360f + layerOffset).toDouble())
                val y = centerY + (kotlin.math.sin(angle) * waveHeight).toFloat()
                path.lineTo(x.toFloat(), y)
            }
            
            drawPath(
                path = path,
                color = color.copy(alpha = layerAlpha),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun getPhaseColor(phase: BreathingPhase): Color {
    return when (phase) {
        BreathingPhase.INHALE -> PhaseInhale
        BreathingPhase.HOLD_IN -> PhaseHold
        BreathingPhase.EXHALE -> PhaseExhale
        BreathingPhase.HOLD_OUT -> PhaseHoldOut
    }
}
