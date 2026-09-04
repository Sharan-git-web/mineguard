package com.mineinspect.app.ui.screens.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.theme.*

/** Backs the RANDOM_EVIDENCE route — an extra spot-check photo outside the section's
 *  standard quota. Reuses the existing evidence capture flow rather than duplicating it. */
@Composable
fun RandomEvidenceScreen(
    sectionId: String,
    onBack: () -> Unit,
    onCapture: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Random Spot Check", subtitle = "MineInspect", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Spacer(Modifier.height(Dimens.sectionGap))
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding)
            ) {
                Icon(Icons.Filled.Shuffle, contentDescription = null, tint = Primary)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Random Spot-Check — Section $sectionId",
                    style = AppType.headlineLg,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Capture an additional evidence photo outside the standard per-section quota. This is stored the same way as any other evidence photo.",
                    style = AppType.bodySm,
                    color = Secondary
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = "Capture Spot-Check Photo",
                leadingIcon = { Icon(Icons.Filled.PhotoCamera, null, tint = OnPrimary) },
                onClick = onCapture
            )
        }
    }
}
