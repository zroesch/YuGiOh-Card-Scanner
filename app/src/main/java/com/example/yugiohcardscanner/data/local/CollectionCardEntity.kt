package com.example.yugiohcardscanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_collection")
data class CollectionCardEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val setName: String,
    val extNumber: String,
    val extRarity: String,
    val imageUrl: String,
//    val storageUrl: String,
    val marketPrice: Double,
    val count: Int = 1 // ✅ Track number of copies
)
