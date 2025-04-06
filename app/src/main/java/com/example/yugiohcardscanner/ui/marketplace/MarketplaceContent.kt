package com.example.yugiohcardscanner.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.ui.marketplace.components.CardItem

@Composable
fun MarketplaceContent(
    modifier: Modifier = Modifier,
    cards: List<CardData>,
    onAddToCollection: (CardData) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(cards) { card ->
            CardItem(
                card = card,
                onAddToCollection = onAddToCollection,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}