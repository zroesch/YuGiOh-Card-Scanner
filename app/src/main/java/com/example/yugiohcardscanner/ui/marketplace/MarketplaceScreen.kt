package com.example.yugiohcardscanner.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.SortType
import com.example.yugiohcardscanner.ui.components.BottomNavBar
import com.example.yugiohcardscanner.ui.marketplace.components.SearchBar
import com.example.yugiohcardscanner.ui.marketplace.components.SortingBottomSheet
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

/**
 * Composable function for the main Marketplace screen.
 *
 * This screen displays a list of available Yu-Gi-Oh! cards that can be
 * added to the user's collection. It uses a [Scaffold] for the basic
 * screen layout, a [SearchBar] for searching and filtering cards, and a
 * [SortingBottomSheet] for selecting the sort order. The card list is
 * displayed via the [MarketplaceContent] composable.
 *
 * @param navController The [NavController] for navigation within the app.
 * @param marketplaceViewModel The [MarketplaceViewModel] for managing the marketplace data.
 * @param sharedCardViewModel The [SharedCardViewModel] for managing the user's collection.
 */
@Composable
fun MarketplaceScreen(
    navController: NavController,
    marketplaceViewModel: MarketplaceViewModel = hiltViewModel(),
    sharedCardViewModel: SharedCardViewModel = hiltViewModel()
) {
    val uiState by marketplaceViewModel.uiState.collectAsState()
    var isSortSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
//                    .statusBarsPadding()
                    .padding(horizontal = 8.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = { marketplaceViewModel.updateSearchQuery(it) },
                    onClearQuery = { marketplaceViewModel.clearSearchQuery() },
                    onSortClick = { isSortSheetVisible = true },
                    onShowSetsClick = { navController.navigate("sets") },
                    onCameraClick = { navController.navigate("scanner") }
                )
            }
        }
    ) { paddingValues ->
        MarketplaceContent(
            modifier = Modifier.padding(paddingValues),
            cards = uiState.filteredCards,
            onAddToCollection = { card -> sharedCardViewModel.addToCollection(card) },
        )
    }

    if (isSortSheetVisible) {
        SortingBottomSheet(
            currentSort = uiState.selectedSort,
            onSortSelected = { marketplaceViewModel.updateSort(it) },
            onDismiss = { isSortSheetVisible = false }
        )
    }
}

val placeholderCards = listOf(
    CardData(
        productId = "68033",
        name = "Gem-Knight Sardonyx",
        cleanName = "Gem Knight Sardonyx",
        setName = "Hidden Arsenal 7: Knight of Stars",
        imageUrl = "https://tcgplayer-cdn.tcgplayer.com/product/68033_200w.jpg",
        extNumber = "HA07-EN001",
        extRarity = "Super Rare",
        groupId = 1110,
        categoryId = 2,
        marketPrice = 0.31,
        storageUrl = null
    ),
    CardData(
        productId = "68034",
        name = "Dark Magician",
        cleanName = "Dark Magician",
        setName = "Starter Deck: Yugi",
        imageUrl = "https://images.ygoprodeck.com/images/cards/46986414.jpg",
        extNumber = "SDY-006",
        extRarity = "Ultra Rare",
        groupId = 999,
        categoryId = 2,
        marketPrice = 99.99,
        storageUrl = null
    ),
    CardData(
        productId = "68033",
        name = "Gem-Knight Sardonyx",
        cleanName = "Gem Knight Sardonyx",
        setName = "Hidden Arsenal 7: Knight of Stars",
        imageUrl = "https://tcgplayer-cdn.tcgplayer.com/product/68033_200w.jpg",
        extNumber = "HA07-EN001",
        extRarity = "Super Rare",
        groupId = 1110,
        categoryId = 2,
        marketPrice = 0.31,
        storageUrl = null
    ),
    CardData(
        productId = "68034",
        name = "Dark Magician",
        cleanName = "Dark Magician",
        setName = "Starter Deck: Yugi",
        imageUrl = "https://images.ygoprodeck.com/images/cards/46986414.jpg",
        extNumber = "SDY-006",
        extRarity = "Ultra Rare",
        groupId = 999,
        categoryId = 2,
        marketPrice = 99.99,
        storageUrl = null
    ),
    CardData(
        productId = "68033",
        name = "Gem-Knight Sardonyx",
        cleanName = "Gem Knight Sardonyx",
        setName = "Hidden Arsenal 7: Knight of Stars",
        imageUrl = "https://tcgplayer-cdn.tcgplayer.com/product/68033_200w.jpg",
        extNumber = "HA07-EN001",
        extRarity = "Super Rare",
        groupId = 1110,
        categoryId = 2,
        marketPrice = 0.31,
        storageUrl = null
    ),
    CardData(
        productId = "68034",
        name = "Dark Magician",
        cleanName = "Dark Magician",
        setName = "Starter Deck: Yugi",
        imageUrl = "https://images.ygoprodeck.com/images/cards/46986414.jpg",
        extNumber = "SDY-006",
        extRarity = "Ultra Rare",
        groupId = 999,
        categoryId = 2,
        marketPrice = 99.99,
        storageUrl = null
    ),
    CardData(
        productId = "68033",
        name = "Gem-Knight Sardonyx",
        cleanName = "Gem Knight Sardonyx",
        imageUrl = "https://tcgplayer-cdn.tcgplayer.com/product/68033_200w.jpg",
        extNumber = "HA07-EN001",
        extRarity = "Super Rare",
        groupId = 1110,
        categoryId = 2,
        marketPrice = 0.31,
        storageUrl = null
    ),
    CardData(
        productId = "68034",
        name = "Dark Magician",
        cleanName = "Dark Magician",
        setName = "Starter Deck: Yugi",
        imageUrl = "https://images.ygoprodeck.com/images/cards/46986414.jpg",
        extNumber = "SDY-006",
        extRarity = "Ultra Rare",
        groupId = 999,
        categoryId = 2,
        marketPrice = 99.99,
        storageUrl = null
    )
)