package com.example.yugiohcardscanner.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Profile screen.
 *
 * This ViewModel manages the UI state and logic for the Profile screen,
 * such as loading the user's collection data. It uses [CollectionRepository]
 * to interact with the underlying data.
 *
 * @property collectionRepository The repository for accessing the user's collection data.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadCollectionData()
    }

    /**
     * Loads the user's collection data.
     *
     * This method retrieves the total value and the number of cards in the
     * user's collection from the [CollectionRepository] and updates the UI
     * state accordingly.
     */
    fun loadCollectionData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val totalValue = collectionRepository.getTotalCollectionValue()
            val numberOfCards = collectionRepository.getNumberOfCards()
            _uiState.update {
                it.copy(
                    totalCards = numberOfCards,
                    totalCollectionValue = totalValue,
                    isLoading = false
                )
            }
        }
    }
}