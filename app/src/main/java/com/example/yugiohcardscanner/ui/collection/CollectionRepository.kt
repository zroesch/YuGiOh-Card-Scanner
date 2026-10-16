package com.example.yugiohcardscanner.ui.collection

import com.example.yugiohcardscanner.data.local.CardDao
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toEntity
import com.example.yugiohcardscanner.data.models.toCardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for managing the user's collection of cards.
 */
@Singleton
open class CollectionRepository @Inject constructor(
    private val cardDao: CardDao?
) {
    /**
     * Adds a card to the user's collection.
     */
    open suspend fun addCardToCollection(card: CardData) {
        val existingCard = cardDao?.getCardById(card.productId)
        val updatedCount = (existingCard?.count ?: 0) + 1
        cardDao?.insertCard(card.toEntity(updatedCount))
    }

    /**
     * Removes a card from the user's collection.
     */
    open suspend fun removeCardFromCollection(card: CardData) {
        val existingCard = cardDao?.getCardById(card.productId)
        if (existingCard != null && existingCard.count > 1) {
            val updatedCount = existingCard.count - 1
            cardDao?.insertCard(card.toEntity(updatedCount))
        } else {
            cardDao?.deleteCard(card.toEntity())
        }
    }

    /**
     * Retrieves a flow of all cards from the user's collection.
     */
    open fun getAllCardsFlow(): Flow<List<CardData>> {
        return cardDao?.getAllCards()?.map { list -> list.map { it.toCardData() } } ?: emptyFlow()
    }

    /**
     * Calculates the total market value of the user's collection.
     */
    open suspend fun getTotalCollectionValue(): Double {
        return cardDao?.getAllCards()?.map { list -> list.sumOf { it.marketPrice * it.count } }
            ?.firstOrNull() ?: 0.0
    }

    /**
     * Gets the total number of cards in the user's collection.
     */
    open suspend fun getNumberOfCards(): Int {
        return cardDao?.getAllCards()?.map { list -> list.sumOf { it.count } }
            ?.firstOrNull() ?: 0
    }

    /**
     * Clears the user's entire card collection.
     */
    open suspend fun clearUserCollection() {
        cardDao?.clearCollection()
    }
}