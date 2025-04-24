package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

interface CardRepository {
    suspend fun preloadAllCardsFromFirestore(): List<CardData>
    suspend fun findCardBySetCode(setCode: String): CardData?
    suspend fun getCachedCards(): List<CardData>
    suspend fun clearCachedCards()
}