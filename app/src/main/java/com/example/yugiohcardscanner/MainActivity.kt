package com.example.yugiohcardscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.yugiohcardscanner.ui.navigation.AppNavGraph
import com.example.yugiohcardscanner.ui.components.BottomNavBar
import com.example.yugiohcardscanner.ui.theme.YuGiOhCardScannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YuGiOhCardScannerTheme  {
                AppWithNavigation()
            }
        }
    }
}

@Composable
fun AppWithNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        AppNavGraph(navController = navController)
    }
}
