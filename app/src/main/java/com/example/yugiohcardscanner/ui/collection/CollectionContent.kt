package com.example.yugiohcardscanner.ui.collection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.ui.collection.components.CardItem

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
            .padding(horizontal = 4.dp, vertical = 8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(cards) { card ->
            CardItem(
                card = card,
                onRemoveFromCollection = onRemoveFromCollection,
                modifier = Modifier
                    .padding(4.dp)
            )
        }
    }
}

