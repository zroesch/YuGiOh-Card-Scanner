package com.example.yugiohcardscanner.repository

import android.util.Log
import com.example.yugiohcardscanner.data.local.AllCardDao
import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toCardData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardCacheRepository @Inject constructor(
    private val allCardDao: AllCardDao,
    private val remoteCardRepository: CardRepository // Inject the interface
) {
    suspend fun cacheCards(cards: List<CardData>) {
        val entities = cards.map {
            AllCardEntity(
                productId = it.productId,
                name = it.name,
                setName = it.setName,
                extNumber = it.extNumber,
                extRarity = it.extRarity,
                imageUrl = it.imageUrl, // Store the direct image URL
                marketPrice = it.marketPrice
            )
        }
        allCardDao.insertAllCards(entities)
    }

    suspend fun getCachedCards(): List<CardData> {
        return allCardDao.getAllCachedCards().map { it.toCardData() }
    }

    suspend fun clearCachedCards() {
        allCardDao.clearAllCachedCards()
    }

    suspend fun findCardBySetCode(setCode: String): CardData? {
        val cachedCard = allCardDao.findCardBySetCode(setCode)?.toCardData()
        if (cachedCard != null) {
            return cachedCard
        }
        // If not in cache, you might not want to fetch EVERYTHING just for one card
        // if the remote source is CSV parsing. This behavior might need rethinking.
        // For now, it mirrors the old logic but will be less efficient.
        // Consider if this method is still needed or if search should primarily be on cached data.
        Log.d("CardCacheRepository", "Card with setCode $setCode not found in cache. Fetching from remote is not efficient here.")
        // Optionally, trigger a full refresh and then search, but that's heavy.
        // Or, simply return null if not in cache, and ensure cache is populated initially.
        return null // Or trigger a full sync if absolutely necessary
    }

    suspend fun ensureCardsAreCached() {
        if (allCardDao.getAllCachedCards().isEmpty()) {
            val cards = remoteCardRepository.preloadAllCardsFromDataSource() // Use the new method
            cacheCards(cards)
        }
    }
}