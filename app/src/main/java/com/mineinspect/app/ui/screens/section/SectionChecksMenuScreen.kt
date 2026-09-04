package com.mineinspect.app.ui.screens.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.ChecklistRow
import com.mineinspect.app.ui.theme.*

/**
 * Connective screen letting the inspector reach the observation/measurement/verification/
 * random-evidence flows from an active section without cluttering SectionMonitorScreen's
 * existing layout with five extra buttons. Not one of the plan's named routes on its own —
 * it's the fan-out point those routes need to be reachable from.
 */
@Composable
fun SectionChecksMenuScreen(
    onBack: () -> Unit,
    onManualObservation: () -> Unit,
    onMeasurementEntry: () -> Unit,
    onPpeVerification: () -> Unit,
    onWorkerVerification: () -> Unit,
    onRandomEvidence: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Additional Section Checks", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text(
                "Log anything beyond the standard photo quota for this section.",
                style = AppType.bodySm,
                color = Secondary
            )
            Spacer(Modifier.height(Dimens.sectionGap))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ChecklistRow(
                    iconVector = Icons.Filled.ReportProblem,
                    title = "Manual Observation",
                    subtitle = "Log a hazard, anomaly, or general note",
                    onClick = onManualObservation
                )
                ChecklistRow(
                    iconVector = Icons.Filled.Speed,
                    title = "Measurement Entry",
                    subtitle = "Gas, temperature, noise, or structural readings",
                    onClick = onMeasurementEntry
                )
                ChecklistRow(
                    iconVector = Icons.Filled.HealthAndSafety,
                    title = "PPE Verification",
                    subtitle = "Confirm required protective equipment",
                    onClick = onPpeVerification
                )
                ChecklistRow(
                    iconVector = Icons.Filled.Groups,
                    title = "Worker Verification",
                    subtitle = "Confirm workers present and PPE-compliant",
                    onClick = onWorkerVerification
                )
                ChecklistRow(
                    iconVector = Icons.Filled.PhotoCamera,
                    title = "Random Spot-Check Photo",
                    subtitle = "Capture evidence outside the standard quota",
                    onClick = onRandomEvidence
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
