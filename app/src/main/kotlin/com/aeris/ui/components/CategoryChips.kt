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
import com.aeris.domain.model.Category

@Composable
fun CategoryChips(
    selected: Category?,
    onSelect: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf<Pair<String, Category?>>(
        stringResource(R.string.filter_all) to null,
        stringResource(R.string.filter_relaxation) to Category.RELAXATION_SLEEP,
        stringResource(R.string.filter_energy) to Category.ENERGY_FOCUS,
        stringResource(R.string.filter_therapy) to Category.THERAPY_HEALTH,
        stringResource(R.string.filter_spiritual) to Category.SPIRITUAL_ADVANCED
    )
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items) { (label, category) ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(label) }
            )
        }
    }
}
