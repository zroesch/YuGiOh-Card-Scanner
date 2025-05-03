package com.example.yugiohcardscanner.ui.scanner

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavHostController
import com.example.yugiohcardscanner.R
import com.example.yugiohcardscanner.ui.scanner.components.CameraPreview
import com.example.yugiohcardscanner.ui.scanner.components.CardPreview
import com.example.yugiohcardscanner.ui.scanner.components.ErrorMessage
import com.example.yugiohcardscanner.ui.scanner.components.ScanningPrompt
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable function for the main scanner screen.
 *
 * This screen allows the user to scan cards using the camera or select images from the gallery.
 * It handles camera permissions, image capture, gallery access, and card scanning states.
 *
 * @param viewModel The [ScannerViewModel] for managing the scanner state and data.
 * @param onNavigateBack Callback to navigate back to the previous screen.
 * @param navController The [NavHostController] for navigation.
 */
@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scanningState by viewModel.scanningState
    val scannedCards by viewModel.scannedCards

    // State to track camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher for camera permission request
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Launcher for gallery image selection
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
                    viewModel.setScanningStateError("Error processing image from gallery.")
                }
            }
        }
    }

    // State to hold the image capture instance
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Request camera permission on launch if not already granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Show camera preview if permission is granted
        if (hasCameraPermission) {
            CameraPreview(
                enabled = scanningState !is ScanningState.ReadyForReview,
                onImageCaptureReady = { imageCapture = it }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
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
                // Close button to navigate back
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
                    .fillMaxWidth(0.80f)
                    .aspectRatio(0.75f)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Shaded area for controls and content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Row: ScanningPrompt or CardPreview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Content based on current scanning state
                        when (val currentState = scanningState) {
                            is ScanningState.ReadyForReview -> {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(scannedCards, key = { it.productId }) { card ->
                                        CardPreview(card = card, onDismiss = {})
                                    }
                                }
                            }

                            is ScanningState.Error -> {
                                ErrorMessage(message = currentState.message) {
                                    viewModel.resetScanningState()
                                }
                            }

                            is ScanningState.Scanning -> {
                                CircularProgressIndicator(color = Color.White)
                            }

                            else -> {
                                if (scannedCards.isEmpty()) {
                                    ScanningPrompt()
                                } else {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(scannedCards, key = { it.productId }) { card ->
                                            CardPreview(card = card, onDismiss = {})
                                        }
                                    }
                                }
                            }
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
                        //Review button
                        IconButton(
                            onClick = {
                                viewModel.moveToReview()
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Review",
                                tint = if (scanningState is ScanningState.Scanning)
                                    Color.White.copy(alpha = 0.5f)
                                else
                                    Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Function to handle taking a picture with the camera.
 *
 * @param imageCapture The ImageCapture instance for capturing images.
 * @param context The application context.
 * @param viewModel The ScannerViewModel for handling the image processing.
 * @param scope The CoroutineScope for launching coroutines.
 */
fun takePicture(
    imageCapture: ImageCapture,
    context: Context,
    viewModel: ScannerViewModel,
    scope: CoroutineScope
) {
    viewModel.captureImage()

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        ContentValues()
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let { uri ->
                    scope.launch {
                        try {
                            val image = InputImage.fromFilePath(context, uri)
                            processImage(image) { setCode ->
                                viewModel.onImageCaptured(setCode)
                            }
                        } catch (e: Exception) {
                            Log.e("ScannerScreen", "Error processing captured image", e)
                            viewModel.setScanningStateError("Error processing captured image.")
                        }
                    }
                }
            }

            override fun onError(error: ImageCaptureException) {
                Log.e("ScannerScreen", "Image capture failed", error)
                viewModel.setScanningStateError("Image capture failed.")
            }
        })
}