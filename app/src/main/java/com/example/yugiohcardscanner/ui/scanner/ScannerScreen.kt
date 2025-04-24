package com.example.yugiohcardscanner.ui.scanner

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yugiohcardscanner.R
import com.example.yugiohcardscanner.ui.scanner.components.CameraPreview
import com.example.yugiohcardscanner.ui.scanner.components.CardPreview
import com.example.yugiohcardscanner.ui.scanner.components.ErrorMessage
import com.example.yugiohcardscanner.ui.scanner.components.ScanningPrompt
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToReview: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scanningState = viewModel.scanningState

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    val image = InputImage.fromFilePath(context, uri)
                    processImage(image) { setCode ->
                        scope.launch {
                            viewModel.searchCardBySetCode(setCode)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ScannerScreen", "Error processing gallery image", e)
                }
            }
        }
    }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (hasCameraPermission) {
            CameraPreview(
                enabled = scanningState !is ScanningState.Success,
                onImageCaptureReady = { imageCapture = it }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),

            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Start),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            // Scanning Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .aspectRatio(0.75f)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            Spacer(modifier = Modifier.height(32.dp))  // Spacing

            // Shaded area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .navigationBarsPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // State-specific content
                    when (scanningState) {
                        is ScanningState.Success -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                CardPreview(card = scanningState.card) {
                                    viewModel.resetScanningState()
                                }
                            }
                        }

                        is ScanningState.Error -> {
                            ErrorMessage(message = scanningState.message) {
                                viewModel.resetScanningState()
                            }
                        }

                        is ScanningState.Scanning -> {
                            CircularProgressIndicator(color = Color.White)
                        }

                        else -> {
                            ScanningPrompt()
                        }
                    }

                    // Bottom controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery button
                        IconButton(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            enabled = scanningState !is ScanningState.Scanning,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_gallery),
                                contentDescription = "Gallery",
                                tint = if (scanningState is ScanningState.Scanning)
                                    Color.White.copy(alpha = 0.5f)
                                else
                                    Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Capture button
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape)
                                .clickable(enabled = scanningState !is ScanningState.Scanning) {
                                    imageCapture?.let { takePicture(it, context, viewModel, scope) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.Gray, CircleShape)
                            )
                        }

                        // Review button
                        IconButton(
                            onClick = {
                                val testImage =
                                    BitmapFactory.decodeResource(context.resources, R.drawable.card)
                                val inputImage = InputImage.fromBitmap(testImage, 0)

                                processImage(inputImage) { setCode ->
                                    Log.d("ManualTest", "Detected: $setCode")
                                    scope.launch {
                                        viewModel.searchCardBySetCode(setCode)
                                    }
                                }
                            },
                            enabled = viewModel.scannedCards.isNotEmpty(),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Review",
                                tint = if (viewModel.scannedCards.isNotEmpty())
                                    Color.White
                                else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun takePicture(
    imageCapture: ImageCapture,
    context: android.content.Context,
    viewModel: ScannerViewModel,
    scope: kotlinx.coroutines.CoroutineScope
) {
    val name = "photo_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        .build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("ScannerScreen", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d("ScannerScreen", "Photo capture succeeded: ${output.savedUri}")
                output.savedUri?.let {
                    scope.launch {
                        try {
                            val image = InputImage.fromFilePath(context, it)
                            processImage(image) { setCode ->
                                scope.launch {
                                    viewModel.searchCardBySetCode(setCode)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ScannerScreen", "Error processing captured image", e)
                        }
                    }
                }
            }
        }
    )
}