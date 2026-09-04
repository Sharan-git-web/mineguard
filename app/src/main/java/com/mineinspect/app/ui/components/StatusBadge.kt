package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

enum class BadgeStatus { SUCCESS, WARNING, CRITICAL, NEUTRAL }

/** Full-pill status chip (Completed / Pending / Locked / Active). */
@Composable
fun StatusBadge(text: String, status: BadgeStatus, modifier: Modifier = Modifier) {
    val (bg, fg, border) = when (status) {
        BadgeStatus.SUCCESS -> Triple(SuccessBg, SuccessText, SuccessBorder)
        BadgeStatus.WARNING -> Triple(WarningBg, WarningText, WarningBorder)
        BadgeStatus.CRITICAL -> Triple(CriticalBg, CriticalText, CriticalBorder)
        BadgeStatus.NEUTRAL -> Triple(NeutralTagBg, NeutralTagText, NeutralTagBorder)
    }
    Text(
        text = text,
        style = AppType.labelSm,
        color = fg,
        modifier = modifier
            .background(bg, RoundedCornerShape(Dimens.radiusFull))
            .border(1.dp, border, RoundedCornerShape(Dimens.radiusFull))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}
