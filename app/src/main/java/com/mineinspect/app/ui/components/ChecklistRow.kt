package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

@Composable
fun ChecklistRow(
    iconVector: ImageVector? = null,
    title: String,
    subtitle: String,
    trailingBadge: (@Composable () -> Unit)? = null,
    showChevron: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .background(SurfaceContainerLowest, RoundedCornerShape(Dimens.radiusMd))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconVector?.let {
            Box(
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(it, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = AppType.labelMd, color = OnSurface, maxLines = 1)
            Text(subtitle, style = AppType.bodySm, color = Secondary, maxLines = 1)
        }
        trailingBadge?.invoke()
        if (showChevron) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Secondary)
        }
    }
}
