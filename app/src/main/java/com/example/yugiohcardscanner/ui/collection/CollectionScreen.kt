package com.example.yugiohcardscanner.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.yugiohcardscanner.ui.collection.components.CollectionSearchBar
import com.example.yugiohcardscanner.ui.collection.components.CollectionSortingBottomSheet
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

/**
 * Displays the user's card collection with search and sort functionality.
 */
@Composable
fun CollectionScreen(
    navController: NavController, // Retained for potential future navigation from this screen
    viewModel: SharedCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
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
            CollectionSearchBar(
                query = uiState.searchQuery,
                onQueryChanged = { viewModel.updateSearchQuery(it) },
                onClearQuery = { viewModel.clearSearchQuery() },
                onSortClick = { isSortSheetVisible = true }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            CollectionContent(
                modifier = Modifier.weight(1f).fillMaxWidth(),
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

@Preview(showBackground = true)
@Composable
fun CollectionScreenPreview() {
    val fakeCollectionRepo = FakeCollectionRepository() // Assuming FakeCollectionRepository is defined elsewhere
    val sharedCardViewModel = SharedCardViewModel(fakeCollectionRepo)
    CollectionScreen(
        navController = rememberNavController(),
        viewModel = sharedCardViewModel
    )
}

// Define a FakeCollectionRepository for the preview if it doesn't exist.
// This is just a placeholder, you'll need to implement it according to your needs.
// class FakeCollectionRepository : CollectionRepository { /* ... */ }
