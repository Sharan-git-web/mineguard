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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ActiveTrackingScreen(
    inspectionId: String,
    onBack: () -> Unit,
    onViewMap: () -> Unit,
    onViewAreas: () -> Unit,
    viewModel: ActiveTrackingViewModel = hiltViewModel()
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

    LaunchedEffect(Unit) {
        viewModel.startTrackingIfPermitted()
    }

    val uiState by viewModel.uiState.collectAsState()
    val pointCount by viewModel.pointCount.collectAsState()
    val markerLogged = uiState.markerLogged
    LaunchedEffect(markerLogged) {
        if (markerLogged) { delay(2200); viewModel.onMarkerFlashConsumed() }
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
                    Text("Mine 1", style = AppType.headlineMd, color = OnSurface, fontWeight = FontWeight.Bold)
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
                    Text(
                        if (uiState.isTracking) "GPS ROUTE TRACKING ACTIVE" else "GPS TRACKING UNAVAILABLE",
                        style = AppType.labelMd, color = OnSurface
                    )
                    Text("Recording a real device fix every 20 seconds", style = AppType.bodySm, color = Secondary)
                }
                StatusBadge(if (uiState.isTracking) "20s REC" else "NO PERMISSION", if (uiState.isTracking) BadgeStatus.CRITICAL else BadgeStatus.WARNING)
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            Text("SESSION TELEMETRY", style = AppType.labelSm, color = Secondary)
            Spacer(Modifier.height(8.dp))
            StatCardRow {
                StatCard("Start Location", "Mine 1 Entrance", modifier = Modifier.weight(1f))
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
                StatCard(
                    "Accuracy",
                    uiState.lastAccuracyMeters?.let { "${"%.1f".format(it)} m" } ?: "—",
                    modifier = Modifier.weight(1f)
                )
                StatCard("Points", "$pointCount pts", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))

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
