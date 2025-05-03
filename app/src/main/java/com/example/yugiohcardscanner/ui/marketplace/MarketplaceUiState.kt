package com.example.yugiohcardscanner.ui.marketplace

import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType

/**
 * Data class representing the UI state for the Marketplace screen.
 *
 * This class holds the state of the Marketplace UI, including the list of
 * all cards, the filtered list of cards, the current search query, the selected
 * sorting method, and whether there is more data to be loaded.
 *
 * @property allCards All the cards available in the marketplace.
 * @property filteredCards The list of cards filtered based on the search query
 *   and sorting method.
 * @property searchQuery The current search query entered by the user.
 * @property selectedSort The currently selected sorting method.
 * @property isLoading Indicates whether data is currently being loaded.
 * @property hasMore Indicates whether there is more data to load.
 */
data class MarketplaceUiState(
    val allCards: List<CardData> = emptyList(),
    val filteredCards: List<CardData> = emptyList(),
    val searchQuery: String = "",
    val selectedSort: SortType = SortType.NONE,
    val isLoading: Boolean = false,
    val hasMore: Boolean = true
)