package com.example.yugiohcardscanner.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CollectionRepository
) : ViewModel() {

    private val _searchQueryInput = MutableStateFlow("")
    private val _searchQuery = MutableStateFlow("")
    private val _selectedSort = MutableStateFlow(SortType.NONE)

    init {
        viewModelScope.launch {
            _searchQueryInput
                .debounce(300)
                .collect { query ->
                    _searchQuery.value = query
                }
        }
    }

    val uiState: StateFlow<CollectionUiState> = combine(
        repository.getAllCardsFlow(),
        _searchQuery,
        _searchQueryInput,
        _selectedSort
    ) { cards, debouncedQuery, inputQuery, sortType ->
        val filtered = if (debouncedQuery.isBlank()) {
            cards
        } else {
            cards.filter {
                it.name.contains(debouncedQuery, ignoreCase = true) ||
                        it.setName.contains(debouncedQuery, ignoreCase = true) ||
                        it.extNumber.contains(debouncedQuery, ignoreCase = true)
            }
        }
        val sorted = filtered.sortedWith(sortType.getComparator())
        CollectionUiState(
            collection = cards,
            filteredCollection = sorted,
            searchQuery = inputQuery,
            selectedSort = sortType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CollectionUiState()
    )

    fun addCardToCollection(card: CardData) {
        viewModelScope.launch {
            repository.addCardToCollection(card)
        }
    }

    fun removeCardFromCollection(card: CardData) {
        viewModelScope.launch {
            repository.removeCardFromCollection(card)
        }
    }

    fun clearUserCollection() {
        viewModelScope.launch {
            repository.clearUserCollection()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQueryInput.value = query
        // Also update uiState immediately with the query string so the text field doesn't lag
        // The _searchQuery state flow will handle the actual delayed filtering
    }
    
    fun clearSearchQuery() {
        _searchQueryInput.value = ""
        _searchQuery.value = ""
    }
    
    fun updateSort(sortType: SortType) {
        _selectedSort.value = sortType
    }
}
