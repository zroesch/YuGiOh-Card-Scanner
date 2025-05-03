package com.example.yugiohcardscanner.ui.collection

import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType

/**
 * Data class to hold the UI state for the collection screen.
 *
 * This class represents the current state of the collection, including:
 * - The full collection of cards.
 * - The filtered collection based on the search query.
 * - The current search query string.
 * - The selected sort option.
 *
 * @property collection The complete list of [CardData] in the user's collection.
 * @property filteredCollection The list of [CardData] that match the current search query.
 * @property searchQuery The current search query string entered by the user.
 * @property selectedSort The currently selected [SortType] for sorting the collection.
 */
data class CollectionUiState(
    val collection: List<CardData> = emptyList(),
    val filteredCollection: List<CardData> = emptyList(),
    val searchQuery: String = "",
    val selectedSort: SortType = SortType.NONE
)