package com.mineinspect.app.ui.screens.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SectionMonitorScreen(
    sectionId: String,
    onBack: () -> Unit,
    onTakePhoto: () -> Unit,
    onComplete: () -> Unit
) {
    var seconds by remember { mutableStateOf(50) }
    LaunchedEffect(Unit) { while (true) { delay(1000); seconds++ } }
    var photosCaptured by remember { mutableStateOf(1) }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Live Inspection Checklist", subtitle = "Mine Inspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Section $sectionId Active", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("Shaft 4 • Level -120m", style = AppType.bodySm, color = Secondary)
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
                    Text("60%", style = AppType.bodySm, color = Primary)
                }
                Spacer(Modifier.height(6.dp))
                AppLinearProgress(progress = 0.6f, fillColor = Primary)
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
                StatusBadge("$photosCaptured of 3", BadgeStatus.WARNING)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = "Take Photo",
                leadingIcon = { Icon(Icons.Filled.CameraAlt, null, tint = OnPrimary) },
                onClick = { if (photosCaptured < 3) photosCaptured++; onTakePhoto() }
            )
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(
                text = if (photosCaptured >= 3) "Complete Section" else "View Areas Checklist",
                leadingIcon = { Icon(Icons.Filled.ChecklistRtl, null, tint = OnSurface) },
                onClick = { if (photosCaptured >= 3) onComplete() }
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
