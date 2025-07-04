package com.example.yugiohcardscanner.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yugiohcardscanner.repository.FakeCardCacheRepository
import com.example.yugiohcardscanner.repository.FakeCardRepository
import com.example.yugiohcardscanner.ui.collection.FakeCollectionRepository
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
    marketplaceViewModel: MarketplaceViewModel = hiltViewModel(), // Default to Hilt for app
    sharedCardViewModel: SharedCardViewModel = hiltViewModel()  // Default to Hilt for app
) {
    val uiState by marketplaceViewModel.uiState.collectAsState()
    var isSortSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
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

@Preview(showBackground = true)
@Composable
fun MarketplaceScreenPreview() {
    val fakeRemoteRepo = FakeCardRepository()
    val fakeCacheRepo = FakeCardCacheRepository()
    val marketplaceViewModel = MarketplaceViewModel(fakeRemoteRepo, fakeCacheRepo)

    // The placeholderCards list for FakeCollectionRepository is defined in that file.
    val fakeCollectionRepo = FakeCollectionRepository()
    val sharedCardViewModel = SharedCardViewModel(fakeCollectionRepo)

    MarketplaceScreen(
        navController = rememberNavController(),
        marketplaceViewModel = marketplaceViewModel,
        sharedCardViewModel = sharedCardViewModel
    )
}