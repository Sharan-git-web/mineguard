package com.mineinspect.app.ui.screens.briefing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.History
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
fun MineBriefingScreen(
    onBack: () -> Unit,
    onStartInspection: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBar(
            title = "Live Inspection Checklist",
            subtitle = "Mine Inspect",
            showBack = true,
            onBack = onBack
        )

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            StatusBadge("PERMIT ACTIVE #M-882", BadgeStatus.NEUTRAL)
            Spacer(Modifier.height(8.dp))
            Text(
                "Blackwood Colliery — Pre-Entry Brief",
                style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text("Shaft Sector 4 • Shift Alpha • Lead Insp. J. Vance", style = AppType.bodySm, color = Secondary)

            Spacer(Modifier.height(Dimens.sectionGap))

            // Hazard index card
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("HAZARD INDEX ASSESSMENT", style = AppType.labelSm, color = Secondary)
                    StatusBadge("Elevated Attention", BadgeStatus.WARNING)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("78", style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                    Text(" / 100", style = AppType.bodyMd, color = Secondary)
                }
                Spacer(Modifier.height(8.dp))
                AppLinearProgress(progress = 0.78f, fillColor = Primary)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.History, contentDescription = null, tint = Secondary)
                    Spacer(Modifier.width(6.dp))
                    Text("12 Previous Violations", style = AppType.bodySm, color = Secondary)
                    Spacer(Modifier.weight(1f))
                    Text("Past 90 days", style = AppType.bodySm, color = Secondary)
                }
                Spacer(Modifier.height(10.dp))
                Text("Focus tags:", style = AppType.bodySm, color = Secondary)
                Spacer(Modifier.height(6.dp))
                Row {
                    StatusBadge("PPE", BadgeStatus.NEUTRAL)
                    Spacer(Modifier.width(6.dp))
                    StatusBadge("Dust", BadgeStatus.NEUTRAL)
                    Spacer(Modifier.width(6.dp))
                    StatusBadge("Maintenance", BadgeStatus.NEUTRAL)
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Required Target Sectors", style = AppType.headlineMd, color = OnSurface)
                Text("3 of 3 Primed", style = AppType.bodySm, color = Secondary)
            }
            Spacer(Modifier.height(10.dp))

            listOf(
                Triple("Section A — Haulage", "Track gauge stability, emergency stop", "Ready"),
                Triple("Section B — Extraction", "Bolting pattern acoustic, micro-seismic", "Ready"),
                Triple("Section C — Ventilation", "Air velocity fan sensor array", "Ready"),
            ).forEach { (title, subtitle, status) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.radiusMd))
                        .background(SurfaceContainerLowest)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(title, style = AppType.labelMd, color = OnSurface)
                        Text(subtitle, style = AppType.bodySm, color = Secondary)
                    }
                    StatusBadge(status, BadgeStatus.SUCCESS)
                }
                Spacer(Modifier.height(Dimens.gutterCard))
            }

            Spacer(Modifier.height(4.dp))

            Text("Evidence Quota Mandatory", style = AppType.headlineMd, color = OnSurface)
            Spacer(Modifier.height(10.dp))
            StatCardRow {
                StatCard("Photos Required", "9", modifier = Modifier.weight(1f))
                StatCard("Random Spot Checks", "3", modifier = Modifier.weight(1f))
                StatCard("Gas Sensor Logs", "2", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            PrimaryActionButton(
                text = "Start Inspection",
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onStartInspection
            )

            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
