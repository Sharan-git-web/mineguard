package com.mineinspect.app.ui.screens.measurement

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
fun MeasurementEntryScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: MeasurementEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saved.collect { onSaved() }
    }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Measurement Entry", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text("Metric Type", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MEASUREMENT_METRICS.forEach { metric ->
                    val selected = metric.type == uiState.metric.type
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimens.radiusMd))
                            .background(if (selected) PrimaryContainer else SurfaceContainerLow)
                            .clickable { viewModel.onMetricChange(metric) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(metric.label, style = AppType.labelMd, color = if (selected) OnPrimaryContainer else OnSurface)
                        Text(metric.unit, style = AppType.bodySm, color = if (selected) OnPrimaryContainer else Secondary)
                    }
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Text("Value", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    if (uiState.valueText.isEmpty()) {
                        Text("Enter value", style = AppType.headlineMd, color = Secondary)
                    }
                    BasicTextField(
                        value = uiState.valueText,
                        onValueChange = viewModel::onValueChange,
                        singleLine = true,
                        textStyle = AppType.headlineMd.copy(color = OnSurface, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Text(uiState.metric.unit, style = AppType.headlineMd, color = Secondary)
            }

            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, style = AppType.bodySm, color = ErrorColor)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Threshold evaluation happens server-side once this syncs — no pass/fail is shown here.",
                style = AppType.bodySm,
                color = Secondary
            )

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = if (uiState.isSaving) "SAVING..." else "Save Measurement",
                loading = uiState.isSaving,
                leadingIcon = { if (!uiState.isSaving) Icon(Icons.Filled.Save, null, tint = OnPrimary) },
                onClick = viewModel::onSaveClick
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
