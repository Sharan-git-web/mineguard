package com.mineinspect.app.ui.screens.gpsgate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*

@Composable
fun GpsGateScreen(
    onBack: () -> Unit,
    onStartInspection: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBar(title = "Active Checkpoint", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            StatusBadge("GATE LOCK VERIFICATION", BadgeStatus.CRITICAL)
            Spacer(Modifier.height(8.dp))
            Text("Start Inspection & GPS Gate", style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold), color = OnSurface)
            Text(
                "Verify geographic lock and field compliance at Blackwood Colliery before shaft descent.",
                style = AppType.bodySm, color = Secondary
            )

            Spacer(Modifier.height(Dimens.sectionGap))

            // Inspector identity card
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Column(Modifier.weight(1f)) {
                    Text("INS-102", style = AppType.headlineMd, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("Chief Geotechnical Officer", style = AppType.bodySm, color = Secondary)
                    Spacer(Modifier.height(10.dp))
                    StatCardRow {
                        StatCard("Target Portal", "Blackwood (North Quad)", modifier = Modifier.weight(1f))
                        StatCard("Portal Anchor", "Portal #04 Descent", modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(Dimens.gutterCard))
            StatCardRow {
                StatCard("Date", "04 Sep 2026", modifier = Modifier.weight(1f))
                StatCard("Timestamp", "10:02 AM MST", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // GNSS telemetry card
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GNSS Constellation Telemetry", style = AppType.labelMd, color = OnSurface)
                    StatusBadge("RTK FIX", BadgeStatus.SUCCESS)
                }
                Spacer(Modifier.height(10.dp))
                StatCardRow {
                    StatCard("Locked Satellites", "14 Sat", modifier = Modifier.weight(1f))
                    StatCard("RTK Phase Lock", "Verified", captionColor = Tertiary, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // Perimeter gate ready card
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLow)
                    .padding(Dimens.cardPadding)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Tertiary)
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Perimeter Gate Ready", style = AppType.labelLg, color = OnSurface)
                        Text("Geofence Status: INSIDE Sector Alpha Hub", style = AppType.bodySm, color = Secondary)
                    }
                    StatusBadge("PASS", BadgeStatus.SUCCESS)
                }
                Spacer(Modifier.height(12.dp))
                Text("Current GPS Accuracy   ±6.2m (Limit: ≤10m)", style = AppType.bodySm, color = Secondary)
                Spacer(Modifier.height(6.dp))
                AppLinearProgress(progress = 0.62f, fillColor = Tertiary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            PrimaryActionButton(
                text = "Start Inspection",
                leadingIcon = { Icon(Icons.Filled.PlayArrow, null, tint = OnPrimary) },
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onStartInspection
            )
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(
                text = "Cancel / Return to Roster",
                leadingIcon = { Icon(Icons.Filled.Cancel, null, tint = OnSurface) },
                onClick = onCancel
            )

            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
