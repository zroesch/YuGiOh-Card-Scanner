package com.example.yugiohcardscanner.ui.scanner

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.repository.CardCacheRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val cardCacheRepository: CardCacheRepository
) : ViewModel() {

    var scanningState by mutableStateOf<ScanningState>(ScanningState.Idle)
        private set

    private val _scannedCards = mutableStateListOf<CardData>()
    val scannedCards: List<CardData> = _scannedCards

    suspend fun searchCardBySetCode(setCode: String) {
        if (scanningState is ScanningState.Scanning) return

        scanningState = ScanningState.Scanning
        try {
            val card = cardCacheRepository.findCardBySetCode(setCode)
            if (card != null) {
                Log.d("ScannerViewModel", "Found card: ${card.name}")
                _scannedCards.add(card)
                scanningState = ScanningState.Success(card)
            } else {
                scanningState = ScanningState.Error("Card not found")
            }
        } catch (e: Exception) {
            scanningState = ScanningState.Error(e.message ?: "Unknown error")
        }
    }

    fun resetScanningState() {
        scanningState = ScanningState.Idle
    }

    fun clearScannedCards() {
        _scannedCards.clear()
    }

    private val _isCapturing = mutableStateOf(false)
    private val isCapturing: Boolean by _isCapturing

    fun captureImage() {
        if (!isCapturing) {
            _isCapturing.value = true
            scanningState = ScanningState.Scanning
        }
    }

    fun onImageCaptured(setCode: String) {
        _isCapturing.value = false
        viewModelScope.launch {
            searchCardBySetCode(setCode)
        }
    }
}

sealed class ScanningState {
    object Idle : ScanningState()
    object Scanning : ScanningState()
    data class Success(val card: CardData) : ScanningState()
    data class Error(val message: String) : ScanningState()
}