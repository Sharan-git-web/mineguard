package com.mineinspect.app.ui.screens.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ActiveTrackingScreen(
    onBack: () -> Unit,
    onViewMap: () -> Unit,
    onViewAreas: () -> Unit
) {
    var minutes by remember { mutableStateOf(14) }
    var seconds by remember { mutableStateOf(25) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
            if (seconds >= 60) { seconds = 0; minutes++ }
        }
    }

    var markerLogged by remember { mutableStateOf(false) }
    LaunchedEffect(markerLogged) {
        if (markerLogged) { delay(2200); markerLogged = false }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBar(title = "Active Checkpoint", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLow)
                    .padding(Dimens.cardPadding),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    StatusBadge("ACTIVE INSPECTION SESSION", BadgeStatus.CRITICAL)
                    Spacer(Modifier.height(6.dp))
                    Text("Portal North Quad", style = AppType.headlineMd, color = OnSurface, fontWeight = FontWeight.Bold)
                }
                StatusBadge("INS-2026-0098", BadgeStatus.NEUTRAL)
            }

            Spacer(Modifier.height(Dimens.gutterCard))

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.GpsFixed, contentDescription = null, tint = Primary)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("GPS ROUTE TRACKING ACTIVE", style = AppType.labelMd, color = OnSurface)
                    Text("Recording 1 point every 5 seconds", style = AppType.bodySm, color = Secondary)
                }
                StatusBadge("5s REC", BadgeStatus.CRITICAL)
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            Text("SESSION TELEMETRY", style = AppType.labelSm, color = Secondary)
            Spacer(Modifier.height(8.dp))
            StatCardRow {
                StatCard("Start Location", "Portal North #02", modifier = Modifier.weight(1f))
                StatCard("Start Time", "10:02 AM", modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(Dimens.gutterCard))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Elapsed Duration", style = AppType.bodySm, color = Secondary)
                    Text(
                        "${minutes}m ${seconds.toString().padStart(2, '0')}s",
                        style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold
                    )
                }
                StatusBadge("ACCURATE", BadgeStatus.SUCCESS)
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            StatCardRow {
                StatCard("Accuracy", "6 m", "Optimal", Tertiary, modifier = Modifier.weight(1f))
                StatCard("Logged", "480 m", "+12m/min", Secondary, modifier = Modifier.weight(1f))
                StatCard("Points", "28 pts", "0 skipped", Secondary, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // Breadcrumb trail placeholder (real map lives on the Route Map screen)
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(14.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Breadcrumb Trail", style = AppType.labelMd, color = OnSurface)
                    Text("Haul Road 04 / Sector B", style = AppType.bodySm, color = Secondary)
                }
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(Dimens.radiusDefault))
                        .background(SurfaceContainerHigh)
                )
                Spacer(Modifier.height(8.dp))
                Text("Latest Node #28 (Elev. 342m)", style = AppType.bodySm, color = Primary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            SecondaryActionButton(
                text = if (markerLogged) "Marker Logged at Point #29" else "Drop Quick Geo-Hazard Marker",
                leadingIcon = {
                    Icon(
                        if (markerLogged) Icons.Filled.Check else Icons.Filled.AddLocationAlt,
                        null, tint = if (markerLogged) Tertiary else OnSurface
                    )
                },
                onClick = { markerLogged = true }
            )

            Spacer(Modifier.height(Dimens.gutterCard))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.gutterCard)) {
                SecondaryActionButton(
                    text = "Live Route Map",
                    leadingIcon = { Icon(Icons.Filled.Map, null, tint = OnSurface) },
                    modifier = Modifier.weight(1f),
                    onClick = onViewMap
                )
                PrimaryActionButton(
                    text = "Areas (1/3 Done)",
                    modifier = Modifier.weight(1f),
                    onClick = onViewAreas
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
