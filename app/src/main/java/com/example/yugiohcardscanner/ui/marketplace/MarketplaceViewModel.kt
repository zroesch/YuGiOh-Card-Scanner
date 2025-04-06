package com.example.yugiohcardscanner.ui.marketplace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import com.example.yugiohcardscanner.repository.CardCacheRepository
import com.example.yugiohcardscanner.repository.CardRepository
import com.example.yugiohcardscanner.repository.FirebaseCardRepository
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.ktx.firestore

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

    private fun loadCards() {
        viewModelScope.launch {
            // 1. Try loading from cache first
            val cachedCards = cardCacheRepository.getCachedCards()

            if (cachedCards.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        allCards = cachedCards,
                        filteredCards = applySortAndFilter(cachedCards, it.searchQuery, it.selectedSort)
                    )
                }
            } else {
                // 2. Fallback: Load from Firestore & cache
                val remoteCards = firebaseCardRepository.preloadAllCardsFromFirestore()
                cardCacheRepository.cacheCards(remoteCards)

                _uiState.update {
                    it.copy(
                        allCards = remoteCards,
                        filteredCards = applySortAndFilter(remoteCards, it.searchQuery, it.selectedSort)
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredCards = applySortAndFilter(it.allCards, query, it.selectedSort)
            )
        }
    }

    fun clearSearchQuery() {
        updateSearchQuery("")
    }

    fun updateSort(sortType: SortType) {
        _uiState.update {
            it.copy(
                selectedSort = sortType,
                filteredCards = applySortAndFilter(it.allCards, it.searchQuery, sortType)
            )
        }
    }

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
