package com.mineinspect.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.theme.*

/**
 * Header pattern: back arrow (optional) + title/subtitle + online status pill + avatar.
 */
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = true,
    onBack: () -> Unit = {},
    online: Boolean = true,
    contextIcon: Painter? = null,
    onAvatarClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.marginScreen, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(onClick = onBack, modifier = Modifier.size(Dimens.touchMin)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = OnSurface)
            }
        }

        contextIcon?.let {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(painter = it, contentDescription = null)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(Modifier.weight(1f)) {
            Text(title, style = AppType.headlineMd, color = OnSurface)
            subtitle?.let { Text(it, style = AppType.bodySm, color = Secondary) }
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimens.radiusFull))
                .background(SurfaceContainerLow)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (online) Tertiary else ErrorColor)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                if (online) "ONLINE" else "OFFLINE",
                style = AppType.labelSm,
                color = if (online) Tertiary else ErrorColor
            )
        }

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = onAvatarClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Primary)
        ) {
            Icon(Icons.Filled.Person, contentDescription = "Profile", tint = OnPrimary)
        }
    }
}
