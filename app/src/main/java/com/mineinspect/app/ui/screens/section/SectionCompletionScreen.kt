package com.mineinspect.app.ui.screens.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.components.StatCard
import com.mineinspect.app.ui.components.StatCardRow
import com.mineinspect.app.ui.theme.*

@Composable
fun SectionCompletionScreen(
    sectionId: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    viewModel: SectionCompletionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Section Complete", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Spacer(Modifier.height(Dimens.sectionGap))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Tertiary)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Section $sectionId Ready to Close",
                    style = AppType.headlineLg,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Review what was captured before moving on.",
                    style = AppType.bodySm,
                    color = Secondary
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            StatCardRow {
                StatCard("Photos", "${uiState.photoCount}", modifier = Modifier.weight(1f))
                StatCard("Observations", "${uiState.observationCount}", modifier = Modifier.weight(1f))
                StatCard("Measurements", "${uiState.measurementCount}", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = "Confirm & Continue",
                trailingIcon = { Icon(Icons.Filled.ArrowForward, null, tint = OnPrimary) },
                onClick = onConfirm
            )
        }
    }
}
