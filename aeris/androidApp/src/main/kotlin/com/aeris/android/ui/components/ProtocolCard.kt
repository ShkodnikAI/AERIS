package com.aeris.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aeris.android.ui.theme.*
import com.aeris.domain.model.Difficulty
import com.aeris.domain.model.Protocol
import com.aeris.domain.model.ProtocolCategory

/**
 * Protocol card for list display.
 */
@Composable
fun ProtocolCard(
    protocol: Protocol,
    languageCode: String = "en",
    isLocked: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(protocol.category)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Category indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(categoryColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getCategoryName(protocol.category, languageCode),
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Protocol name
                    Text(
                        text = protocol.name.get(languageCode),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLocked) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                }
                
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = protocol.description.get(languageCode),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Meta info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Duration
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${protocol.sessionDurationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Difficulty badge
                DifficultyBadge(difficulty = protocol.difficulty, languageCode = languageCode)
            }
        }
    }
}

@Composable
fun DifficultyBadge(
    difficulty: Difficulty,
    languageCode: String = "en"
) {
    val (color, text) = when (difficulty) {
        Difficulty.BEGINNER -> CategoryRelaxation to (if (languageCode == "ru") "Начинающий" else "Beginner")
        Difficulty.INTERMEDIATE -> CategoryEnergy to (if (languageCode == "ru") "Средний" else "Intermediate")
        Difficulty.ADVANCED -> CategoryTherapy to (if (languageCode == "ru") "Продвинутый" else "Advanced")
        Difficulty.EXPERT -> CategorySpiritual to (if (languageCode == "ru") "Эксперт" else "Expert")
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun getCategoryColor(category: ProtocolCategory): Color {
    return when (category) {
        ProtocolCategory.RELAXATION_SLEEP -> CategoryRelaxation
        ProtocolCategory.ENERGY_FOCUS -> CategoryEnergy
        ProtocolCategory.THERAPY_HEALTH -> CategoryTherapy
        ProtocolCategory.SPIRITUAL_ADVANCED -> CategorySpiritual
    }
}

fun getCategoryName(category: ProtocolCategory, languageCode: String): String {
    return when (category) {
        ProtocolCategory.RELAXATION_SLEEP -> if (languageCode == "ru") "Релаксация" else "Relaxation"
        ProtocolCategory.ENERGY_FOCUS -> if (languageCode == "ru") "Энергия" else "Energy"
        ProtocolCategory.THERAPY_HEALTH -> if (languageCode == "ru") "Терапия" else "Therapy"
        ProtocolCategory.SPIRITUAL_ADVANCED -> if (languageCode == "ru") "Духовные" else "Spiritual"
    }
}
