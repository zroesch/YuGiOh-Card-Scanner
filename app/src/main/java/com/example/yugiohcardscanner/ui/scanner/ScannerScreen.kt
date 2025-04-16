package com.example.yugiohcardscanner.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.launch
import java.util.regex.Pattern

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
                onSetCodeDetected = { setCode ->
                    scope.launch {
                        viewModel.searchCardBySetCode(setCode)
                    }
                },
                enabled = scanningState !is ScanningState.Success,
                onImageCapture = { imageProxy ->
                    // Handle image capture
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                // Scanning frame background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                // States container (rest of the content remains the same)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (scanningState) {
                        is ScanningState.Success -> {
                            CardPreview(card = scanningState.card) {
                                viewModel.resetScanningState()
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
                        else -> Unit
                    }

                }
            }

            // Bottom controls -
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
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = "Gallery",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Capture button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape)
                        .clickable(enabled = scanningState !is ScanningState.Success) {
                            viewModel.captureImage()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                // Review button
                IconButton(
                    onClick = {
                        val testImage = BitmapFactory.decodeResource(context.resources, R.drawable.card)


                        val inputImage = InputImage.fromBitmap(testImage, 0)

                        processImage(inputImage) { setCode ->
                            Log.d("ManualTest", "Detected: $setCode")
                            scope.launch {
                                viewModel.searchCardBySetCode(setCode)
                            }
                        }
//                        if (viewModel.scannedCards.isNotEmpty()) {
//                            onNavigateToReview()
//                        }
                    },
//                    enabled = viewModel.scannedCards.isNotEmpty(),
                    enabled = true,
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


@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = Color.White
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White
                )
            }
        }
    }
}

private fun processImage(image: InputImage, onSetCodeFound: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    recognizer.process(image)
        .addOnSuccessListener { text ->
            Log.d("ManualTest", "Full Text:\n${text.text}")

            val setCodePattern = Pattern.compile("[A-Z0-9]+-[A-Z0-9]+")

            outer@ for (block in text.textBlocks) {
                for (line in block.lines) {
                    val matcher = setCodePattern.matcher(line.text)
                    if (matcher.find()) {
                        onSetCodeFound(matcher.group())
                        break@outer
                    }
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("TextRecognition", "Text recognition failed", e)
        }
}