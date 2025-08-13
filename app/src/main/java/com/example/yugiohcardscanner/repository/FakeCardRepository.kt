package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData

/**
 * Fake implementation of [CardRepository] for previewing and testing.
 */
class FakeCardRepository : CardRepository {

    private val placeholderCards = listOf(
        CardData(
            productId = "22984",
            name = "Red-Eyes B. Dragon",
            cleanName = "Red Eyes B Dragon",
            setName = "Starter Deck: Joey", // Assuming from SDJ prefix
            imageUrl = "drawable/red_eyes_b_dragon", // Updated
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
            setName = "Starter Deck: Joey", // Assuming from SDJ prefix
            imageUrl = "drawable/swordsman_of_landstar", // Updated
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
            setName = "Starter Deck: Joey", // Assuming from SDJ prefix
            imageUrl = "drawable/baby_dragon", // Updated
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
            setName = "Starter Deck: Joey", // Assuming from SDJ prefix
            imageUrl = "drawable/spirit_of_the_harp", // Updated
            extNumber = "SDJ-004",
            extRarity = "Common",
            groupId = 293,
            categoryId = 2,
            marketPrice = 0.29,
            storageUrl = null
        )
    )

    override suspend fun preloadAllCardsFromDataSource(): List<CardData> {
        return placeholderCards
    }

    override suspend fun findCardBySetCode(setCode: String): CardData? {
        return placeholderCards.find { it.extNumber.equals(setCode, ignoreCase = true) }
    }
}