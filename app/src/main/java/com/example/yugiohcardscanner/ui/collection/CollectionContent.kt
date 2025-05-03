package com.example.yugiohcardscanner.ui.collection

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
import com.example.yugiohcardscanner.ui.collection.components.CardItem

/**
 * Composable function to display the main content of the collection screen.
 *
 * This composable uses a `LazyVerticalGrid` to display a list of [CardData] items.
 *
 * @param modifier Modifier to be applied to the collection content.
 * @param cards The list of [CardData] objects to display.
 * @param onRemoveFromCollection Callback function for when a card is to be removed
 *                               from the collection. It takes the [CardData] object as input.
 */
@Composable
fun CollectionContent(
    modifier: Modifier = Modifier,
    cards: List<CardData>,
    onRemoveFromCollection: (CardData) -> Unit
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
                onRemoveFromCollection = onRemoveFromCollection,
                modifier = Modifier
                    .padding(2.dp)
            )
        }
    }
}