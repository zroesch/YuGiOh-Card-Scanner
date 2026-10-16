package com.example.yugiohcardscanner.repository

import android.util.Log
import com.example.yugiohcardscanner.data.local.AllCardDao
import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toCardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardCacheRepository @Inject constructor(
    private val allCardDao: AllCardDao,
    private val remoteCardRepository: CardRepository // Inject the interface
) : CardCacheOperations {
    override suspend fun cacheCards(cards: List<CardData>) = withContext(Dispatchers.IO) {
        // Chunking prevents SQLite memory crashes during massive syncs
        cards.chunked(1000).forEach { chunk ->
            val entities = chunk.map {
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
    }

    override suspend fun getCachedCards(): List<CardData> = withContext(Dispatchers.IO) {
        allCardDao.getAllCachedCards().map { it.toCardData() }
    }

    override suspend fun clearCachedCards() {
        allCardDao.clearAllCachedCards()
    }

    override suspend fun findCardBySetCode(setCode: String): CardData? = withContext(Dispatchers.IO) {
        val cachedCard = allCardDao.findCardBySetCode(setCode)?.toCardData()
        if (cachedCard != null) {
            return@withContext cachedCard
        }
        Log.d("CardCacheRepository", "Card with setCode $setCode not found in cache. Fetching from remote is not efficient here.")
        return@withContext null
    }

    override suspend fun ensureCardsAreCached() {
        if (allCardDao.getAllCachedCards().isEmpty()) {
            val cards = remoteCardRepository.preloadAllCardsFromDataSource() // Use the new method
            cacheCards(cards)
        }
    }
}
