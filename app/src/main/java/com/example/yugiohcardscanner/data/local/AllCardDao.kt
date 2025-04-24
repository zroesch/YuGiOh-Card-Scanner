package com.example.yugiohcardscanner.data.local

import androidx.room.*

@Dao
interface AllCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCards(cards: List<AllCardEntity>)

    @Query("SELECT * FROM all_cards")
    suspend fun getAllCachedCards(): List<AllCardEntity>

    @Query("DELETE FROM all_cards")
    suspend fun clearAllCachedCards()

    @Query("SELECT * FROM all_cards WHERE extNumber = :setCode LIMIT 1")
    suspend fun findCardBySetCode(setCode: String): AllCardEntity?

    @Query("SELECT * FROM all_cards WHERE name LIKE '%' || :searchQuery || '%' OR setName LIKE '%' || :searchQuery || '%'")
    suspend fun searchCards(searchQuery: String): List<AllCardEntity>

    @Query("SELECT * FROM all_cards WHERE productId = :productId")
    suspend fun getCardById(productId: String): AllCardEntity?
}