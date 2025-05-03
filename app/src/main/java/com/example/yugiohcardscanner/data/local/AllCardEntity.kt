package com.example.yugiohcardscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a Yu-Gi-Oh! card entity for caching in the local database.
 *
 * This entity is used to store card data for faster retrieval, and
 * it is used as a cache for all cards that can be searched.
 *
 * @property productId Unique identifier of the card.
 * @property name Name of the card.
 * @property setName Name of the set to which the card belongs.
 * @property extNumber Card's number within the set.
 * @property extRarity Card's rarity.
 * @property imageUrl URL of the card's image.
 * @property marketPrice Current market price of the card.
 */
@Entity(tableName = "all_cards")
data class AllCardEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val setName: String,
    val extNumber: String,
    val extRarity: String,
    val imageUrl: String,
    val marketPrice: Double
)