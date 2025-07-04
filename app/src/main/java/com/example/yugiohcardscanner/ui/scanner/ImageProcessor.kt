package com.example.yugiohcardscanner.ui.scanner

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.regex.Pattern

/**
 * Processes an image to detect and extract a Yu-Gi-Oh! set code.
 *
 * This function uses the ML Kit Text Recognition API to analyze an image
 * and search for a set code that matches a specific pattern.
 *
 * @param image The [InputImage] to be processed.
 * @param onSetCodeFound Callback function to be invoked with the found set code.
 */
fun processImage(image: InputImage, onSetCodeFound: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    recognizer.process(image)
        .addOnSuccessListener { text ->
            Log.d("ManualTest", "Full Text:\n${text.text}")

            // Flexible pattern for set codes
            val setCodePattern = Pattern.compile("(?i)[A-Z]{2,4}[\\s\\-]*[A-Z]{0,3}[0-9]{3}")
            var setCodeFound = false

            outer@ for (block in text.textBlocks) {
                Log.d("ScannerScreen", "Block Text: ${block.text}")
                for (line in block.lines) {
                    val matcher = setCodePattern.matcher(line.text)
                    if (matcher.find()) {
                        onSetCodeFound(matcher.group())
                        setCodeFound = true
                        break@outer
                    }
                }
            }
            if (!setCodeFound) {
                Log.d("ScannerScreen", "Set code not found")
            }
        }
        .addOnFailureListener { e ->
            Log.e("TextRecognition", "Text recognition failed", e)
        }
}