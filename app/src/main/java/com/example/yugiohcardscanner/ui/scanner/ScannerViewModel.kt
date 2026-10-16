package com.example.yugiohcardscanner.ui.scanner

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.repository.CardCacheRepository
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the scanner feature, responsible for handling card scanning logic.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val cardCacheRepository: CardCacheRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private var _scanningState = mutableStateOf<ScanningState>(ScanningState.Idle)
    val scanningState: State<ScanningState> = _scanningState

    private val _scannedCards = mutableStateListOf<CardData>()
    val scannedCards: State<List<CardData>> = mutableStateOf(_scannedCards.toList())

    private val scannedCardIds = mutableSetOf<String>()

    private var isProcessingFrame = false

    suspend fun searchCardBySetCode(setCode: String) {
        if (isProcessingFrame || _scanningState.value is ScanningState.ReadyForReview) return

        isProcessingFrame = true
        _scanningState.value = ScanningState.Scanning
        try {
            val card = cardCacheRepository.findCardBySetCode(setCode)
            if (card != null) {
                Log.d("ScannerViewModel", "Found card: ${card.name}")
                if (!scannedCardIds.contains(card.productId)) {
                    _scannedCards.add(card)
                    scannedCardIds.add(card.productId)
                    (scannedCards as MutableState<List<CardData>>).value = _scannedCards.toList()
                }
                delay(700) // Brief pause to show the user a success state
                _scanningState.value = ScanningState.Idle
            } else {
                _scanningState.value = ScanningState.Error("Card not found")
                // Reset state automatically after a delay so user isn't stuck
                viewModelScope.launch {
                    delay(3000)
                    if (_scanningState.value is ScanningState.Error) {
                        _scanningState.value = ScanningState.Idle
                    }
                }
            }
        } catch (e: Exception) {
            _scanningState.value = ScanningState.Error(e.message ?: "Unknown error")
            viewModelScope.launch {
                delay(3000)
                if (_scanningState.value is ScanningState.Error) {
                    _scanningState.value = ScanningState.Idle
                }
            }
        } finally {
            isProcessingFrame = false
        }
    }

    fun resetScanningState() {
        _scanningState.value = ScanningState.Idle
    }

    fun clearScannedCards() {
        _scannedCards.clear()
        scannedCardIds.clear()
        (scannedCards as MutableState<List<CardData>>).value = _scannedCards.toList()
    }

    fun moveToReview() {
        _scanningState.value = ScanningState.ReadyForReview
    }

    private val _isCapturing = mutableStateOf(false)
    private val isCapturing = _isCapturing

    fun captureImage() {
        if (!isCapturing.value) {
            _isCapturing.value = true
            _scanningState.value = ScanningState.Scanning
        }
    }

    fun onImageCaptured(setCode: String) {
        _isCapturing.value = false
        viewModelScope.launch {
            searchCardBySetCode(setCode)
        }
    }

    fun setScanningStateError(message: String) {
        _scanningState.value = ScanningState.Error(message)
        isProcessingFrame = false
    }

    suspend fun addCardToCollection(card: CardData) {
        collectionRepository.addCardToCollection(card)
    }

    suspend fun addCardsToCollection(cards: List<CardData>) {
        cards.forEach { card ->
            collectionRepository.addCardToCollection(card)
        }
    }
}

sealed class ScanningState {
    object Idle : ScanningState()
    object Scanning : ScanningState()
    object ReadyForReview : ScanningState()
    data class Error(val message: String) : ScanningState()
}
