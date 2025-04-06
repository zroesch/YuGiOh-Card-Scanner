package com.example.yugiohcardscanner.utils

import com.example.yugiohcardscanner.data.models.CardData
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toCardDataOrNull(): CardData? {
    return try {
        CardData(
            productId = getString("productId") ?: return null,
            name = getString("name") ?: "",
            cleanName = getString("cleanName") ?: "",
            setName = getString("setName") ?: "",
            extNumber = getString("extNumber") ?: "",
            extRarity = getString("extRarity") ?: "",
            imageUrl = getString("imageUrl") ?: "",
            storageUrl = getString("storageUrl"),
            groupId = getLong("groupId")?.toInt() ?: 0,
            categoryId = getLong("categoryId")?.toInt() ?: 0,
            marketPrice = getDouble("marketPrice") ?: 0.0
        )
    } catch (e: Exception) {
        null
    }
}
