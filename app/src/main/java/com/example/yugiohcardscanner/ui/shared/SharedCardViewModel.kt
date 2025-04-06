package com.example.yugiohcardscanner.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import com.example.yugiohcardscanner.ui.collection.CollectionUiState
import com.example.yugiohcardscanner.ui.marketplace.MarketplaceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedCardViewModel @Inject constructor(
    private val repository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState

    init {
        loadCollection()
    }

    fun addToCollection(card: CardData) {
        viewModelScope.launch {
            repository.addCardToCollection(card)
            loadCollection()
        }
    }

    fun removeCardFromCollection(card: CardData) {
        viewModelScope.launch {
            repository.removeCardFromCollection(card)
            loadCollection()
        }
    }

    private fun loadCollection() {
        viewModelScope.launch {
            val cards = repository.getAllCardsFromCollection()
            _uiState.update {
                it.copy(collection = cards)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearSearchQuery() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    fun updateSort(sortType: SortType) {
        _uiState.update { it.copy(selectedSort = sortType) }
    }
}

