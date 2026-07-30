package com.aeris.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aeris.R
import com.aeris.domain.model.Badge

@Composable
fun BadgeGrid(
    badges: List<Badge>,
    earnedIds: List<String>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(badges) { badge ->
            val earned = earnedIds.contains(badge.id)
            Card(
                modifier = Modifier.aspectRatio(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (earned) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (earned) "T" else "L",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (earned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = stringResource(id = getBadgeNameResId(badge.nameKey)),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

fun getBadgeNameResId(key: String): Int {
    return when (key) {
        "badge_first_breath_name" -> R.string.badge_first_breath_name
        "badge_week_warrior_name" -> R.string.badge_week_warrior_name
        "badge_co2_warrior_name" -> R.string.badge_co2_warrior_name
        "badge_night_owl_name" -> R.string.badge_night_owl_name
        "badge_early_bird_name" -> R.string.badge_early_bird_name
        "badge_month_master_name" -> R.string.badge_month_master_name
        "badge_century_name" -> R.string.badge_century_name
        else -> R.string.app_name
    }
}
