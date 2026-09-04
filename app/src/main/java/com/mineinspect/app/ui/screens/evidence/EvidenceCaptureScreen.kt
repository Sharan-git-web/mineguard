package com.mineinspect.app.ui.screens.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.theme.*

@Composable
fun EvidenceCaptureScreen(onBack: () -> Unit, onCaptured: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Inspection Audit Form", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Conveyor #2 Capture", style = AppType.headlineLg, color = OnSurface)
                Text("10:21 AM", style = AppType.bodySm, color = Secondary)
            }
            Text("Target: Pulley Lagging & Belt Wear", style = AppType.bodySm, color = Secondary)

            Spacer(Modifier.height(12.dp))

            // Viewfinder placeholder — real CameraX preview goes here
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(InverseSurface)
            ) {
                Text(
                    "LAT: 33.0841° N | LON: 109.3512° W",
                    style = AppType.labelSm, color = Color.White,
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                )
                Text(
                    "ELEV: -184m",
                    style = AppType.labelSm, color = Color.White,
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(Dimens.radiusMd)).background(SurfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.FlashAuto, null, tint = OnSurface) }

                // Shutter button
                Box(
                    Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .border(3.dp, Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable(onClick = onCaptured)
                    )
                }

                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(Dimens.radiusMd)).background(SurfaceContainerLow),
                    contentAlignment = Alignment.Center
                ) { Text("Boost", style = AppType.labelSm, color = OnSurface) }
            }

            Spacer(Modifier.height(Dimens.gutterCard))
        }
    }
}
