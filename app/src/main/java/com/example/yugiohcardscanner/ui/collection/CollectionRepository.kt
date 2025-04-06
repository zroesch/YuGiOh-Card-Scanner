package com.example.yugiohcardscanner.ui.collection

import com.example.yugiohcardscanner.data.local.CardDao
import com.example.yugiohcardscanner.data.models.CardData
import com.example.yugiohcardscanner.data.models.toEntity
import com.example.yugiohcardscanner.data.models.toCardData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepository @Inject constructor(
    private val cardDao: CardDao
) {
    suspend fun addCardToCollection(card: CardData) {
        val existingCard = cardDao.getCardById(card.productId)
        val updatedCount = (existingCard?.count ?: 0) + 1
        cardDao.insertCard(card.toEntity(updatedCount))
    }

    suspend fun removeCardFromCollection(card: CardData) {
        val existingCard = cardDao.getCardById(card.productId)
        if (existingCard != null && existingCard.count > 1) {
            val updatedCount = existingCard.count - 1
            cardDao.insertCard(card.toEntity(updatedCount))
        } else {
            cardDao.deleteCard(card.toEntity())
        }
    }

    suspend fun getAllCardsFromCollection(): List<CardData> {
        return cardDao.getAllCards().map { it.toCardData() }
    }

    suspend fun clearUserCollection() {
        cardDao.clearCollection()
    }


}
