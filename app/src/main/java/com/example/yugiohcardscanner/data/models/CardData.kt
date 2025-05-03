package com.example.yugiohcardscanner.data.models

import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.local.CollectionCardEntity

/**
 * Data class representing a Yu-Gi-Oh! card.
 *
 * This class holds various properties of a card, such as its product ID, name,
 * set name, image URL, rarity, number within the set, market price, storage URL, and count.
 *
 * @property productId Unique identifier of the card.
 * @property name Name of the card.
 * @property cleanName Cleaned version of the card name.
 * @property setName Name of the card's set.
 * @property imageUrl URL of the card's image.
 * @property extNumber Card's number within the set.
 * @property extRarity Card's rarity.
 * @property groupId Card's group ID.
 * @property categoryId Card's category ID.
 * @property marketPrice Current market price of the card.
 * @property storageUrl URL for the card's image in storage (if available).
 * @property count Number of copies of this card in the user's collection.
 */
data class CardData(
    val productId: String = "",
    val name: String = "",
    val cleanName: String = "",
    val setName: String = "",
    val imageUrl: String = "",
    val extNumber: String = "",
    val extRarity: String = "",
    val groupId: Int = 0,
    val categoryId: Int = 0,
    val marketPrice: Double = 0.0,
    val storageUrl: String? = null,
    val count: Int = 1
)

/**
 * Converts a [CardData] object to a [CollectionCardEntity] object.
 *
 * @param count Optional parameter to specify the number of copies of the card.
 * @return A [CollectionCardEntity] object representing the card.
 */
fun CardData.toEntity(count: Int = this.count): CollectionCardEntity {
    return CollectionCardEntity(
        productId = productId,
        name = name,
        setName = setName,
        extNumber = extNumber,
        extRarity = extRarity,
        imageUrl = imageUrl,
        marketPrice = marketPrice,
        count = count
    )
}

/**
 * Converts a [CollectionCardEntity] object to a [CardData] object.
 *
 * @return A [CardData] object representing the card.
 */
fun CollectionCardEntity.toCardData(): CardData {
    return CardData(
        productId = productId,
        name = name,
        setName = setName,
        extNumber = extNumber,
        extRarity = extRarity,
        imageUrl = imageUrl,
        marketPrice = marketPrice,
        count = count
    )
}

/**
 * Converts an [AllCardEntity] object to a [CardData] object.
 *
 * @return A [CardData] object representing the card.
 */
fun AllCardEntity.toCardData(): CardData {
    return CardData(
        productId = productId,
        name = name,
        setName = setName,
        extNumber = extNumber,
        extRarity = extRarity,
        imageUrl = imageUrl,
        marketPrice = marketPrice
    )
}

/**
 * Converts a [CardData] object to an [AllCardEntity] object.
 *
 * @return An [AllCardEntity] object representing the card.
 */
fun CardData.toAllCardEntity(): AllCardEntity {
    return AllCardEntity(
        productId = productId,
        name = name,
        setName = setName,
        extNumber = extNumber,
        extRarity = extRarity,
        imageUrl = imageUrl,
        marketPrice = marketPrice
    )
}