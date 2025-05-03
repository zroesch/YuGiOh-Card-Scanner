package com.example.yugiohcardscanner.ui.collection

import com.example.yugiohcardscanner.data.local.CardDao
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toEntity
import com.example.yugiohcardscanner.data.models.toCardData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for managing the user's collection of cards.
 *
 * This class provides methods to add, remove, retrieve, and manage cards in the collection.
 * It acts as an intermediary between the data sources (like the local database) and the
 * UI or view models.
 *
 * @property cardDao The data access object for interacting with the local card database.
 */
@Singleton
class CollectionRepository @Inject constructor(
    private val cardDao: CardDao
) {
    /**
     * Adds a card to the user's collection.
     *
     * If the card already exists in the collection, its count is incremented.
     * Otherwise, the card is added with a count of 1.
     *
     * @param card The [CardData] object representing the card to be added.
     */
    suspend fun addCardToCollection(card: CardData) {
        val existingCard = cardDao.getCardById(card.productId)
        val updatedCount = (existingCard?.count ?: 0) + 1
        cardDao.insertCard(card.toEntity(updatedCount))
    }

    /**
     * Removes a card from the user's collection.
     *
     * If the card's count is greater than 1, the count is decremented.
     * If the card's count is 1, the card is removed entirely from the collection.
     *
     * @param card The [CardData] object representing the card to be removed.
     */
    suspend fun removeCardFromCollection(card: CardData) {
        val existingCard = cardDao.getCardById(card.productId)
        if (existingCard != null && existingCard.count > 1) {
            val updatedCount = existingCard.count - 1
            cardDao.insertCard(card.toEntity(updatedCount))
        } else {
            cardDao.deleteCard(card.toEntity())
        }
    }

    /**
     * Retrieves all cards from the user's collection.
     *
     * @return A list of [CardData] objects representing the cards in the collection.
     */
    suspend fun getAllCardsFromCollection(): List<CardData> {
        return cardDao.getAllCards().map { it.toCardData() }
    }

    /**
     * Calculates the total market value of the user's collection.
     *
     * @return The total market value as a [Double].
     */
    suspend fun getTotalCollectionValue(): Double {
        return cardDao.getAllCards().sumOf { it.marketPrice * it.count }
    }

    /**
     * Gets the total number of cards in the user's collection.
     *
     * @return The total number of cards as an [Int].
     */
    suspend fun getNumberOfCards(): Int {
        return cardDao.getAllCards().sumOf { it.count }
    }

    /**
     * Clears the user's entire card collection.
     */
    suspend fun clearUserCollection() {
        cardDao.clearCollection()
    }
}