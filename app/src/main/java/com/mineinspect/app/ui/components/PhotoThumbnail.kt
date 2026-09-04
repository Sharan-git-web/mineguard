package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

enum class ThumbnailStatus { VERIFIED, FLAGGED, NONE }

@Composable
fun PhotoThumbnail(
    caption: String,
    status: ThumbnailStatus = ThumbnailStatus.NONE,
    modifier: Modifier = Modifier.size(width = 96.dp, height = 96.dp)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radiusDefault))
            .background(SurfaceContainerHigh),
        contentAlignment = Alignment.Center
    ) {
        Text(
            caption,
            style = AppType.labelSm,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(6.dp)
        )
        if (status != ThumbnailStatus.NONE) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(if (status == ThumbnailStatus.VERIFIED) Tertiary else ErrorColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (status == ThumbnailStatus.VERIFIED) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
