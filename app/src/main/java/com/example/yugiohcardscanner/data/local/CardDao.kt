package com.example.yugiohcardscanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for interacting with the user's collection of cards.
 *
 * This interface defines the methods to query, insert, update, and delete card
 * data from the local database.
 */
@Dao
interface CardDao {
    /**
     * Retrieves all cards from the user's collection.
     *
     * @return A list of [CollectionCardEntity] objects representing the cards.
     */
    @Query("SELECT * FROM user_collection")
    suspend fun getAllCards(): List<CollectionCardEntity>

    /**
     * Retrieves a specific card from the user's collection by its product ID.
     *
     * @param productId The unique identifier of the card.
     * @return The [CollectionCardEntity] if found, null otherwise.
     */
    @Query("SELECT * FROM user_collection WHERE productId = :productId")
    suspend fun getCardById(productId: String): CollectionCardEntity?

    /**
     * Inserts a card into the user's collection.
     *
     * If a card with the same primary key (productId) already exists, it will be
     * replaced.
     *
     * @param card The [CollectionCardEntity] to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CollectionCardEntity)

    /**
     * Deletes a card from the user's collection.
     *
     * @param card The [CollectionCardEntity] to be deleted.
     */
    @Delete
    suspend fun deleteCard(card: CollectionCardEntity)

    /**
     * Clears the user's entire card collection.
     */
    @Query("DELETE FROM user_collection")
    suspend fun clearCollection()

    /**
     * Increments the count of a specific card in the user's collection.
     *
     * @param productId The unique identifier of the card.
     */
    @Query("UPDATE user_collection SET count = count + 1 WHERE productId = :productId")
    suspend fun incrementCardCount(productId: String)

    /**
     * Decrements the count of a specific card in the user's collection, but only if
     * the count is greater than 0.
     *
     * @param productId The unique identifier of the card.
     */
    @Query("UPDATE user_collection SET count = count - 1 WHERE productId = :productId AND count > 0")
    suspend fun decrementCardCount(productId: String)
}