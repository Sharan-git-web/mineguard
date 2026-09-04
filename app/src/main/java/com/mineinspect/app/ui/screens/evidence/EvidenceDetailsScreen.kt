package com.mineinspect.app.ui.screens.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*

@Composable
fun EvidenceDetailsScreen(onBack: () -> Unit, onSave: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Inspection Audit Form", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Photo Captured", style = AppType.headlineMd, color = OnSurface)
                StatusBadge("Metadata Bound", BadgeStatus.SUCCESS)
            }

            Spacer(Modifier.height(Dimens.gutterCard))
            Box(
                Modifier.fillMaxWidth().height(200.dp)
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerHigh)
            )

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("TELEMETRY & AUDIT METADATA", style = AppType.labelSm, color = Secondary)
            Spacer(Modifier.height(10.dp))

            listOf(
                "Mine Facility" to "Blackwood Colliery — Shaft 4B",
                "Section / Zone" to "Section B — Primary Conveyor",
                "GPS Coordinates" to "33.0841° N, 109.3512° W",
                "Inspector Signature" to "INS-102 (J. Vance)",
                "Timestamp" to "04 Sep 2026, 10:21:44 AM",
            ).forEach { (label, value) ->
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                        .background(SurfaceContainerLowest).padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = AppType.bodySm, color = Secondary)
                    Text(value, style = AppType.labelMd, color = OnSurface)
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow).padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SHA-256: e8f4c2b9a712...890d", style = AppType.bodySm, color = Secondary)
                Text("Copy", style = AppType.labelSm, color = Primary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = "Save & Continue",
                leadingIcon = { Icon(Icons.Filled.Save, null, tint = OnPrimary) },
                onClick = onSave
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
