package com.example.yugiohcardscanner.ui.collection

import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType

// ✅ Create this new UI state class
data class CollectionUiState(
    val collection: List<CardData> = emptyList(),
    val searchQuery: String = "",
    val selectedSort: SortType = SortType.NONE
)

