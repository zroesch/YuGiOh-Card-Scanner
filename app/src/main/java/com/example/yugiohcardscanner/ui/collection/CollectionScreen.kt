package com.example.yugiohcardscanner.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.yugiohcardscanner.ui.components.BottomNavBar
import com.example.yugiohcardscanner.ui.collection.components.CollectionSearchBar
import com.example.yugiohcardscanner.ui.collection.components.CollectionSortingBottomSheet
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

/**
 * Composable function for the main Collection screen.
 *
 * This screen displays the user's card collection, allowing them to search, sort, and
 * manage their cards. It uses a [Scaffold] for the basic screen layout, a
 * [CollectionSearchBar] for searching and sorting, a [CollectionSortingBottomSheet] for
 * selecting the sort order, and [CollectionContent] to display the cards.
 *
 * @param navController The [NavController] for navigation within the app.
 * @param viewModel The [SharedCardViewModel] for managing the collection data.
 */
@Composable
fun CollectionScreen(
    navController: NavController,
    viewModel: SharedCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isSortSheetVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp)
            ) {
                CollectionSearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = { viewModel.updateSearchQuery(it) },
                    onClearQuery = { viewModel.clearSearchQuery() },
                    onSortClick = { isSortSheetVisible = true }
                )
            }
        },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            CollectionContent(
                modifier = Modifier.padding(paddingValues),
                cards = uiState.filteredCollection,
                onRemoveFromCollection = viewModel::removeCardFromCollection
            )
        }
    }

    if (isSortSheetVisible) {
        CollectionSortingBottomSheet(
            currentSort = uiState.selectedSort,
            onSortSelected = { viewModel.updateSort(it) },
            onDismiss = { isSortSheetVisible = false }
        )
    }
}