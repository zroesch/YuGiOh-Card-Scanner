package com.example.yugiohcardscanner.ui.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.yugiohcardscanner.ui.components.BottomNavBar
import com.example.yugiohcardscanner.ui.collection.components.CollectionSearchBar
import com.example.yugiohcardscanner.ui.collection.CollectionContent
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

@Composable
fun CollectionScreen(
    navController: NavController,
    viewModel: SharedCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(top = 8.dp)
            ) {
                CollectionSearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = { viewModel.updateSearchQuery(it) },
                    onClearQuery = { viewModel.clearSearchQuery() }
                )
            }
        },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CollectionContent(
            modifier = Modifier.padding(paddingValues),
            cards = uiState.collection,
            onRemoveFromCollection = viewModel::removeCardFromCollection
        )
    }
}