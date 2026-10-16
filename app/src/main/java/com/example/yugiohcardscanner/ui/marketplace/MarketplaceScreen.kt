// MarketplaceScreen.kt
package com.example.yugiohcardscanner.ui.marketplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.compose.rememberNavController
import com.example.yugiohcardscanner.ui.marketplace.components.SearchBar
import com.example.yugiohcardscanner.ui.marketplace.components.SortingBottomSheet

/**
 * Displays the marketplace screen, allowing users to search for cards,
 * sort them, and add them to their collection.
 */
import com.example.yugiohcardscanner.ui.navigation.HomeRoute
import com.example.yugiohcardscanner.ui.navigation.ScannerRoute

@Composable
fun MarketplaceScreen(
    navController: NavController,
    marketplaceViewModel: MarketplaceViewModel = hiltViewModel()
) {
    val uiState by marketplaceViewModel.uiState.collectAsState()
    var isSortSheetVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                onShowSetsClick = { navController.navigate(HomeRoute) },
                onCameraClick = { navController.navigate(ScannerRoute) }
            )
        }

        MarketplaceContent(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            cards = uiState.filteredCards,
            onAddToCollection = { card -> marketplaceViewModel.addToCollection(card) },
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

// @Preview(showBackground = true)
// @Composable
// fun MarketplaceScreenPreview() {
// }
