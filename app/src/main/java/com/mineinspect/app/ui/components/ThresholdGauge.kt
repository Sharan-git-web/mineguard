package com.mineinspect.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mineinspect.app.ui.theme.*

/**
 * Horizontal threshold gauge: green "safe" zone up to [threshold], red "critical" zone beyond,
 * with a pin at [value] out of [maxValue]. Matches screen 16 (Measurement Entry).
 */
@Composable
fun ThresholdGauge(
    value: Float,
    maxValue: Float,
    threshold: Float,
    unit: String,
    modifier: Modifier = Modifier
) {
    val isCritical = value > threshold
    val valueColor = if (isCritical) ErrorColor else Tertiary

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                String.format("%.1f", value),
                style = AppType.headlineXl.copy(fontSize = 48.sp, fontWeight = FontWeight.ExtraBold),
                color = valueColor
            )
            Spacer(Modifier.width(4.dp))
            Text(unit, style = AppType.bodyMd, color = Secondary, modifier = Modifier.padding(bottom = 8.dp))
        }

        Spacer(Modifier.height(12.dp))

        val trackHeight = 10.dp
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            val thresholdFraction = (threshold / maxValue).coerceIn(0f, 1f)
            val valueFraction = (value / maxValue).coerceIn(0f, 1f)
            val trackY = size.height / 2
            val trackPx = trackHeight.toPx()

            // Safe zone (green)
            drawLine(
                color = SuccessText.copy(alpha = 0.35f),
                start = Offset(0f, trackY),
                end = Offset(size.width * thresholdFraction, trackY),
                strokeWidth = trackPx,
                cap = StrokeCap.Round
            )
            // Critical zone (red)
            drawLine(
                color = CriticalBg,
                start = Offset(size.width * thresholdFraction, trackY),
                end = Offset(size.width, trackY),
                strokeWidth = trackPx,
                cap = StrokeCap.Round
            )
            // Threshold tick
            drawLine(
                color = Secondary,
                start = Offset(size.width * thresholdFraction, trackY - trackPx),
                end = Offset(size.width * thresholdFraction, trackY + trackPx),
                strokeWidth = 2.dp.toPx()
            )
            // Active pin
            drawCircle(
                color = valueColor,
                radius = 12.dp.toPx(),
                center = Offset(size.width * valueFraction, trackY)
            )
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(size.width * valueFraction, trackY)
            )
        }

        Spacer(Modifier.height(6.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0.0 Safe", style = AppType.labelSm, color = Secondary)
            Text("THRESHOLD ($threshold)", style = AppType.labelSm, color = Secondary)
            Text("$maxValue Max", style = AppType.labelSm, color = Secondary)
        }
    }
}
