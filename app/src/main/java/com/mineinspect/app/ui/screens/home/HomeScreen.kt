package com.mineinspect.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*

@Composable
fun HomeScreen(
    onStartInspection: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(NavTab.INSPECTIONS) }

    Scaffold(
        bottomBar = {
            AppBottomNavBar(
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Surface)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            AppTopBar(
                title = "Assigned Inspections",
                subtitle = "Inspector INS-102 • Shift #402",
                showBack = false,
                online = true
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.marginScreen)
            ) {
                // Active / Priority Mine Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.radiusXl))
                        .background(SurfaceContainerLowest)
                        .padding(Dimens.cardPadding)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(text = "IN PROGRESS", status = BadgeStatus.WARNING)
                        Text("DUE IN 2H 45M", style = AppType.labelSm, color = Secondary)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Blackwood Colliery - Shaft 4B",
                        style = AppType.headlineLg,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Sector A, B & C • MSHA Audit Pipeline #9940",
                        style = AppType.bodySm,
                        color = Secondary
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Inspection Progress", style = AppType.labelMd, color = OnSurface)
                        Text("35%", style = AppType.labelMd, color = Primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    AppLinearProgress(progress = 0.35f)

                    Spacer(Modifier.height(20.dp))

                    PrimaryActionButton(
                        text = "RESUME FIELD BRIEFING",
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = OnPrimary)
                        },
                        onClick = onStartInspection
                    )
                }

                Spacer(Modifier.height(Dimens.sectionGap))

                // Telemetry Stats Row
                Text("Field Telemetry Status", style = AppType.headlineMd, color = OnSurface)
                Spacer(Modifier.height(10.dp))

                StatCardRow {
                    StatCard(
                        label = "GPS Accuracy",
                        value = "1.8m",
                        caption = "LOCK ACTIVE",
                        captionColor = Tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Local Buffer",
                        value = "4 Items",
                        caption = "UNSYNCED",
                        captionColor = WarningText,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                StatCardRow {
                    StatCard(
                        label = "Hazard Index",
                        value = "Low (0.12)",
                        caption = "SECTOR B CLEAR",
                        captionColor = Tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "SHA-256 Ledger",
                        value = "Verified",
                        caption = "TAMPER-EVIDENT",
                        captionColor = Tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(Dimens.sectionGap))

                // Assigned Mine List
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Assigned Mines Queue", style = AppType.headlineMd, color = OnSurface)
                    Text("3 Mines", style = AppType.labelMd, color = Secondary)
                }

                Spacer(Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChecklistRow(
                        iconVector = Icons.Filled.Business,
                        title = "Blackwood Colliery - Shaft 4B",
                        subtitle = "Sectors A, B, C • Priority 1",
                        trailingBadge = { StatusBadge(text = "IN PROGRESS", status = BadgeStatus.WARNING) },
                        onClick = onStartInspection
                    )
                    ChecklistRow(
                        iconVector = Icons.Filled.Engineering,
                        title = "Appalachian Slope Mine #2",
                        subtitle = "Ventilation & Methane Check",
                        trailingBadge = { StatusBadge(text = "PENDING", status = BadgeStatus.NEUTRAL) }
                    )
                    ChecklistRow(
                        iconVector = Icons.Filled.CheckCircle,
                        title = "Cumberland South Portal",
                        subtitle = "Quarterly Compliance Audit",
                        trailingBadge = { StatusBadge(text = "COMPLETED", status = BadgeStatus.SUCCESS) }
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
