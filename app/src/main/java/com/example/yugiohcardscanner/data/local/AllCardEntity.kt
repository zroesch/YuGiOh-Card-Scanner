package com.example.yugiohcardscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


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