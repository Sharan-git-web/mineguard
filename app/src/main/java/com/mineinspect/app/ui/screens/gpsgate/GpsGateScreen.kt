package com.mineinspect.app.ui.screens.gpsgate

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GpsGateScreen(
    inspectionId: String,
    onBack: () -> Unit,
    onStartInspection: () -> Unit,
    onCancel: () -> Unit,
    viewModel: GpsGateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(granted) }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            viewModel.acquireFix()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
    ) {
        AppTopBar(title = "Active Checkpoint", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            StatusBadge("GATE LOCK VERIFICATION", BadgeStatus.CRITICAL)
            Spacer(Modifier.height(8.dp))
            Text("Start Inspection & GPS Gate", style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold), color = OnSurface)
            Text(
                "Verify geographic lock and field compliance at ${uiState.mineName ?: "the mine"} before inspection.",
                style = AppType.bodySm, color = Secondary
            )

            Spacer(Modifier.height(Dimens.sectionGap))

            // Inspector identity card
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Column(Modifier.weight(1f)) {
                    Text(uiState.inspectorId ?: "—", style = AppType.headlineMd, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text("Inspector", style = AppType.bodySm, color = Secondary)
                    Spacer(Modifier.height(10.dp))
                    StatCardRow {
                        StatCard("Target Mine", uiState.mineName ?: "—", modifier = Modifier.weight(1f))
                        StatCard("Mine Gate", "${uiState.mineName ?: "—"} Gate", modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(Dimens.gutterCard))
            val timestampText = uiState.capturedAt?.let {
                SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()).format(Date(it))
            } ?: "Awaiting fix"
            StatCardRow {
                StatCard("Date", SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()), modifier = Modifier.weight(1f))
                StatCard("Timestamp", timestampText, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // GNSS telemetry card
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GNSS Fix Status", style = AppType.labelMd, color = OnSurface)
                    StatusBadge(
                        if (uiState.hasFix) "FIX ACQUIRED" else if (uiState.isAcquiring) "ACQUIRING" else "NO FIX",
                        if (uiState.hasFix) BadgeStatus.SUCCESS else BadgeStatus.WARNING
                    )
                }
                Spacer(Modifier.height(10.dp))
                StatCardRow {
                    StatCard(
                        "Accuracy",
                        uiState.accuracyMeters?.let { "±${"%.1f".format(it)}m" } ?: "—",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        "Fix Source",
                        "Device GPS",
                        captionColor = Tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // Perimeter gate ready card
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLow)
                    .padding(Dimens.cardPadding)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (uiState.hasFix) Icons.Filled.CheckCircle else Icons.Filled.GpsNotFixed,
                        contentDescription = null,
                        tint = if (uiState.hasFix) Tertiary else Secondary
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Location Lock", style = AppType.labelLg, color = OnSurface)
                        Text(
                            if (uiState.permissionDenied) "Location permission denied — required to proceed"
                            else if (uiState.hasFix) "Real device GPS fix captured and queued for sync"
                            else "Acquiring device location…",
                            style = AppType.bodySm, color = Secondary
                        )
                    }
                    StatusBadge(if (uiState.hasFix) "PASS" else "PENDING", if (uiState.hasFix) BadgeStatus.SUCCESS else BadgeStatus.NEUTRAL)
                }
                Spacer(Modifier.height(12.dp))
                AppLinearProgress(progress = if (uiState.hasFix) 1f else if (uiState.isAcquiring) 0.5f else 0f, fillColor = Tertiary)
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            if (uiState.permissionDenied) {
                SecondaryActionButton(
                    text = "Grant Location Permission",
                    leadingIcon = { Icon(Icons.Filled.LocationOn, null, tint = OnSurface) },
                    onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )
                Spacer(Modifier.height(10.dp))
            }

            PrimaryActionButton(
                text = "Start Inspection",
                enabled = uiState.hasFix,
                leadingIcon = { Icon(Icons.Filled.PlayArrow, null, tint = OnPrimary) },
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onStartInspection
            )
            Spacer(Modifier.height(10.dp))
            SecondaryActionButton(
                text = "Cancel / Return to Roster",
                leadingIcon = { Icon(Icons.Filled.Cancel, null, tint = OnSurface) },
                onClick = onCancel
            )

            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
