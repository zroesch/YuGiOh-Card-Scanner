package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData
import com.google.firebase.firestore.DocumentSnapshot

interface CardRepository {
//    suspend fun getNextBatchOfCards(lastDoc: DocumentSnapshot? = null, limit: Long = 20): List<CardData>
//    suspend fun getSnapshotForCard(productId: String): DocumentSnapshot?
    suspend fun preloadAllCardsFromFirestore(): List<CardData>
}


