package com.mineinspect.app.ui.screens.areas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*

@Composable
fun AreasCoverageScreen(onBack: () -> Unit, onOpenSectionB: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "MineInspect", subtitle = "Inspections", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text("MANDATE ISO 45001 / MSHA", style = AppType.labelSm, color = Secondary)
            Text("Sector Coverage & Audit", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(Dimens.gutterCard))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow).padding(14.dp)
            ) {
                Text("COMPLIANCE PROGRESS", style = AppType.labelSm, color = Secondary)
                Text("1 / 3", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                AppLinearProgress(progress = 0.333f, fillColor = Primary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("INSPECTION SECTORS", style = AppType.labelSm, color = Secondary)
            Spacer(Modifier.height(10.dp))

            SectorCard("SECTOR A", "Primary Pit Haul Ramp", "Acoustic sensor & incline berm integrity verified", BadgeStatus.SUCCESS, "Completed")
            Spacer(Modifier.height(Dimens.gutterCard))

            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLowest).padding(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SECTOR B", style = AppType.labelSm, color = Primary)
                    StatusBadge("Active", BadgeStatus.WARNING)
                }
                Text("Crusher Chute Feed Deck", style = AppType.headlineMd, color = OnSurface)
                Text("Grizzly feeders, transfer chutes & e-stops", style = AppType.bodySm, color = Secondary)
                Spacer(Modifier.height(10.dp))
                PrimaryActionButton(text = "Open Section B (Active)", onClick = onOpenSectionB)
            }
            Spacer(Modifier.height(Dimens.gutterCard))

            SectorCard("SECTOR C", "Explosives Magazine & Bunker", "Perimeter lockouts, ventilation dampers & blast doors", BadgeStatus.NEUTRAL, "Locked")

            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}

@Composable
private fun SectorCard(tag: String, title: String, subtitle: String, badgeStatus: BadgeStatus, badgeText: String) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
            .background(SurfaceContainerLowest).padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(tag, style = AppType.labelSm, color = Secondary)
            StatusBadge(badgeText, badgeStatus)
        }
        Text(title, style = AppType.headlineMd, color = OnSurface)
        Text(subtitle, style = AppType.bodySm, color = Secondary)
    }
}
