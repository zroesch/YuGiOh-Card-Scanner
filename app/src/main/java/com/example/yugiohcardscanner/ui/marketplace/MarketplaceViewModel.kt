package com.example.yugiohcardscanner.ui.marketplace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import com.example.yugiohcardscanner.repository.CardCacheOperations
import com.example.yugiohcardscanner.repository.CardRepository
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for the Marketplace screen.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val cardRepository: CardRepository, // Hilt will provide CsvCardRepository here
    private val cardCacheOperations: CardCacheOperations,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        loadCards()
        
        // Debounce search query to prevent UI lag when typing
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300) // Wait 300ms after user stops typing
                .collect { query ->
                    val currentState = _uiState.value
                    val filtered = applySortAndFilter(currentState.allCards, query, currentState.selectedSort)
                    _uiState.update { it.copy(filteredCards = filtered) }
                }
        }
    }

    private fun loadCards() {
        viewModelScope.launch {
            Log.d("MarketplaceViewModel", "loadCards called")
            _uiState.update { it.copy(isLoading = true) }

            var allCards = withContext(Dispatchers.IO) { cardCacheOperations.getCachedCards() }
            Log.d("MarketplaceViewModel", "Cached cards count: ${allCards.size}")

            if (allCards.isEmpty()) {
                Log.d("MarketplaceViewModel", "Cache empty. Loading from remote CSV data source.")
                allCards = cardRepository.preloadAllCardsFromDataSource()
                Log.d("MarketplaceViewModel", "Remote cards loaded from CSV: ${allCards.size}")

                if (allCards.isNotEmpty()) {
                    // This runs on IO thread via Room's suspend functions
                    cardCacheOperations.cacheCards(allCards)
                    Log.d("MarketplaceViewModel", "Remote cards cached.")
                }
            }

            val filtered = applySortAndFilter(allCards, _uiState.value.searchQuery, _uiState.value.selectedSort)
            _uiState.update {
                it.copy(
                    allCards = allCards,
                    filteredCards = filtered,
                    isLoading = false
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        val currentState = _uiState.value
        if (currentState.searchQuery == query) return
        
        // Update query immediately so UI reflects input, but keep old filtered list while processing
        _uiState.update { it.copy(searchQuery = query) }
        
        // Pass it to the flow for the 300ms delayed filtering
        _searchQueryFlow.value = query
    }

    fun clearSearchQuery() {
        updateSearchQuery("")
    }

    fun updateSort(sortType: SortType) {
        val currentState = _uiState.value
        if (currentState.selectedSort == sortType) return

        _uiState.update { it.copy(selectedSort = sortType) }

        viewModelScope.launch {
            val filtered = applySortAndFilter(currentState.allCards, currentState.searchQuery, sortType)
            _uiState.update { it.copy(filteredCards = filtered) }
        }
    }

    fun addToCollection(card: CardData) {
        viewModelScope.launch {
            collectionRepository.addCardToCollection(card)
        }
    }

    private suspend fun applySortAndFilter(
        cards: List<CardData>,
        query: String,
        sortType: SortType
    ): List<CardData> = withContext(Dispatchers.Default) {
        val filtered = if (query.isBlank()) {
            cards
        } else {
            cards.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.setName.contains(query, ignoreCase = true) ||
                        it.extNumber.contains(query, ignoreCase = true)
            }
        }
        filtered.sortedWith(sortType.getComparator())
    }
}