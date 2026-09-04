package com.mineinspect.app.ui.screens.submission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mineinspect.app.ui.components.AppTopBar
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.theme.*

@Composable
fun SubmissionCompleteScreen(onReturnHome: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Submission Complete", subtitle = "MineInspect", showBack = false)

        Column(
            Modifier.fillMaxSize().padding(horizontal = Dimens.marginScreen),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(SurfaceContainerLowest)
                    .padding(Dimens.cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Tertiary)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Inspection Submitted",
                    style = AppType.headlineLg,
                    color = OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Your inspection is on its way to the officer dashboard for processing.",
                    style = AppType.bodySm,
                    color = Secondary
                )
            }

            Spacer(Modifier.height(Dimens.sectionGap))
            PrimaryActionButton(
                text = "Return to Home",
                leadingIcon = { Icon(Icons.Filled.Home, null, tint = OnPrimary) },
                onClick = onReturnHome
            )
        }
    }
}
