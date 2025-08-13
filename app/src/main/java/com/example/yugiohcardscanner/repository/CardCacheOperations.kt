package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

/**
 * Interface defining the operations for a card cache.
 * This allows for interchangeable fake and real implementations for previews and testing.
 */
interface CardCacheOperations {
    suspend fun getCachedCards(): List<CardData>
    suspend fun cacheCards(cards: List<CardData>)
    suspend fun clearCachedCards()
    suspend fun findCardBySetCode(setCode: String): CardData?
    suspend fun ensureCardsAreCached()
}
