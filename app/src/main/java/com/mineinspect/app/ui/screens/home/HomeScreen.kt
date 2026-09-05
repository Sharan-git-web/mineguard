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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.data.local.entity.MineCacheEntity
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*

@Composable
fun HomeScreen(
    onStartInspection: (String) -> Unit = {},
    onOpenMap: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(NavTab.INSPECTIONS) }
    val uiState by viewModel.uiState.collectAsState()

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
                title = when (selectedTab) {
                    NavTab.INSPECTIONS -> "Assigned Inspections"
                    NavTab.HAZARDS -> "Hazard Reports"
                    NavTab.MAP -> "Facility Map"
                    NavTab.SYNC -> "Sync Center"
                },
                subtitle = when (selectedTab) {
                    NavTab.INSPECTIONS -> "Inspector INS-102 • Shift #402"
                    NavTab.HAZARDS -> "Open & Recent Flags"
                    NavTab.MAP -> "Live Position Overview"
                    NavTab.SYNC -> "Offline Buffer Status"
                },
                showBack = false,
                online = true
            )

            when (selectedTab) {
                NavTab.INSPECTIONS -> InspectionsTabContent(uiState.mines, onStartInspection)
                NavTab.HAZARDS -> HazardsTabContent()
                NavTab.MAP -> MapTabContent(onOpenMap)
                NavTab.SYNC -> SyncTabContent(uiState.queuedItemCount, viewModel::syncNow)
            }
        }
    }
}

@Composable
private fun InspectionsTabContent(mines: List<MineCacheEntity>, onStartInspection: (String) -> Unit) {
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
                        "Mine 1",
                        style = AppType.headlineLg,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Section 1, 2 & 3 • Audit Pipeline #9940",
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
                        enabled = true,
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = OnPrimary)
                        },
                        onClick = { onStartInspection(mines.firstOrNull()?.mineId ?: "dev-mine-1") }
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
                    StatCard(
                        label = "Hazard Index",
                        value = "Low (0.12)",
                        caption = "SECTION 2 CLEAR",
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
                    Text("${mines.size} Mines", style = AppType.labelMd, color = Secondary)
                }

                Spacer(Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    mines.forEach { mine ->
                        ChecklistRow(
                            iconVector = Icons.Filled.Business,
                            title = mine.name,
                            subtitle = "Permit ${mine.permitNumber} • ${mine.sectionCount} sections",
                            trailingBadge = { StatusBadge(text = "PENDING", status = BadgeStatus.NEUTRAL) },
                            onClick = { onStartInspection(mine.mineId) }
                        )
                    }
                }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun HazardsTabContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.marginScreen)
    ) {
        StatCardRow {
            StatCard(label = "Open Hazards", value = "3", captionColor = WarningText, modifier = Modifier.weight(1f))
            StatCard(label = "Critical", value = "1", caption = "SECTION 2", captionColor = CriticalText, modifier = Modifier.weight(1f))
            StatCard(label = "Resolved (7d)", value = "12", captionColor = Tertiary, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(Dimens.sectionGap))
        Text("Active Flags", style = AppType.headlineMd, color = OnSurface)
        Spacer(Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChecklistRow(
                iconVector = Icons.Filled.Warning,
                title = "Loose Rock — Section 2 Corridor",
                subtitle = "Reported 2h ago by INS-088",
                trailingBadge = { StatusBadge(text = "CRITICAL", status = BadgeStatus.CRITICAL) }
            )
            ChecklistRow(
                iconVector = Icons.Filled.SensorsOff,
                title = "Gas Sensor Drift — Mine 1",
                subtitle = "Reported 5h ago by INS-102",
                trailingBadge = { StatusBadge(text = "WARNING", status = BadgeStatus.WARNING) }
            )
            ChecklistRow(
                iconVector = Icons.Filled.CheckCircle,
                title = "PPE Non-Compliance — Mine 2",
                subtitle = "Resolved yesterday",
                trailingBadge = { StatusBadge(text = "RESOLVED", status = BadgeStatus.SUCCESS) }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun MapTabContent(onOpenMap: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.marginScreen)
    ) {
        StatusBadge("LIVE TELEMETRY", BadgeStatus.CRITICAL)
        Spacer(Modifier.height(8.dp))
        Text("Mine 1", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)
        Text("Last position update 12s ago", style = AppType.bodySm, color = Secondary)

        Spacer(Modifier.height(Dimens.gutterCard))
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(Dimens.radiusXl))
                .background(SurfaceContainerLow)
        )

        Spacer(Modifier.height(Dimens.sectionGap))
        PrimaryActionButton(
            text = "Open Full Route Map",
            leadingIcon = { Icon(Icons.Filled.Map, null, tint = OnPrimary) },
            onClick = onOpenMap
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SyncTabContent(queuedItemCount: Int, onSyncNow: () -> Unit) {
    var syncTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(syncTriggered) {
        if (syncTriggered) {
            kotlinx.coroutines.delay(1500)
            syncTriggered = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.marginScreen)
    ) {
        StatCardRow {
            StatCard(
                label = "Queued Items",
                value = "$queuedItemCount",
                captionColor = if (queuedItemCount > 0) WarningText else Tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(Dimens.sectionGap))
        Text(
            if (queuedItemCount > 0) "$queuedItemCount record(s) waiting to sync" else "Everything is synced",
            style = AppType.headlineMd,
            color = OnSurface
        )
        Spacer(Modifier.height(10.dp))

        Spacer(Modifier.height(Dimens.sectionGap))
        PrimaryActionButton(
            text = if (syncTriggered) "Syncing…" else "Sync Now",
            loading = syncTriggered,
            enabled = queuedItemCount > 0,
            leadingIcon = { Icon(Icons.Filled.Sync, null, tint = OnPrimary) },
            onClick = {
                syncTriggered = true
                onSyncNow()
            }
        )

        Spacer(Modifier.height(24.dp))
    }
}
