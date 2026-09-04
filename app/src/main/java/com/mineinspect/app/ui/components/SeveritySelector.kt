package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

enum class Severity(val label: String) {
    LOW("Low"), MED("Med"), HIGH("High"), CRITICAL("Critical")
}

/** 4-way segmented Pass/Fail-style triage control from the Manual Observation screen. */
@Composable
fun SeveritySelector(
    selected: Severity,
    onSelect: (Severity) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Severity.values().forEach { sev ->
            val isSelected = sev == selected
            val (bg, fg) = when {
                isSelected && sev == Severity.CRITICAL -> ErrorColor to OnError
                isSelected && sev == Severity.HIGH -> PrimaryContainer to OnPrimaryContainer
                isSelected && sev == Severity.MED -> Secondary to OnSecondary
                isSelected -> SurfaceContainerHighest to OnSurface
                else -> SurfaceContainerLow to OnSurface
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(Dimens.radiusDefault))
                    .background(bg)
                    .clickable { onSelect(sev) },
                contentAlignment = Alignment.Center
            ) {
                Text(sev.label, style = AppType.labelMd, color = fg)
            }
        }
    }
}

/** Caption line below the selector ("Immediate Hazard", "Urgent Remediation", etc). */
@Composable
fun SeverityCaption(severity: Severity, modifier: Modifier = Modifier) {
    val (text, color) = when (severity) {
        Severity.CRITICAL -> "Immediate Hazard" to ErrorColor
        Severity.HIGH -> "Urgent Remediation" to Primary
        Severity.MED -> "Scheduled Action" to Secondary
        Severity.LOW -> "Informational Note" to Secondary
    }
    Text(text.uppercase(), style = AppType.labelSm, color = color, modifier = modifier)
}
