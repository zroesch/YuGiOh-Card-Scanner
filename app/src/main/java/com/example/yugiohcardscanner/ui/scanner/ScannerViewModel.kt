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
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the scanner feature, responsible for handling card scanning logic.
 *
 * This ViewModel manages the state of the scanner, including scanning status, scanned cards,
 * and interactions with the card cache and user collection.
 *
 * @param cardCacheRepository Repository for accessing and managing cached card data.
 * @param collectionRepository Repository for accessing and managing user's collection data.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val cardCacheRepository: CardCacheRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    // State to hold the current scanning state
    private var _scanningState = mutableStateOf<ScanningState>(ScanningState.Idle)

    /** State flow to reflect the current scanning state. */
    val scanningState: State<ScanningState> = _scanningState

    // List to hold scanned cards
    private val _scannedCards = mutableStateListOf<CardData>()

    /** List of currently scanned cards. */
    val scannedCards: State<List<CardData>> = mutableStateOf(_scannedCards.toList())

    // Set to track already scanned card IDs
    private val scannedCardIds = mutableSetOf<String>()

    /**
     * Searches for a card by its set code.
     *
     * This function attempts to find a card based on the provided set code. If the card is found,
     * it's added to the list of scanned cards. If not found or an error occurs, the scanning state
     * is updated accordingly.
     *
     * @param setCode The set code to search for.
     */
    suspend fun searchCardBySetCode(setCode: String) {
        if (_scanningState.value is ScanningState.Scanning) return

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
                _scanningState.value = ScanningState.Idle
            } else {
                _scanningState.value = ScanningState.Error("Card not found")
            }
        } catch (e: Exception) {
            _scanningState.value = ScanningState.Error(e.message ?: "Unknown error")
        }
    }

    /** Resets the scanning state to idle. */
    fun resetScanningState() {
        _scanningState.value = ScanningState.Idle
    }

    /** Clears the list of scanned cards. */
    fun clearScannedCards() {
        _scannedCards.clear()
        scannedCardIds.clear()
        (scannedCards as MutableState<List<CardData>>).value = _scannedCards.toList()
    }

    /** Moves the scanning state to ready for review. */
    fun moveToReview() {
        _scanningState.value = ScanningState.ReadyForReview
    }

    // State to hold if the image is capturing.
    private val _isCapturing = mutableStateOf(false)
    private val isCapturing = _isCapturing

    /** Sets the is capturing state to true and sets the scanning state to scanning. */
    fun captureImage() {
        if (!isCapturing.value) {
            _isCapturing.value = true
            _scanningState.value = ScanningState.Scanning
        }
    }

    /**
     * Callback function invoked when an image has been captured.
     *
     * @param setCode The set code extracted from the image.
     */
    fun onImageCaptured(setCode: String) {
        _isCapturing.value = false
        viewModelScope.launch {
            searchCardBySetCode(setCode)
        }
    }
    /**
     * Sets the scanning state to an error state with the given message.
     *
     * @param message The error message to display.
     */
    fun setScanningStateError(message: String) {
        _scanningState.value = ScanningState.Error(message)
    }

    /**
     * Adds a scanned card to the user's collection.
     *
     * @param card The card to add to the collection.
     */
    suspend fun addCardToCollection(card: CardData) {
        collectionRepository.addCardToCollection(card)
    }
}

/**
 * Sealed class to represent the various states of the scanning process.
 */
sealed class ScanningState {
    /** Represents the idle state of the scanner. */
    object Idle : ScanningState()

    /** Represents the state when the scanner is actively scanning. */
    object Scanning : ScanningState()

    /** Represents the state when the scanned items are ready for review. */
    object ReadyForReview : ScanningState()

    /** Represents an error state, providing an error message. */
    data class Error(val message: String) : ScanningState()
}