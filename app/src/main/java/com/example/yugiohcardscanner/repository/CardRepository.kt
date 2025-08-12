package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

interface CardRepository {
    /**
     * Loads all cards from the primary data source (now TCG CSV).
     */
    suspend fun preloadAllCardsFromDataSource(): List<CardData>

    /**
     * Finds a card by its set code from the primary data source.
     */
    suspend fun findCardBySetCode(setCode: String): CardData?
}