package com.example.yugiohcardscanner.ui.marketplace

import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType

data class MarketplaceUiState(
    val allCards: List<CardData> = emptyList(),
    val filteredCards: List<CardData> = emptyList(),
    val searchQuery: String = "",
    val selectedSort: SortType = SortType.NONE,
    val isLoading: Boolean = false,
    val hasMore: Boolean = true
)



