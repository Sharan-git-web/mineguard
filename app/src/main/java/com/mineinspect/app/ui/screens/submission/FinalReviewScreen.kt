package com.mineinspect.app.ui.screens.submission

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.components.SecondaryActionButton
import com.mineinspect.app.ui.components.StatCard
import com.mineinspect.app.ui.components.StatCardRow
import com.mineinspect.app.ui.theme.*

/** Backs FINAL_REVIEW; also serves INSPECTION_SUMMARY's intent in one screen (see
 *  FinalReviewViewModel doc). */
@Composable
fun FinalReviewScreen(
    onBack: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: FinalReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.submitted.collect { onSubmitted() }
    }

    LaunchedEffect(Unit) {
        viewModel.reportReady.collect { file ->
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No PDF viewer installed to open the report", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(Modifier.fillMaxSize().background(Surface).verticalScroll(rememberScrollState())) {
        AppTopBar(title = "Final Review", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Text(
                "Inspection Summary",
                style = AppType.headlineXl.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text(
                "Review everything captured before submitting for processing.",
                style = AppType.bodySm,
                color = Secondary
            )

            Spacer(Modifier.height(Dimens.sectionGap))
            StatCardRow {
                StatCard("Photos", "${uiState.photoCount}", modifier = Modifier.weight(1f))
                StatCard("Observations", "${uiState.observationCount}", modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(Dimens.gutterCard))
            StatCardRow {
                StatCard("Measurements", "${uiState.measurementCount}", modifier = Modifier.weight(1f))
                StatCard("GPS Points", "${uiState.gpsPointCount}", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(if (uiState.unsyncedCount > 0) WarningBg else SuccessBg)
                    .padding(14.dp)
            ) {
                Text(
                    if (uiState.unsyncedCount > 0) {
                        "${uiState.unsyncedCount} item(s) still syncing"
                    } else {
                        "All items synced — ready to submit"
                    },
                    style = AppType.labelMd,
                    color = if (uiState.unsyncedCount > 0) WarningText else SuccessText
                )
            }

            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, style = AppType.bodySm, color = ErrorColor)
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            SecondaryActionButton(
                text = if (uiState.isGeneratingReport) "Generating…" else "Generate Report",
                leadingIcon = { if (!uiState.isGeneratingReport) Icon(Icons.Filled.Description, null, tint = OnSurface) },
                enabled = !uiState.isGeneratingReport,
                onClick = viewModel::onGenerateReportClick
            )
            Spacer(Modifier.height(Dimens.gutterCard))
            PrimaryActionButton(
                text = if (uiState.isSubmitting) "SUBMITTING..." else "Confirm & Submit",
                loading = uiState.isSubmitting,
                leadingIcon = { if (!uiState.isSubmitting) Icon(Icons.Filled.Send, null, tint = OnPrimary) },
                onClick = viewModel::onSubmitClick
            )
            Spacer(Modifier.height(Dimens.sectionGap))
        }
    }
}
