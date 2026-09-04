package com.mineinspect.app.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*

@Composable
fun RouteMapScreen(
    onBack: () -> Unit,
    viewModel: RouteMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "MineInspect", subtitle = "Map", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            StatusBadge("SUB-LEVEL 03 ACTIVE TELEMETRY", BadgeStatus.CRITICAL)
            Spacer(Modifier.height(8.dp))
            Text("Mine Route & Live Position Map", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(Dimens.gutterCard))
            StatCardRow {
                StatCard("Heading", "074° E", modifier = Modifier.weight(1f))
                StatCard("Depth", "-240 m", modifier = Modifier.weight(1f))
                StatCard("Pace", "1.2 m/s", modifier = Modifier.weight(1f))
                StatCard("UWB Acc.", "±4.8m", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // Map placeholder
            Box(
                Modifier.fillMaxWidth().height(260.dp)
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLow)
            )

            Spacer(Modifier.height(Dimens.sectionGap))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("INSPECTION ROUTE PIPELINE", style = AppType.labelSm, color = Secondary)
                Text("66% Completed", style = AppType.bodySm, color = Tertiary)
            }
            Spacer(Modifier.height(10.dp))

            listOf(
                Triple("Mine 1 Entrance", "Cleared 08:42 UTC • Atmospheric Pass", "0.0 km"),
                Triple("Section 1 Hub", "Cleared 09:14 UTC • 12 Checkpoints Logged", "0.4 km"),
                Triple("Section 2 Corridor", "18 Checkpoints Logged • Active Zone", "Active Now"),
            ).forEach { (title, subtitle, trail) ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                        .background(SurfaceContainerLowest).padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(title, style = AppType.labelMd, color = OnSurface)
                        Text(subtitle, style = AppType.bodySm, color = Secondary)
                    }
                    StatusBadge(trail, if (trail == "Active Now") BadgeStatus.CRITICAL else BadgeStatus.SUCCESS)
                }
                Spacer(Modifier.height(Dimens.gutterCard))
            }

            PrimaryActionButton(
                text = "Areas Coverage Checklist (18/24)",
                leadingIcon = { Icon(Icons.Filled.ChecklistRtl, null, tint = OnPrimary) },
                onClick = onBack
            )
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(
                text = when {
                    uiState.pinDropped -> "Hazard Pin Logged"
                    !uiState.hasInspectionContext -> "Drop Geo Hazard Pin (start an inspection first)"
                    uiState.isDroppingPin -> "Logging Pin…"
                    else -> "Drop Geo Hazard Pin"
                },
                leadingIcon = { Icon(Icons.Filled.AddLocationAlt, null, tint = OnSurface) },
                enabled = uiState.hasInspectionContext && !uiState.isDroppingPin && !uiState.pinDropped,
                onClick = viewModel::onDropHazardPin
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
