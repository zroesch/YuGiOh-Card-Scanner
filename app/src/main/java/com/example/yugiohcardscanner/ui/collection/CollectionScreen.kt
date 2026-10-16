package com.example.yugiohcardscanner.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.navigation.compose.rememberNavController
import com.example.yugiohcardscanner.ui.collection.components.CollectionSearchBar
import com.example.yugiohcardscanner.ui.collection.components.CollectionSortingBottomSheet
import com.example.yugiohcardscanner.ui.navigation.ScannerRoute

/**
 * Displays the user's card collection with search and sort functionality.
 */
@Composable
fun CollectionScreen(
    navController: NavController, // Retained for potential future navigation from this screen
    viewModel: CollectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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

        if (uiState.collection.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = "Empty Collection",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your collection is empty.",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate(ScannerRoute) }) {
                        Text("Scan Your First Card")
                    }
                }
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

// @Preview(showBackground = true)
// @Composable
// fun CollectionScreenPreview() {
// }
