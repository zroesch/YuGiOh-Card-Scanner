package com.example.yugiohcardscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.yugiohcardscanner.ui.navigation.AppNavGraph
import com.example.yugiohcardscanner.ui.components.BottomNavBar
import com.example.yugiohcardscanner.ui.theme.YuGiOhCardScannerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * [MainActivity] is the main activity for the YuGiOh Card Scanner application.
 *
 * It serves as the entry point for the application's UI and sets up the main
 * composable content using Jetpack Compose. It also initializes the navigation
 * system and applies the application's theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created.
     *
     * Sets up the UI content using Jetpack Compose and initializes the application theme.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YuGiOhCardScannerTheme {
                AppWithNavigation()
            }
        }
    }
}

/**
 * [AppWithNavigation] is a composable function that sets up the main navigation
 * structure for the application.
 *
 * It uses `rememberNavController` to create and remember a navigation controller
 * and `currentBackStackEntryAsState` to observe the current navigation route.
 * It also determines whether to show the bottom navigation bar based on the
 * current route.
 */
@Composable
fun AppWithNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Define routes where the bottom bar should be hidden
    val hideBottomBarRoutes = listOf("scanner", "scan_review")
    // Determine whether to show the bottom bar
    val shouldShowBottomBar = currentRoute !in hideBottomBarRoutes

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(navController = navController)
        }
    }
}