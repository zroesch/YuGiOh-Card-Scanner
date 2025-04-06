package com.example.yugiohcardscanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yugiohcardscanner.ui.collection.CollectionScreen
import com.example.yugiohcardscanner.ui.marketplace.MarketplaceScreen
import com.example.yugiohcardscanner.ui.profile.ProfileScreen
import com.example.yugiohcardscanner.ui.scanner.ScannerScreen
import com.example.yugiohcardscanner.ui.home.HomeScreen
import com.example.yugiohcardscanner.ui.marketplace.MarketplaceViewModel
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

@Composable
fun AppNavGraph(navController: NavHostController) {
    val sharedCardViewModel: SharedCardViewModel = hiltViewModel()
    val marketplaceViewModel: MarketplaceViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen()
        }

        composable("collection") {
            CollectionScreen(
                navController = navController,
                viewModel = sharedCardViewModel
            )
        }

        composable("marketplace") {
            MarketplaceScreen(
                marketplaceViewModel = marketplaceViewModel,
                sharedCardViewModel = sharedCardViewModel,
                navController = navController
            )
        }

        composable("profile") {
            ProfileScreen()
        }

        composable("scanner") {
            ScannerScreen()
        }

        composable("sets") {
            // TODO: SetsScreen(navController)
        }
    }
}
