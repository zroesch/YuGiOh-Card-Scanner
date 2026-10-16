package com.example.yugiohcardscanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import com.example.yugiohcardscanner.ui.collection.CollectionScreen
import com.example.yugiohcardscanner.ui.home.HomeScreen
import com.example.yugiohcardscanner.ui.marketplace.MarketplaceScreen
import com.example.yugiohcardscanner.ui.scanner.ScannerScreen
import com.example.yugiohcardscanner.ui.scanner.ScannerViewModel
import com.example.yugiohcardscanner.ui.scanner.components.ReviewScreen
import kotlinx.serialization.Serializable

// Define Type-Safe Routes
@Serializable data object HomeRoute
@Serializable data object CollectionRoute
@Serializable data object MarketplaceRoute
@Serializable data object ScannerRoute
@Serializable data object ScanReviewRoute

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            HomeScreen()
        }

        composable<CollectionRoute> {
            CollectionScreen(
                navController = navController
            )
        }

        composable<MarketplaceRoute> {
            MarketplaceScreen(
                navController = navController
            )
        }

        composable<ScannerRoute> {
            val scannerViewModel: ScannerViewModel = hiltViewModel()
            ScannerScreen(
                viewModel = scannerViewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable<ScanReviewRoute> {
            val scannerViewModel: ScannerViewModel = hiltViewModel()
            ReviewScreen(
                viewModel = scannerViewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController,
                onNavigateToCollection = {
                    navController.navigate(CollectionRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}