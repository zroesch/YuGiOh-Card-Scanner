package com.example.yugiohcardscanner.ui.scanner.components

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors
import java.util.regex.Pattern

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    onSetCodeDetected: (String) -> Unit,
    enabled: Boolean = true,
    onImageCapture: (ImageProxy) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val isProcessing = remember { mutableStateOf(false) }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    ) {
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            if (isProcessing.value) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                isProcessing.value = true
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                                    .process(image)
                                    .addOnSuccessListener { visionText ->
                                        Log.d("MLKit", "Full Text: ${visionText.text}")
                                        val setCodePattern = Pattern.compile("[A-Z0-9]+-[A-Z0-9]+")
                                        visionText.textBlocks.forEach { block ->
                                            block.lines.forEach { line ->
                                                val matcher = setCodePattern.matcher(line.text.uppercase())
                                                if (matcher.find()) {
                                                    onSetCodeDetected(matcher.group())
                                                    return@forEach
                                                }
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("CameraPreview", "MLKit failed", e)
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()  // ✅ close only when done!
                                        isProcessing.value = false
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }

                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", e)
                }
            } catch (e: Exception) {
                Log.e("CameraPreview", "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

private fun processImage(image: InputImage, onSetCodeFound: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    recognizer.process(image)
        .addOnSuccessListener { text ->
            val setCodePattern = Pattern.compile("[A-Z0-9]+-[A-Z0-9]+")

            text.textBlocks.forEach { block ->
                block.lines.forEach { line ->
                    val matcher = setCodePattern.matcher(line.text)
                    if (matcher.find()) {
                        onSetCodeFound(matcher.group())
                        return@forEach
                    }
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("TextRecognition", "Text recognition failed", e)
        }
}