package com.example.yugiohcardscanner.repository

import com.example.yugiohcardscanner.data.models.CardData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseCardRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : CardRepository {

    override suspend fun preloadAllCardsFromFirestore(): List<CardData> {
        return try {
            val snapshot = firestore
                .collection("categories")
                .document("2")
                .collection("sets")
                .get()
                .await()

            val allCards = mutableListOf<CardData>()

            for (setDoc in snapshot.documents) {
                val productsSnapshot = setDoc.reference.collection("products").get().await()
                val cards = productsSnapshot.toObjects(CardData::class.java)
                allCards.addAll(cards)
            }

            allCards
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    // ✅ Load next batch of cards (paginated)
//    override suspend fun getNextBatchOfCards(
//        lastDoc: DocumentSnapshot?,
//        limit: Long
//    ): List<CardData> {
//        val baseQuery = firestore
//            .collectionGroup("products")
//            .limit(limit)
//
//        val query = lastDoc?.let {
//            baseQuery.startAfter(it)
//        } ?: baseQuery
//
//        return try {
//            val result = query.get().await()
//            result.documents.mapNotNull { it.toObject(CardData::class.java) }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyList()
//        }
//    }




    // ✅ Get the DocumentSnapshot of a card by productId (optional, for targeted pagination)
//    override suspend fun getSnapshotForCard(productId: String): DocumentSnapshot? {
//        return try {
//            val query = firestore
//                .collectionGroup("products")
//                .whereEqualTo("productId", productId)
//                .limit(1)
//                .get()
//                .await()
//
//            query.documents.firstOrNull()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
}
