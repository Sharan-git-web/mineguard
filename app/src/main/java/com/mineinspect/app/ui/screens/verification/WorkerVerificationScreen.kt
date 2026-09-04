package com.mineinspect.app.ui.screens.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.theme.*

@Composable
fun WorkerVerificationScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: WorkerVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saved.collect { onSaved() }
    }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Worker Verification", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text("Workers Verified Present", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    if (uiState.workerCountText.isEmpty()) {
                        Text("Enter count", style = AppType.headlineMd, color = Secondary)
                    }
                    BasicTextField(
                        value = uiState.workerCountText,
                        onValueChange = viewModel::onWorkerCountChange,
                        singleLine = true,
                        textStyle = AppType.headlineMd.copy(color = OnSurface, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .clickable { viewModel.onToggleAllWearingPpe() }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("All Workers Wearing Required PPE", style = AppType.labelMd, color = OnSurface)
                Text(
                    if (uiState.allWearingPpe) "Yes" else "No",
                    style = AppType.labelMd,
                    color = if (uiState.allWearingPpe) Tertiary else ErrorColor
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("Notes (optional)", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(14.dp)
            ) {
                if (uiState.notes.isEmpty()) {
                    Text("Additional context…", style = AppType.bodyMd, color = Secondary)
                }
                BasicTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChange,
                    textStyle = AppType.bodyMd.copy(color = OnSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, style = AppType.bodySm, color = ErrorColor)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = if (uiState.isSaving) "SAVING..." else "Save Verification",
                loading = uiState.isSaving,
                leadingIcon = { if (!uiState.isSaving) Icon(Icons.Filled.Save, null, tint = OnPrimary) },
                onClick = viewModel::onSaveClick
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
