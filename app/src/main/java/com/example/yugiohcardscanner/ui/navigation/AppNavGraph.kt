package com.example.yugiohcardscanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yugiohcardscanner.ui.collection.CollectionScreen
import com.example.yugiohcardscanner.ui.home.HomeScreen
import com.example.yugiohcardscanner.ui.marketplace.MarketplaceScreen
import com.example.yugiohcardscanner.ui.marketplace.MarketplaceViewModel
//import com.example.yugiohcardscanner.ui.profile.ProfileScreen
import com.example.yugiohcardscanner.ui.scanner.ScannerScreen
import com.example.yugiohcardscanner.ui.scanner.ScannerViewModel
import com.example.yugiohcardscanner.ui.scanner.components.ReviewScreen
import com.example.yugiohcardscanner.ui.shared.SharedCardViewModel

/**
 * Composable function that defines the main navigation graph for the app.
 *
 * This function sets up the navigation structure using a [NavHost] and defines
 * the destinations available in the app, along with the composable content
 * for each destination.
 *
 * @param navController The [NavHostController] that manages the navigation state.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    // ViewModels that are shared across multiple screens
    val sharedCardViewModel: SharedCardViewModel = hiltViewModel()
    val marketplaceViewModel: MarketplaceViewModel = hiltViewModel()
    val scannerViewModel: ScannerViewModel = hiltViewModel()

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

//        composable("profile") {
//            ProfileScreen()
//        }

        composable("scanner") {
            ScannerScreen(
                viewModel = scannerViewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable("scan_review") {
            ReviewScreen(
                viewModel = scannerViewModel,
                sharedViewModel = sharedCardViewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController,
                onNavigateToCollection = {
                    navController.navigate("collection") {
                        popUpTo("home") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("card_detail") {
            // TODO: Implement CardDetailScreen
        }

        composable("sets") {
            // TODO: SetsScreen(navController)
        }
    }
}