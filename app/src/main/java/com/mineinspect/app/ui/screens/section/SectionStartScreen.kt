package com.mineinspect.app.ui.screens.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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

private fun sectionTitle(sectionId: String): String = when (sectionId.uppercase()) {
    "1", "A" -> "Section 1"
    "2", "B" -> "Section 2"
    "3", "C" -> "Section 3"
    else -> "Section $sectionId"
}

@Composable
fun SectionStartScreen(
    inspectionId: String,
    sectionId: String,
    onBack: () -> Unit,
    onBegin: () -> Unit
) {
    val displaySection = if (sectionId.equals("B", ignoreCase = true)) "2" else if (sectionId.equals("A", ignoreCase = true)) "1" else if (sectionId.equals("C", ignoreCase = true)) "3" else sectionId

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Active Checkpoint", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            StatusBadge("SECTION READINESS PROTOCOL", BadgeStatus.CRITICAL)
            Spacer(Modifier.height(8.dp))
            Text("Section $displaySection", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)
            Text("Level 4 • ID #CV-882", style = AppType.bodySm, color = Secondary)

            Spacer(Modifier.height(Dimens.gutterCard))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow).padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Location Verified (±1.4m)", style = AppType.labelMd, color = OnSurface)
                StatusBadge("Locked", BadgeStatus.SUCCESS)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest).padding(Dimens.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("00:00", style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                Text("EXPECTED DURATION 15 min", style = AppType.labelSm, color = Secondary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("Inspection Criteria", style = AppType.headlineMd, color = OnSurface)
            Spacer(Modifier.height(10.dp))
            StatCardRow {
                StatCard("Mandatory Photos", "3 Required", modifier = Modifier.weight(1f))
                StatCard("Random Spot Checks", "2 Assigned", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = "Begin Section Check",
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onBegin
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
