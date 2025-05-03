package com.example.yugiohcardscanner.ui.marketplace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import com.example.yugiohcardscanner.repository.CardCacheRepository
import com.example.yugiohcardscanner.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Marketplace screen.
 *
 * This ViewModel manages the UI state and logic for the Marketplace screen,
 * including loading cards, filtering cards based on search queries,
 * sorting cards based on user preferences, and updating the UI state accordingly.
 *
 * @property firebaseCardRepository The repository for interacting with card data (Firestore).
 * @property cardCacheRepository The repository for caching card data.
 */
@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val firebaseCardRepository: CardRepository,
    private val cardCacheRepository: CardCacheRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState

    init {
        loadCards()
    }

    /**
     * Loads the cards from the cache or Firestore.
     *
     * This method first checks if cards are available in the cache. If not,
     * it fetches them from Firestore, caches them, and then updates the UI state.
     */
    private fun loadCards() {
        viewModelScope.launch {
            Log.d("MarketplaceViewModel", "loadCards called")
            val cachedCards = cardCacheRepository.getCachedCards()
            Log.d("MarketplaceViewModel", "Cached cards: ${cachedCards.size}")

            if (cachedCards.isNotEmpty()) {
                _uiState.update {
                    Log.d("MarketplaceViewModel", "Using cached cards")
                    it.copy(
                        allCards = cachedCards,
                        filteredCards = applySortAndFilter(cachedCards, it.searchQuery, it.selectedSort)
                    )
                }
            } else {
                Log.d("MarketplaceViewModel", "Loading from Firestore")
                val remoteCards = firebaseCardRepository.preloadAllCardsFromFirestore()
                Log.d("MarketplaceViewModel", "Remote cards: ${remoteCards.size}")
                cardCacheRepository.cacheCards(remoteCards)

                _uiState.update {
                    Log.d("MarketplaceViewModel", "Updating UI with remote cards")
                    it.copy(
                        allCards = remoteCards,
                        filteredCards = applySortAndFilter(remoteCards, it.searchQuery, it.selectedSort)
                    )
                }
            }
        }
    }

    /**
     * Updates the search query and filters the card list accordingly.
     *
     * @param query The new search query string.
     */
    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredCards = applySortAndFilter(it.allCards, query, it.selectedSort)
            )
        }
    }

    /**
     * Clears the search query and resets the filtered card list.
     */
    fun clearSearchQuery() {
        updateSearchQuery("")
    }

    /**
     * Updates the sorting method and resorts the card list accordingly.
     *
     * @param sortType The new sorting method to apply.
     */
    fun updateSort(sortType: SortType) {
        _uiState.update {
            it.copy(
                selectedSort = sortType,
                filteredCards = applySortAndFilter(it.allCards, it.searchQuery, sortType)
            )
        }
    }

    /**
     * Applies the current sort and filter settings to the given card list.
     *
     * @param cards The list of cards to filter and sort.
     * @param query The current search query string.
     * @param sortType The current sorting method.
     * @return The filtered and sorted list of cards.
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
}