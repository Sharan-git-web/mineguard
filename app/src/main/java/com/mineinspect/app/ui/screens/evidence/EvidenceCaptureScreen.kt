package com.mineinspect.app.ui.screens.evidence

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mineinspect.app.ui.components.*
import com.mineinspect.app.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("MINE_${timeStamp}_", ".jpg", storageDir)
}

private fun createImageUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
        context,
        "com.mineinspect.app.fileprovider",
        file
    )
}

@Composable
fun EvidenceCaptureScreen(
    sectionId: String,
    onBack: () -> Unit,
    onCaptured: (String) -> Unit,
    viewModel: EvidenceCaptureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.evidenceSaved.collect { evidenceId ->
            onCaptured(evidenceId)
        }
    }

    // URI/File we tell the camera to write into
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = photoUri
        val file = photoFile
        if (success && uri != null && file != null) {
            viewModel.onPhotoCaptured(uri, file)
        }
    }

    Column(Modifier.fillMaxSize().background(Surface)) {
        AppTopBar(title = "Inspection Audit Form", onBack = onBack)

        Column(Modifier.padding(horizontal = Dimens.marginScreen)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Section $sectionId Capture",
                    style = AppType.headlineLg, color = OnSurface
                )
            }
            Text("Tap the button below to open the camera", style = AppType.bodySm, color = Secondary)

            Spacer(Modifier.height(12.dp))

            // Preview box — shows the captured photo after the camera hands it back
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clip(RoundedCornerShape(Dimens.radiusXl))
                    .background(InverseSurface),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.capturedUri != null) {
                    AsyncImage(
                        model = uiState.capturedUri,
                        contentDescription = "Captured photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(Dimens.radiusXl))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Camera preview will appear here",
                            style = AppType.bodySm,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.sectionGap))

            // Shutter row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .border(3.dp, Primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable(enabled = !uiState.isSaving) {
                                if (!hasCameraPermission) {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                } else {
                                    val file = createImageFile(context)
                                    val uri = createImageUri(context, file)
                                    photoFile = file
                                    photoUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.PhotoCamera,
                            contentDescription = "Take Photo",
                            tint = OnPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                if (!hasCameraPermission) "Tap to grant camera permission"
                else if (uiState.isSaving) "Saving photo…"
                else "Tap to open camera",
                style = AppType.bodySm,
                color = Secondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(Dimens.gutterCard))
        }
    }
}
