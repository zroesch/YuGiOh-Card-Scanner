package com.example.yugiohcardscanner.ui.collection

import com.example.yugiohcardscanner.data.models.CardData

/**
 * Fake implementation of [CollectionRepository] for previews.
 * This class should not be used in production.
 *
 * @param placeholderCards The initial list of cards in the collection.
 */
class FakeCollectionRepository(
    private val placeholderCards: List<CardData> = listOf(
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
) : CollectionRepository(cardDao = null) { // Pass null for the real DAO

    private val collection = mutableListOf<CardData>()

    init {
        collection.addAll(placeholderCards)
    }

    override suspend fun addCardToCollection(card: CardData) {
        collection.add(card)
    }

    override suspend fun removeCardFromCollection(card: CardData) {
        collection.remove(card)
    }

    override suspend fun getAllCardsFromCollection(): List<CardData> {
        return collection
    }

    override suspend fun getTotalCollectionValue(): Double {
        return collection.sumOf { it.marketPrice }
    }

    override suspend fun getNumberOfCards(): Int {
        return collection.size
    }

    override suspend fun clearUserCollection() {
        collection.clear()
    }
}