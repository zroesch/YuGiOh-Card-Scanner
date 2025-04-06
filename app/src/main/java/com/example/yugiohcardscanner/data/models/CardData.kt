package com.example.yugiohcardscanner.data.models

import com.example.yugiohcardscanner.data.local.AllCardEntity
import com.example.yugiohcardscanner.data.local.CollectionCardEntity

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

// ✅ Conversion Functions
fun CardData.toEntity(count: Int = this.count): CollectionCardEntity {
    return CollectionCardEntity(
        productId = productId,
        name = name,
        setName = setName,
        extNumber = extNumber,
        extRarity = extRarity,
        imageUrl = imageUrl,
//        storageUrl = storageUrl,
        marketPrice = marketPrice,
        count = count
    )
}

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




