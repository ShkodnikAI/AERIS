package com.aeris.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

@Composable
fun SimpleBarChart(
    data: List<Int>,
    modifier: Modifier = Modifier,
    barColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return
    val max = data.maxOrNull()?.coerceAtLeast(1) ?: 1
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val barWidth = size.width / data.size * 0.7f
        val spacing = size.width / data.size * 0.3f
        data.forEachIndexed { index, value ->
            val barHeight = (value.toFloat() / max) * size.height
            drawRoundRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = index * (barWidth + spacing) + spacing / 2,
                    y = size.height - barHeight
                ),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
