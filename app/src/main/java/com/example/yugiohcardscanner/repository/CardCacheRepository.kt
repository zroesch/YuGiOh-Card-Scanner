package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.local.CardDao
import com.example.yugiohcardscanner.data.models.CardData
import javax.inject.Inject

class CardCacheRepository @Inject constructor(
    private val cardDao: CardDao
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
        cardDao.insertAllCards(entities)
    }

    suspend fun getCachedCards(): List<CardData> {
        return cardDao.getAllCachedCards().map {
            CardData(
                productId = it.productId,
                name = it.name,
                setName = it.setName,
                extNumber = it.extNumber,
                extRarity = it.extRarity,
                imageUrl = it.imageUrl,
                marketPrice = it.marketPrice
            )
        }
    }

    suspend fun clearCachedCards() {
        cardDao.clearAllCachedCards()
    }
}
