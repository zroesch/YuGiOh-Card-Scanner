package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

/**
 * Fake implementation of [CardCacheOperations] for previewing and testing.
 * This class should not be used in production.
 */
class FakeCardCacheRepository : CardCacheOperations {

    private var cachedCards: List<CardData> = listOf(
        CardData(
            productId = "22984",
            name = "Red-Eyes B. Dragon",
            cleanName = "Red Eyes B Dragon",
            setName = "Starter Deck: Joey",
            imageUrl = "drawable/red_eyes_b_dragon",
            extNumber = "SDJ-001",
            extRarity = "Ultra Rare",
            groupId = 293,
            categoryId = 2,
            marketPrice = 6.0,
            storageUrl = null
        ),
        CardData(
            productId = "22985",
            name = "Swordsman of Landstar",
            cleanName = "Swordsman of Landstar",
            setName = "Starter Deck: Joey",
            imageUrl = "drawable/swordsman_of_landstar",
            extNumber = "SDJ-002",
            extRarity = "Common",
            groupId = 293,
            categoryId = 2,
            marketPrice = 0.28,
            storageUrl = null
        ),
        CardData(
            productId = "22986",
            name = "Baby Dragon",
            cleanName = "Baby Dragon",
            setName = "Starter Deck: Joey",
            imageUrl = "drawable/baby_dragon",
            extNumber = "SDJ-003",
            extRarity = "Common",
            groupId = 293,
            categoryId = 2,
            marketPrice = 0.43,
            storageUrl = null
        ),
        CardData(
            productId = "22987",
            name = "Spirit of the Harp",
            cleanName = "Spirit of the Harp",
            setName = "Starter Deck: Joey",
            imageUrl = "drawable/spirit_of_the_harp",
            extNumber = "SDJ-004",
            extRarity = "Common",
            groupId = 293,
            categoryId = 2,
            marketPrice = 0.29,
            storageUrl = null
        )
    )

    override suspend fun getCachedCards(): List<CardData> {
        return cachedCards
    }

    override suspend fun cacheCards(cards: List<CardData>) {
        cachedCards = cards
    }

    override suspend fun clearCachedCards() {
        cachedCards = emptyList()
    }

    override suspend fun findCardBySetCode(setCode: String): CardData? {
        return cachedCards.find { it.extNumber.equals(setCode, ignoreCase = true) }
    }

    override suspend fun ensureCardsAreCached() {
        // In a fake implementation, we can assume the cache is always populated
        // or we can add some logic here to populate it with placeholder data if needed.
    }
}