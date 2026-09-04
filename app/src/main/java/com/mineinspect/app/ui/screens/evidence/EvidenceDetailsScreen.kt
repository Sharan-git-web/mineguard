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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EvidenceDetailsScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: EvidenceDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val evidence = uiState.evidence

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Inspection Audit Form", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Photo Captured", style = AppType.headlineMd, color = OnSurface)
                StatusBadge("Metadata Bound", BadgeStatus.SUCCESS)
            }

            Spacer(Modifier.height(Dimens.gutterCard))

            if (evidence != null) {
                AsyncImage(
                    model = File(evidence.localFilePath),
                    contentDescription = "Captured inspection photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(Dimens.radiusXl))
                )
            } else {
                Box(
                    Modifier.fillMaxWidth().height(200.dp)
                        .clip(RoundedCornerShape(Dimens.radiusXl))
                        .background(SurfaceContainerHigh)
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("TELEMETRY & AUDIT METADATA", style = AppType.labelSm, color = Secondary)
            Spacer(Modifier.height(10.dp))

            val gpsPoint = uiState.gpsPoint
            val gpsText = if (gpsPoint != null) {
                "%.4f°, %.4f° (±%.1fm)".format(gpsPoint.latitude, gpsPoint.longitude, gpsPoint.accuracyMeters)
            } else {
                "No GPS fix linked"
            }
            val timestampText = evidence?.capturedAt?.let {
                SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()).format(Date(it))
            } ?: "—"

            listOf(
                "Mine Facility" to (uiState.mineName ?: "—"),
                "Section / Zone" to "Section ${evidence?.sectionIndex ?: "—"}",
                "GPS Coordinates" to gpsText,
                "Inspector Signature" to "${evidence?.inspectorId ?: "—"} (Inspector)",
                "Timestamp" to timestampText,
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
                Text("SHA-256: ${evidence?.fileHash ?: "—"}", style = AppType.bodySm, color = Secondary)
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
