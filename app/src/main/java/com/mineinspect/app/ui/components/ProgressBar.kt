package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.Primary
import com.mineinspect.app.ui.theme.SurfaceContainerLow

/** Linear progress bar used for compliance %, sync %, coverage % across screens. */
@Composable
fun AppLinearProgress(
    progress: Float, // 0f..1f
    modifier: Modifier = Modifier,
    trackColor: Color = SurfaceContainerLow,
    fillColor: Color = Primary,
    height: Dp = 8.dp
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(50))
                .background(fillColor)
        )
    }
}
