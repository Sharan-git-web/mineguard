package com.mineinspect.app.ui.screens.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SectionMonitorScreen(
    inspectionId: String,
    sectionId: String,
    onBack: () -> Unit,
    onTakePhoto: () -> Unit,
    onComplete: () -> Unit,
    onMoreChecks: () -> Unit,
    viewModel: SectionMonitorViewModel = hiltViewModel()
) {
    var seconds by remember { mutableStateOf(50) }
    LaunchedEffect(Unit) { while (true) { delay(1000); seconds++ } }

    val displaySection = if (sectionId.equals("B", ignoreCase = true)) "2" else if (sectionId.equals("A", ignoreCase = true)) "1" else if (sectionId.equals("C", ignoreCase = true)) "3" else sectionId
    val photosCaptured by viewModel.photosCaptured.collectAsState()

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Live Inspection Checklist", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Section $displaySection Active", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("Mine 1 • Level -120m", style = AppType.bodySm, color = Secondary)
                }
                StatusBadge("GPS Locked", BadgeStatus.SUCCESS)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow).padding(14.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Route Progression", style = AppType.labelMd, color = OnSurface)
                    Text(if (photosCaptured >= 3) "100%" else "${photosCaptured * 33}%", style = AppType.bodySm, color = Primary)
                }
                Spacer(Modifier.height(6.dp))
                AppLinearProgress(progress = if (photosCaptured >= 3) 1.0f else (photosCaptured * 0.33f), fillColor = Primary)
            }

            Spacer(Modifier.height(Dimens.gutterCard))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLowest).padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Elapsed Time", style = AppType.bodySm, color = Secondary)
                    Text("${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}", style = AppType.headlineLg, color = OnSurface)
                }
                StatusBadge("On Pace", BadgeStatus.SUCCESS)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Photographic Evidence", style = AppType.headlineMd, color = OnSurface)
                StatusBadge("$photosCaptured of 3", if (photosCaptured >= 3) BadgeStatus.SUCCESS else BadgeStatus.WARNING)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            if (photosCaptured < 3) {
                PrimaryActionButton(
                    text = "Take Photo ($photosCaptured/3)",
                    leadingIcon = { Icon(Icons.Filled.CameraAlt, null, tint = OnPrimary) },
                    onClick = onTakePhoto
                )
                Spacer(Modifier.height(10.dp))
                SecondaryActionButton(
                    text = "View Areas Checklist",
                    leadingIcon = { Icon(Icons.Filled.ChecklistRtl, null, tint = OnSurface) },
                    onClick = onBack
                )
            } else {
                PrimaryActionButton(
                    text = "Complete Section (3/3 Photos Done)",
                    leadingIcon = { Icon(Icons.Filled.CheckCircle, null, tint = OnPrimary) },
                    onClick = onComplete
                )
                Spacer(Modifier.height(10.dp))
                SecondaryActionButton(
                    text = "View Areas Checklist",
                    leadingIcon = { Icon(Icons.Filled.ChecklistRtl, null, tint = OnSurface) },
                    onClick = onBack
                )
            }

            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(
                text = "More Checks (Observations, Measurements, PPE…)",
                leadingIcon = { Icon(Icons.Filled.Add, null, tint = OnSurface) },
                onClick = onMoreChecks
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}

