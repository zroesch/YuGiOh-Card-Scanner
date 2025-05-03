package com.example.yugiohcardscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a card entity in the user's collection.
 *
 * This entity stores data about the cards that the user has added to their
 * collection, including the count of how many copies they own.
 *
 * @property productId Unique identifier of the card.
 * @property name Name of the card.
 * @property setName Name of the set to which the card belongs.
 * @property extNumber Card's number within the set.
 * @property extRarity Card's rarity.
 * @property imageUrl URL of the card's image.
 * @property marketPrice Current market price of the card.
 * @property count Number of copies of this card in the user's collection.
 */
@Entity(tableName = "user_collection")
data class CollectionCardEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val setName: String,
    val extNumber: String,
    val extRarity: String,
    val imageUrl: String,
    val marketPrice: Double,
    val count: Int = 1
)