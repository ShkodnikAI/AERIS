package com.aeris.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aeris.R
import com.aeris.domain.model.NervousState
import com.aeris.ui.theme.MutedGreen
import com.aeris.ui.theme.SoftRed
import com.aeris.ui.theme.WarmAmber

@Composable
fun NsiIndicator(state: NervousState, modifier: Modifier = Modifier) {
    val (label, color) = when (state) {
        NervousState.HYPERAROUSAL -> stringResource(R.string.nsi_hyperarousal) to SoftRed
        NervousState.BALANCED -> stringResource(R.string.nsi_balanced) to MutedGreen
        NervousState.HYPOAROUSAL -> stringResource(R.string.nsi_hypoarousal) to WarmAmber
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
