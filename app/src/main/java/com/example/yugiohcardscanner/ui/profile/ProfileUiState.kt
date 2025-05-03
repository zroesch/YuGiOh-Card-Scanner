package com.example.yugiohcardscanner.ui.profile

/**
 * Data class representing the UI state for the Profile screen.
 *
 * This class holds the necessary information to display the user's
 * profile data, including the total number of cards, the total collection
 * value, and the loading state.
 *
 * @property totalCards The total number of cards in the user's collection.
 * @property totalCollectionValue The total market value of the user's collection.
 * @property isLoading Indicates whether the profile data is currently being loaded.
 */
data class ProfileUiState(
    val totalCards: Int = 0,
    val totalCollectionValue: Double = 0.0,
    val isLoading: Boolean = false
)