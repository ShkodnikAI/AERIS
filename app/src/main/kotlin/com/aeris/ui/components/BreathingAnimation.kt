package com.aeris.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aeris.domain.model.Phase

@Composable
fun BreathingAnimation(
    phase: Phase,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val targetScale = when (phase) {
        Phase.INHALE -> 1.0f
        Phase.HOLD -> 1.0f
        Phase.EXHALE -> 0.3f
        Phase.HOLD_EMPTY -> 0.3f
    }
    val scale by animateFloatAsState(
        targetValue = if (phase == Phase.INHALE || phase == Phase.HOLD) 1.0f else 0.3f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "breathing_scale"
    )

    val color = when (phase) {
        Phase.INHALE -> MaterialTheme.colorScheme.primary
        Phase.HOLD -> MaterialTheme.colorScheme.tertiary
        Phase.EXHALE -> MaterialTheme.colorScheme.secondary
        Phase.HOLD_EMPTY -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.6f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale * 0.8f + 0.2f)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.8f))
        )
    }
}
