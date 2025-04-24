package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.local.AllCardDao
import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toCardData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardCacheRepository @Inject constructor(
    private val allCardDao: AllCardDao,
    private val firebaseCardRepository: FirebaseCardRepository
) {
    suspend fun cacheCards(cards: List<CardData>) {
        val entities = cards.map {
            AllCardEntity(
                productId = it.productId,
                name = it.name,
                setName = it.setName,
                extNumber = it.extNumber,
                extRarity = it.extRarity,
                imageUrl = it.imageUrl,
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
        // First try to find in local cache
        val cachedCard = allCardDao.findCardBySetCode(setCode)?.toCardData()
        if (cachedCard != null) {
            return cachedCard
        }

        // If not found in cache, try to load all cards from Firestore
        // (since your FirebaseCardRepository loads all cards at once)
//        val allCards = firebaseCardRepository.preloadAllCardsFromFirestore()
//
//        // Cache the new cards
//        cacheCards(allCards)
//
//        // Try to find the card with the matching set code
//        return allCards.find { it.extNumber == setCode }
        return null
    }

    suspend fun ensureCardsAreCached() {
        // Check if we have any cached cards
        if (allCardDao.getAllCachedCards().isEmpty()) {
            // If not, load from Firebase and cache them
            val cards = firebaseCardRepository.preloadAllCardsFromFirestore()
            cacheCards(cards)
        }
    }
}