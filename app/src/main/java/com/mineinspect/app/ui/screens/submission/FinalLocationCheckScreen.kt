package com.mineinspect.app.ui.screens.submission

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.LocationOn
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
import com.mineinspect.app.ui.components.AppLinearProgress
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.components.SecondaryActionButton
import com.mineinspect.app.ui.components.StatusBadge
import com.mineinspect.app.ui.components.BadgeStatus
import com.mineinspect.app.ui.theme.*

@Composable
fun FinalLocationCheckScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: FinalLocationCheckViewModel = hiltViewModel()
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
        if (hasPermission) viewModel.acquireFix() else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Final Location Check", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            StatusBadge("PRE-SUBMISSION VERIFICATION", BadgeStatus.CRITICAL)
            Spacer(Modifier.height(8.dp))
            Text(
                "Confirm On-Site Location",
                style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text(
                "One last real GPS fix before this inspection can be submitted.",
                style = AppType.bodySm,
                color = Secondary
            )

            Spacer(Modifier.height(Dimens.sectionGap))
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
                            when {
                                uiState.permissionDenied -> "Location permission denied — required to proceed"
                                uiState.hasFix -> "Fix confirmed — ±${"%.1f".format(uiState.accuracyMeters ?: 0f)}m"
                                else -> "Acquiring device location…"
                            },
                            style = AppType.bodySm, color = Secondary
                        )
                    }
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
                text = "Confirm Location & Continue",
                enabled = uiState.hasFix,
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onContinue
            )
        }
    }
}
