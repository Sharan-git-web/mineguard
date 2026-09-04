package com.mineinspect.app.ui.screens.observation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.components.Severity
import com.mineinspect.app.ui.components.SeverityCaption
import com.mineinspect.app.ui.components.SeveritySelector
import com.mineinspect.app.ui.theme.*

@Composable
fun ManualObservationScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ManualObservationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saved.collect { onSaved() }
    }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Manual Observation", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text("Category", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ManualObservationUiState.CATEGORIES.forEach { category ->
                    val selected = category == uiState.category
                    Text(
                        category,
                        style = AppType.labelSm,
                        color = if (selected) OnPrimary else OnSurface,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Primary else SurfaceContainerLow)
                            .clickable { viewModel.onCategoryChange(category) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("Severity", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            SeveritySelector(selected = uiState.severity, onSelect = viewModel::onSeverityChange)
            Spacer(Modifier.height(6.dp))
            SeverityCaption(uiState.severity)

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("Notes", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(14.dp)
            ) {
                if (uiState.notes.isEmpty()) {
                    Text("Describe what you observed…", style = AppType.bodyMd, color = Secondary)
                }
                BasicTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChange,
                    textStyle = AppType.bodyMd.copy(color = OnSurface),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = if (uiState.isSaving) "SAVING..." else "Save Observation",
                loading = uiState.isSaving,
                leadingIcon = { if (!uiState.isSaving) Icon(Icons.Filled.Save, null, tint = OnPrimary) },
                onClick = viewModel::onSaveClick
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
