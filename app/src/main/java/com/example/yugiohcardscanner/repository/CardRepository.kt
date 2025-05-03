package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

/**
 * Interface for managing card data.
 *
 * This interface defines the methods for interacting with a card data source,
 * such as loading cards from Firestore or retrieving cached cards.
 * Concrete implementations of this interface handle the specifics of where
 * and how card data is stored and retrieved.
 */
interface CardRepository {
    /**
     * Loads all cards from the data source (e.g., Firestore).
     *
     * @return A list of [CardData] objects representing all cards.
     */
    suspend fun preloadAllCardsFromFirestore(): List<CardData>

    /**
     * Finds a card by its set code.
     *
     * @param setCode The set code of the card to find.
     * @return The [CardData] object if found, null otherwise.
     */
    suspend fun findCardBySetCode(setCode: String): CardData?

    /**
     * Retrieves cached cards.
     *
     * @return A list of [CardData] objects representing the cached cards.
     */
    suspend fun getCachedCards(): List<CardData>

    /**
     * Clears the cached cards.
     */
    suspend fun clearCachedCards()
}