package com.example.yugiohcardscanner.repository

import android.util.Log
import com.example.yugiohcardscanner.data.models.CardData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository class for interacting with Firebase Firestore to manage card data.
 *
 * This repository handles operations such as loading all cards from Firestore,
 * finding a card by its set code, and potentially fetching paginated batches
 * of cards. It is designed to work directly with Firestore and does not handle
 * caching, which is delegated to [CardCacheRepository].
 *
 * @property firestore The [FirebaseFirestore] instance for database interactions.
 */
class FirebaseCardRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : CardRepository {

    /**
     * Loads all cards from Firestore.
     *
     * This method retrieves all card data from Firestore in a hierarchical structure:
     * categories -> sets -> products. It iterates through all sets and products to
     * construct a complete list of [CardData] objects.
     *
     * @return A list of [CardData] objects representing all cards loaded from Firestore.
     */
    override suspend fun preloadAllCardsFromDataSource(): List<CardData> {
        Log.d("FirebaseCardRepo", "Starting preloadAllCardsFromFirestore")
        return try {
            val snapshot = firestore
                .collection("categories")
                .document("2")
                .collection("sets")
                .get()
                .await()

            Log.d("FirebaseCardRepo", "Sets snapshot size: ${snapshot.documents.size}")

            val allCards = mutableListOf<CardData>()

            for (setDoc in snapshot.documents) {
                Log.d("FirebaseCardRepo", "Processing set: ${setDoc.id}")
                val productsSnapshot = setDoc.reference.collection("products").get().await()
                Log.d("FirebaseCardRepo", "Products snapshot size: ${productsSnapshot.documents.size} for set: ${setDoc.id}")

                val cards = productsSnapshot.toObjects(CardData::class.java)
                allCards.addAll(cards)
            }

            Log.d("FirebaseCardRepo", "Total cards loaded: ${allCards.size}")
            allCards
        } catch (e: Exception) {
            Log.e("FirebaseCardRepo", "Error loading cards", e)
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Finds a card by its set code in Firestore.
     *
     * This method first loads all cards from Firestore and then searches for
     * the card with the matching set code.
     *
     * @param setCode The set code of the card to find.
     * @return The [CardData] object if found, null otherwise.
     */
    override suspend fun findCardBySetCode(setCode: String): CardData? {
        return try {
            val allCards = preloadAllCardsFromDataSource()
            allCards.find { it.extNumber == setCode }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Returns an empty list, as caching is not handled by this repository.
     * Caching is managed by [CardCacheRepository].
     *
     * @return An empty list of [CardData].
     */
    override suspend fun getCachedCards(): List<CardData> {
        return emptyList() // Caching is handled by CardCacheRepository
    }

    /**
     * No-op, as caching is not handled by this repository.
     * Caching is managed by [CardCacheRepository].
     */
    override suspend fun clearCachedCards() {
        // No-op as FirebaseCardRepository doesn't handle caching
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