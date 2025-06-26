package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

interface CardRepository {
    /**
     * Loads all cards from the primary data source (now TCG CSV).
     */
    suspend fun preloadAllCardsFromDataSource(): List<CardData> // Renamed for clarity

    // findCardBySetCode might become less efficient if you have to parse all CSVs each time.
    // Caching will be crucial here.
    suspend fun findCardBySetCode(setCode: String): CardData?

    // These caching related methods might be better suited solely for CardCacheRepository
    // or you can have default implementations here if some repositories don't cache.
    suspend fun getCachedCards(): List<CardData>
    suspend fun clearCachedCards()
}