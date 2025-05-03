package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.local.AllCardDao
import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toCardData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for managing the cache of all cards.
 *
 * This repository handles operations related to caching card data, such as
 * saving, retrieving, clearing, and finding cards in the cache. It uses
 * [AllCardDao] for database interactions and [FirebaseCardRepository] to
 * load cards from Firestore when they are not available in the cache.
 *
 * @property allCardDao The data access object for interacting with the all card cache.
 * @property firebaseCardRepository The repository for loading cards from Firestore.
 */
@Singleton
class CardCacheRepository @Inject constructor(
    private val allCardDao: AllCardDao,
    private val firebaseCardRepository: FirebaseCardRepository
) {
    /**
     * Caches a list of cards in the local database.
     *
     * @param cards The list of [CardData] objects to be cached.
     */
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

    /**
     * Retrieves all cached cards.
     *
     * @return A list of [CardData] objects representing the cached cards.
     */
    suspend fun getCachedCards(): List<CardData> {
        return allCardDao.getAllCachedCards().map { it.toCardData() }
    }

    /**
     * Clears all cached cards.
     */
    suspend fun clearCachedCards() {
        allCardDao.clearAllCachedCards()
    }

    /**
     * Finds a card in the cache by its set code.
     *
     * First, it tries to find the card in the local cache. If not found,
     * it attempts to load all cards from Firestore (as [FirebaseCardRepository]
     * loads all cards at once) and then caches them.
     *
     * @param setCode The set code of the card to find.
     * @return The [CardData] object if found, null otherwise.
     */
    suspend fun findCardBySetCode(setCode: String): CardData? {
        // First try to find in local cache
        val cachedCard = allCardDao.findCardBySetCode(setCode)?.toCardData()
        if (cachedCard != null) {
            return cachedCard
        }
        return null
    }

    /**
     * Ensures that cards are cached.
     *
     * Checks if there are any cached cards. If not, it loads all cards from
     * Firebase and caches them.
     */
    suspend fun ensureCardsAreCached() {
        // Check if we have any cached cards
        if (allCardDao.getAllCachedCards().isEmpty()) {
            // If not, load from Firebase and cache them
            val cards = firebaseCardRepository.preloadAllCardsFromFirestore()
            cacheCards(cards)
        }
    }
}