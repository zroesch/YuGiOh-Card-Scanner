package com.example.yugiohcardscanner.repository

import android.util.Log
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.remote.TcgCsvDataSource
import javax.inject.Inject

/**
 * A concrete implementation of [CardRepository] that fetches card data
 * from a remote CSV data source via [TcgCsvDataSource].
 *
 * This repository is responsible for interacting with the CSV data source
 * to load all available cards. It does not handle caching directly;
 * caching is intended to be managed by a separate caching repository or a decorator.
 *
 * @property tcgCsvDataSource The data source for fetching card data from CSV files.
 */
class CsvCardRepository @Inject constructor(
    private val tcgCsvDataSource: TcgCsvDataSource
) : CardRepository {

    /**
     * Loads all cards from the primary data source (TCG CSV).
     *
     * Delegates the fetching and parsing of card data to the [tcgCsvDataSource].
     *
     * @return A list of [CardData] objects. Returns an empty list if an error occurs
     *         during fetching or parsing.
     */
    override suspend fun preloadAllCardsFromDataSource(): List<CardData> {
        Log.d("CsvCardRepository", "Starting preloadAllCardsFromDataSource (CSV)")
        return try {
            val cards = tcgCsvDataSource.fetchAllCards()
            Log.d("CsvCardRepository", "Total cards loaded from CSV: ${cards.size}")
            cards
        } catch (e: Exception) {
            Log.e("CsvCardRepository", "Error loading cards from CSV", e)
            emptyList()
        }
    }

    /**
     * Finds a card by its set code (extension number).
     *
     * **Note:** This implementation is inefficient if called frequently without caching,
     * as it requires fetching and parsing all CSV data each time. It is recommended
     * to rely on a caching layer (e.g., [CardCacheRepository]) for this functionality
     * in performance-sensitive scenarios.
     *
     * @param setCode The set code (e.g., "LOB-001") of the card to find.
     * @return The [CardData] object if found, or null otherwise.
     */
    override suspend fun findCardBySetCode(setCode: String): CardData? {
        Log.w("CsvCardRepository", "findCardBySetCode called directly on CsvCardRepository. This might be inefficient.")
        // This will fetch all cards every time it's called if there's no higher-level cache.
        val allCards = preloadAllCardsFromDataSource()
        return allCards.find { it.extNumber.equals(setCode, ignoreCase = true) } // Added ignoreCase for robustness
    }

    /**
     * Gets cached cards. This repository does not implement caching.
     *
     * This method is part of the [CardRepository] interface but is not implemented
     * by [CsvCardRepository] as it does not handle caching directly. Caching is
     * expected to be handled by another repository (e.g., [CardCacheRepository]).
     *
     * @return An empty list, as this repository does not cache cards.
     */
    override suspend fun getCachedCards(): List<CardData> {
        Log.d("CsvCardRepository", "getCachedCards called on CsvCardRepository. This repository does not cache. Returning empty list.")
        return emptyList() // This repository doesn't handle caching.
    }

    /**
     * Clears cached cards. This repository does not implement caching.
     *
     * This method is part of the [CardRepository] interface but is a no-op
     * in [CsvCardRepository] as it does not handle caching directly.
     */
    override suspend fun clearCachedCards() {
        Log.d("CsvCardRepository", "clearCachedCards called on CsvCardRepository. No-op as this repository does not cache.")
        // No-op, caching handled by another repository
    }
}