package com.example.yugiohcardscanner.data.local

import androidx.room.*

@Dao
interface CardDao {
    @Query("SELECT * FROM user_collection")
    suspend fun getAllCards(): List<CollectionCardEntity>

    @Query("SELECT * FROM user_collection WHERE productId = :productId")
    suspend fun getCardById(productId: String): CollectionCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CollectionCardEntity)

    @Delete
    suspend fun deleteCard(card: CollectionCardEntity)

    @Query("DELETE FROM user_collection")
    suspend fun clearCollection()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCards(cards: List<AllCardEntity>)

    @Query("SELECT * FROM all_cards")
    suspend fun getAllCachedCards(): List<AllCardEntity>

    @Query("DELETE FROM all_cards")
    suspend fun clearAllCachedCards()
}
