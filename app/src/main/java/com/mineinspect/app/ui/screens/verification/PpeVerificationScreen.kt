package com.mineinspect.app.ui.screens.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.theme.*

@Composable
fun PpeVerificationScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: PpeVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saved.collect { onSaved() }
    }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "PPE Verification", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text(
                "Tap each item to toggle Pass/Fail based on what you observe.",
                style = AppType.bodySm,
                color = Secondary
            )
            Spacer(Modifier.height(Dimens.sectionGap))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.items.forEachIndexed { index, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimens.radiusMd))
                            .background(SurfaceContainerLowest)
                            .clickable { viewModel.onToggleItem(index) }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.label, style = AppType.labelMd, color = OnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (item.passed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                contentDescription = null,
                                tint = if (item.passed) Tertiary else ErrorColor
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (item.passed) "Pass" else "Fail",
                                style = AppType.labelMd,
                                color = if (item.passed) Tertiary else ErrorColor
                            )
                        }
                    }
                }
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
