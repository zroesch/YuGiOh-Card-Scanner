package com.example.yugiohcardscanner.ui.marketplace.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yugiohcardscanner.data.models.*

@Composable
fun CardGrid(cards: List<CardData>, onAddToCollection: (CardData) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(cards) { card ->
            CardItem(card = card, onAddToCollection = onAddToCollection)
        }
    }
}

