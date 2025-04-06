package com.example.yugiohcardscanner.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState

    init {
        loadCollection() // ✅ Load collection from Room
    }

    // ✅ Load collection from Room database
    fun loadCollection() {
        viewModelScope.launch {
            val savedCollection = repository.getAllCardsFromCollection()
            _uiState.value = _uiState.value.copy(collection = savedCollection)
        }
    }

    // ✅ Add card to collection with count
    fun addCardToCollection(card: CardData) {
        viewModelScope.launch {
            repository.addCardToCollection(card)
            loadCollection() // ✅ Reload collection after adding
        }
    }

    // ✅ Remove card from collection
    fun removeCardFromCollection(card: CardData) {
        viewModelScope.launch {
            repository.removeCardFromCollection(card)
            loadCollection() // ✅ Reload collection after removing
        }
    }

    // ✅ Clear entire user collection
    fun clearUserCollection() {
        viewModelScope.launch {
            repository.clearUserCollection()
            loadCollection() // ✅ Clear collection and reload
        }
    }

    // ✅ Update search query for filtering
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}
