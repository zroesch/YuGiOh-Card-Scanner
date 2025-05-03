package com.example.yugiohcardscanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for interacting with the cache of all cards.
 *
 * This interface defines the methods to insert, query, and delete card data
 * from the cache.
 */
@Dao
interface AllCardDao {
    /**
     * Inserts multiple cards into the cache.
     *
     * If a card with the same primary key (productId) already exists, it will be
     * replaced.
     *
     * @param cards A list of [AllCardEntity] objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCards(cards: List<AllCardEntity>)

    /**
     * Retrieves all cached cards.
     *
     * @return A list of [AllCardEntity] objects representing the cached cards.
     */
    @Query("SELECT * FROM all_cards")
    suspend fun getAllCachedCards(): List<AllCardEntity>

    /**
     * Clears all cached cards.
     */
    @Query("DELETE FROM all_cards")
    suspend fun clearAllCachedCards()

    /**
     * Finds a card in the cache by its set code.
     *
     * @param setCode The set code to search for.
     * @return The [AllCardEntity] if found, null otherwise.
     */
    @Query("SELECT * FROM all_cards WHERE extNumber = :setCode LIMIT 1")
    suspend fun findCardBySetCode(setCode: String): AllCardEntity?

    /**
     * Searches for cards in the cache based on a search query.
     *
     * The search is performed on both the card name and the set name.
     *
     * @param searchQuery The search query string.
     * @return A list of [AllCardEntity] objects that match the query.
     */
    @Query("SELECT * FROM all_cards WHERE name LIKE '%' || :searchQuery || '%' OR setName LIKE '%' || :searchQuery || '%'")
    suspend fun searchCards(searchQuery: String): List<AllCardEntity>

    /**
     * Retrieves a specific card from the cache by its product ID.
     *
     * @param productId The unique identifier of the card.
     * @return The [AllCardEntity] if found, null otherwise.
     */
    @Query("SELECT * FROM all_cards WHERE productId = :productId")
    suspend fun getCardById(productId: String): AllCardEntity?
}