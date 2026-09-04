package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

@Composable
fun StatCard(
    label: String,
    value: String,
    caption: String? = null,
    captionColor: Color = Secondary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceContainerLow, RoundedCornerShape(Dimens.radiusMd))
            .padding(12.dp)
    ) {
        Text(label.uppercase(), style = AppType.labelSm, color = Secondary)
        Spacer(Modifier.height(4.dp))
        Text(value, style = AppType.headlineMd, color = OnSurface, fontWeight = FontWeight.Bold)
        caption?.let {
            Spacer(Modifier.height(2.dp))
            Text(it, style = AppType.bodySm, color = captionColor)
        }
    }
}

/** Row wrapper for 2 or 4 StatCards laid out evenly. */
@Composable
fun StatCardRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Dimens.gutterCard)) {
        content()
    }
}
