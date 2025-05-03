package com.example.yugiohcardscanner.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import com.example.yugiohcardscanner.ui.collection.CollectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the card collection and related UI states.
 *
 * This ViewModel handles loading, filtering, sorting, adding, and removing cards from the user's collection.
 * It provides UI states for the collection screen.
 *
 * @param collectionRepository The repository for accessing and managing the user's card collection.
 */
@HiltViewModel
class SharedCardViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    // State to hold the current UI state of the collection screen
    private val _uiState = MutableStateFlow(CollectionUiState())

    /** State flow to reflect the current state of the collection UI. */
    val uiState: StateFlow<CollectionUiState> = _uiState

    // State to hold if the collection is loading.
    private val _isLoading = MutableStateFlow(false)

    /** State flow to reflect the loading state of the collection. */
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCollection()
    }

    /** Loads the card collection from the repository and updates the UI state. */
    private fun loadCollection() {
        viewModelScope.launch {
            _isLoading.value = true
            val allCards = collectionRepository.getAllCardsFromCollection()
            _uiState.update {
                it.copy(
                    collection = allCards,
                    filteredCollection = applySortAndFilter(allCards, "", it.selectedSort)
                )
            }
            _isLoading.value = false
        }
    }

    /**
     * Updates the search query and filters the collection accordingly.
     *
     * @param query The new search query.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredCollection = applySortAndFilter(it.collection, query, it.selectedSort)
            )
        }
    }

    /** Clears the search query and resets the collection filter. */
    fun clearSearchQuery() {
        updateSearchQuery("")
    }

    /**
     * Updates the sorting method and applies it to the collection.
     *
     * @param sortType The new sorting method.
     */
    fun updateSort(sortType: SortType) {
        _uiState.update {
            it.copy(
                selectedSort = sortType,
                filteredCollection = applySortAndFilter(it.collection, it.searchQuery, sortType)
            )
        }
    }

    /**
     * Applies sorting and filtering to a list of cards.
     *
     * @param cards The list of cards to process.
     * @param query The search query to filter by.
     * @param sortType The sorting method to apply.
     * @return The sorted and filtered list of cards.
     */
    private fun applySortAndFilter(
        cards: List<CardData>,
        query: String,
        sortType: SortType
    ): List<CardData> {
        return cards
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedWith(sortType.getComparator())
    }

    /**
     * Removes a card from the collection.
     *
     * @param card The card to remove.
     */
    fun removeCardFromCollection(card: CardData) {
        viewModelScope.launch {
            collectionRepository.removeCardFromCollection(card)
            updateUiStateAfterCollectionChange()
        }
    }

    /**
     * Adds a card to the collection and refreshes the collection.
     *
     * @param card The card to add.
     */
    fun addToCollection(card: CardData) {
        viewModelScope.launch {
            collectionRepository.addCardToCollection(card)
            updateUiStateAfterCollectionChange()
        }
    }

    /**
     * Adds a list of cards to the collection and refreshes it.
     *
     * @param cards A list of cards to add.
     */
    fun addCardsToCollection(cards: List<CardData>) {
        viewModelScope.launch {
            _isLoading.value = true
            cards.forEach { card ->
                collectionRepository.addCardToCollection(card)
            }
            updateUiStateAfterCollectionChange()
            _isLoading.value = false
        }
    }

    /**
     * Updates the collection ui state.
     */
    private suspend fun updateUiStateAfterCollectionChange() {
        val allCards = collectionRepository.getAllCardsFromCollection()
        _uiState.update {
            it.copy(
                collection = allCards,
                filteredCollection = applySortAndFilter(allCards, it.searchQuery, it.selectedSort)
            )
        }
    }
}