package com.aeris.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aeris.R
import com.aeris.domain.model.Difficulty

@Composable
fun DifficultyChips(
    selected: Difficulty?,
    onSelect: (Difficulty?) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf<Pair<String, Difficulty?>>(
        stringResource(R.string.filter_all) to null,
        stringResource(R.string.filter_beginner) to Difficulty.BEGINNER,
        stringResource(R.string.filter_intermediate) to Difficulty.INTERMEDIATE,
        stringResource(R.string.filter_advanced) to Difficulty.ADVANCED,
        stringResource(R.string.filter_expert) to Difficulty.EXPERT
    )
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items) { (label, diff) ->
            FilterChip(
                selected = selected == diff,
                onClick = { onSelect(diff) },
                label = { Text(label) }
            )
        }
    }
}
