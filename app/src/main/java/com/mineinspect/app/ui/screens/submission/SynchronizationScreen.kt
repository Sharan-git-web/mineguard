package com.mineinspect.app.ui.screens.submission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.data.local.SyncState
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.components.SecondaryActionButton
import com.mineinspect.app.ui.theme.*

/** Backs SYNCHRONIZATION and, when nothing is actively in-flight yet, reads naturally as
 *  OFFLINE_SAVE's "saved locally, will sync when online" state too — see
 *  SynchronizationViewModel doc for why this wasn't split into two screens. */
@Composable
fun SynchronizationScreen(
    onContinue: () -> Unit,
    viewModel: SynchronizationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isProcessing = uiState.inspectionSyncState == SyncState.PROCESSING.name ||
        uiState.inspectionSyncState == SyncState.SYNCING.name

    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Synchronization", subtitle = "MineInspect", showBack = false)

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.marginScreen)
        ) {
            Spacer(Modifier.height(Dimens.sectionGap))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.unsyncedCount > 0) {
                    CircularProgressIndicator(color = Primary)
                    Spacer(Modifier.height(12.dp))
                    Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = Primary)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Saved Locally — Syncing…",
                        style = AppType.headlineLg,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${uiState.unsyncedCount} item(s) remaining. Nothing is lost if you close the app now — sync resumes automatically.",
                        style = AppType.bodySm,
                        color = Secondary
                    )
                } else {
                    Icon(
                        if (isProcessing) Icons.Filled.CloudSync else Icons.Filled.CloudDone,
                        contentDescription = null,
                        tint = Tertiary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (isProcessing) "Synced — Server Processing" else "Fully Synced",
                        style = AppType.headlineLg,
                        color = OnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isProcessing) {
                            "All data reached the server. Risk scoring and report generation are still running."
                        } else {
                            "Every item for this inspection has synced."
                        },
                        style = AppType.bodySm,
                        color = Secondary
                    )
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            if (uiState.unsyncedCount > 0) {
                SecondaryActionButton(text = "Retry Sync Now", onClick = viewModel::syncNow)
                Spacer(Modifier.height(10.dp))
            }
            PrimaryActionButton(
                text = "Continue",
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onContinue
            )
        }
    }
}
