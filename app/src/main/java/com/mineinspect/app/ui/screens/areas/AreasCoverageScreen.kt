package com.mineinspect.app.ui.screens.areas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
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
fun AreasCoverageScreen(
    inspectionId: String,
    onBack: () -> Unit,
    onOpenSectionB: () -> Unit,
    onOpenSection3: () -> Unit = {},
    onCompleteAudit: () -> Unit = {},
    viewModel: AreasCoverageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sec2Done = uiState.sec2Photos >= 3
    val sec3Done = uiState.sec3Photos >= 3

    val progressCount = if (sec3Done) 3 else if (sec2Done) 2 else 1
    val progressFloat = if (sec3Done) 1.0f else if (sec2Done) 0.666f else 0.333f

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "MineInspect", subtitle = "Inspections", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text("COMPLIANCE AUDIT", style = AppType.labelSm, color = Secondary)
            Text("Areas Coverage & Progress", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(Dimens.gutterCard))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow).padding(14.dp)
            ) {
                Text("COMPLIANCE PROGRESS", style = AppType.labelSm, color = Secondary)
                Text("$progressCount / 3", style = AppType.headlineLg, color = OnSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                AppLinearProgress(progress = progressFloat, fillColor = Primary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("INSPECTION AREAS", style = AppType.labelSm, color = Secondary)
            Spacer(Modifier.height(10.dp))

            // Area 1
            SectorCard("AREA 1", "Section 1 Main Deck", "Sensors & incline berm integrity verified", BadgeStatus.SUCCESS, "Completed")
            Spacer(Modifier.height(Dimens.gutterCard))

            // Area 2
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLowest).padding(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("AREA 2", style = AppType.labelSm, color = Primary)
                    StatusBadge(if (sec2Done) "Completed" else "Active", if (sec2Done) BadgeStatus.SUCCESS else BadgeStatus.WARNING)
                }
                Text("Section 2 Main Deck", style = AppType.headlineMd, color = OnSurface)
                Text("Feeders, transfer chutes & safety checks", style = AppType.bodySm, color = Secondary)
                Spacer(Modifier.height(10.dp))
                if (sec2Done) {
                    SecondaryActionButton(
                        text = "Review Section 2 (Completed 3/3)",
                        onClick = onOpenSectionB
                    )
                } else {
                    PrimaryActionButton(
                        text = "Open Section 2 (Active)",
                        onClick = onOpenSectionB
                    )
                }
            }
            Spacer(Modifier.height(Dimens.gutterCard))

            // Area 3
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLowest).padding(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("AREA 3", style = AppType.labelSm, color = if (sec2Done) Primary else Secondary)
                    StatusBadge(
                        text = if (sec3Done) "Completed" else if (sec2Done) "Active" else "Locked",
                        status = if (sec3Done) BadgeStatus.SUCCESS else if (sec2Done) BadgeStatus.WARNING else BadgeStatus.NEUTRAL
                    )
                }
                Text("Section 3 Main Deck", style = AppType.headlineMd, color = OnSurface)
                Text("Perimeter lockouts & blast doors", style = AppType.bodySm, color = Secondary)
                Spacer(Modifier.height(10.dp))
                if (sec3Done) {
                    SecondaryActionButton(
                        text = "Review Section 3 (Completed 3/3)",
                        onClick = onOpenSection3
                    )
                } else if (sec2Done) {
                    PrimaryActionButton(
                        text = "Proceed to Section 3 (Active)",
                        leadingIcon = { Icon(Icons.Filled.PlayArrow, null, tint = OnPrimary) },
                        onClick = onOpenSection3
                    )
                } else {
                    Text("Complete Section 2 to unlock Section 3", style = AppType.bodySm, color = Secondary)
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            if (sec3Done) {
                PrimaryActionButton(
                    text = "Complete & Submit Inspection Audit",
                    leadingIcon = { Icon(Icons.Filled.CheckCircle, null, tint = OnPrimary) },
                    onClick = onCompleteAudit
                )
                Spacer(Modifier.height(Dimens.sectionGap))
            }
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
