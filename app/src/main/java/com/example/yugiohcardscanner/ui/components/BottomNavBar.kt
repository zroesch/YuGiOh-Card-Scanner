package com.example.yugiohcardscanner.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.yugiohcardscanner.ui.navigation.CollectionRoute
import com.example.yugiohcardscanner.ui.navigation.HomeRoute
import com.example.yugiohcardscanner.ui.navigation.MarketplaceRoute
import kotlin.reflect.KClass

/**
 * Composable function for the bottom navigation bar.
 *
 * This function creates a bottom navigation bar that allows the user to
 * navigate between different sections of the app: Home, Collection, Marketplace, and Profile.
 *
 * @param navController The [NavController] used for navigating between screens.
 * @param modifier Modifier to be applied to the NavigationBar.
 */
@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem(HomeRoute::class, "Home", Icons.Default.Home),
        BottomNavItem(CollectionRoute::class, "Collection", Icons.Default.Folder),
        BottomNavItem(MarketplaceRoute::class, "Marketplace", Icons.Default.ShoppingCart),
        // BottomNavItem(ProfileRoute::class, "Profile", Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier.height(72.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.route) } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        // In compose navigation we can use the object instance.
                        // However, we just have the class. 
                        // It's better to pass the object instance. Let's fix BottomNavItem.
                        navController.navigate(
                            when (item.route) {
                                HomeRoute::class -> HomeRoute
                                CollectionRoute::class -> CollectionRoute
                                MarketplaceRoute::class -> MarketplaceRoute
                                else -> HomeRoute
                            }
                        ) {
                            popUpTo(HomeRoute) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

/**
 * Data class representing an item in the bottom navigation bar.
 *
 * @property route The route to navigate to when this item is selected.
 * @property label The label to display for this item.
 * @property icon The icon to display for this item.
 */
data class BottomNavItem(val route: KClass<*>, val label: String, val icon: ImageVector)
