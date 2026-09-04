package com.mineinspect.app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mineinspect.app.BuildConfig
import com.mineinspect.app.ui.components.PrimaryActionButton
import com.mineinspect.app.ui.components.SecondaryActionButton
import com.mineinspect.app.ui.theme.*

@Composable
fun LoginScreen(
    onSignIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.signedIn.collect {
            onSignIn()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.marginScreen)
    ) {
        Spacer(Modifier.height(24.dp))

        // Brand header card
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.radiusXl))
                .background(SurfaceContainerLow)
                .padding(Dimens.cardPadding)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(Dimens.radiusMd))
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = OnPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("MINE INSPECT", style = AppType.headlineLg, color = OnSurface)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "v4.2",
                            style = AppType.labelSm,
                            color = OnTertiary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(TertiaryContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text("Field Inspector Portal", style = AppType.bodySm, color = Secondary)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SurfaceContainerLowest)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(Modifier.size(6.dp).clip(CircleShape).background(Tertiary))
                    Spacer(Modifier.width(4.dp))
                    Text("MESH ONLINE", style = AppType.labelSm, color = Tertiary)
                }
            }
        }

        Spacer(Modifier.height(Dimens.sectionGap))

        // Auth card
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimens.radiusXl))
                .background(SurfaceContainerLowest)
                .padding(Dimens.cardPadding)
        ) {
            Text("Inspector Verification", style = AppType.headlineLg, color = OnSurface)
            Text(
                "Enter certified pin to unlock safety checklist telemetry",
                style = AppType.bodySm,
                color = Secondary
            )

            Spacer(Modifier.height(20.dp))

            // Inspector ID
            Text("Inspector ID", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Badge, contentDescription = null, tint = Secondary)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (uiState.inspectorId.isEmpty()) {
                        Text("Enter Inspector ID", style = AppType.headlineMd, color = Secondary)
                    }
                    BasicTextField(
                        value = uiState.inspectorId,
                        onValueChange = viewModel::onInspectorIdChange,
                        singleLine = true,
                        textStyle = AppType.headlineMd.copy(color = OnSurface, fontWeight = FontWeight.Bold),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (uiState.inspectorId.isNotBlank()) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Tertiary)
                }
            }

            Spacer(Modifier.height(16.dp))

            // PIN
            Text("8-Digit Security PIN", style = AppType.labelMd, color = OnSurface)
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Secondary)
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f)) {
                    if (uiState.pin.isEmpty()) {
                        Text("Enter PIN", style = AppType.headlineLg, color = Secondary)
                    }
                    BasicTextField(
                        value = uiState.pin,
                        onValueChange = viewModel::onPinChange,
                        singleLine = true,
                        textStyle = AppType.headlineLg.copy(color = OnSurface),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = if (uiState.pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = viewModel::onTogglePinVisibility) {
                    Icon(
                        if (uiState.pinVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Toggle PIN visibility",
                        tint = Secondary
                    )
                }
            }

            uiState.errorMessage?.let { message ->
                Spacer(Modifier.height(8.dp))
                Text(message, style = AppType.bodySm, color = ErrorColor)
            }

            Spacer(Modifier.height(20.dp))

            // Sign in action
            PrimaryActionButton(
                text = if (uiState.isSigningIn) "AUTHORIZING..." else "SIGN IN TO FIELD TERMINAL",
                loading = uiState.isSigningIn,
                trailingIcon = {
                    if (!uiState.isSigningIn) Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = OnPrimary)
                },
                onClick = viewModel::onSignInClick
            )

            if (BuildConfig.DEBUG) {
                Spacer(Modifier.height(10.dp))
                SecondaryActionButton(
                    text = "Skip Login (Dev Build Only)",
                    onClick = viewModel::onDevBypassClick
                )
            }

            Spacer(Modifier.height(12.dp))

            // Biometric hardware isn't integrated yet (no biometric API call exists) — this
            // row stays visually in place but no longer bypasses real credential validation.
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimens.radiusMd))
                    .background(SurfaceContainerLow)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Fingerprint, contentDescription = null, tint = Secondary)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Tap biometric scanner on rugged case",
                    style = AppType.labelMd,
                    color = OnSurface
                )
            }
        }

        Spacer(Modifier.height(Dimens.sectionGap))
    }
}
