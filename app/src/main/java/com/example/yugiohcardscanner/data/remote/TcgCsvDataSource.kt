package com.example.yugiohcardscanner.data.remote

import android.util.Log
import com.example.yugiohcardscanner.data.models.CardData
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

private const val TAG = "TcgCsvDataSource"
private const val BASE_URL_YUGIOH = "https://tcgcsv.com/tcgplayer/2/"

/**
 * A data source responsible for fetching Yu-Gi-Oh! card data from the TCG CSV remote source (tcgcsv.com).
 *
 * This class handles the network requests and CSV parsing for card groups (sets) and
 * individual card products within those groups. It uses OkHttp for network operations
 * and the kotlin-csv library for efficient CSV parsing.
 *
 * @property okHttpClient The OkHttp client instance used for making network requests.
 */
class TcgCsvDataSource @Inject constructor(private val okHttpClient: OkHttpClient) {

    /**
     * Fetches all card data from the remote TCG CSV source.
     *
     * This process involves two main steps:
     * 1. Fetching a list of all card groups (sets) from `Groups.csv`.
     * 2. For each group, fetching its corresponding `ProductsAndPrices.csv` which contains
     *    the individual card data.
     *
     * All operations are performed on the IO dispatcher.
     *
     * @return A list of [CardData] objects representing all cards fetched from the source.
     *         Returns an empty list if fetching fails or if no cards are found.
     */
    suspend fun fetchAllCards(): List<CardData> {
        Log.d(TAG, "fetchAllCards called")
        return withContext(Dispatchers.IO) {
            val allCards = mutableListOf<CardData>()
            Log.d(TAG, "Fetching groups...")
            val groups = fetchGroupsWithCsvLibrary()
            Log.d(TAG, "Fetched ${groups.size} groups.")

            for (group in groups) {
                Log.d(TAG, "Fetching products for group: ${group.name} (ID: ${group.groupIdInSet})")
                val cardsInGroup = fetchProductsForGroupWithCsvLibrary(group.groupIdInSet, group.name)
                Log.d(TAG, "Fetched ${cardsInGroup.size} cards for group: ${group.name}")
                allCards.addAll(cardsInGroup)
            }
            Log.d(TAG, "Finished fetching all cards. Total cards: ${allCards.size}")
            allCards
        }
    }

    /**
     * Fetches information about all card groups (sets) from the `Groups.csv` file.
     *
     * Uses the kotlin-csv library to parse the CSV data.
     *
     * @return A list of [GroupInfo] objects, each containing the ID and name of a card set.
     *         Returns an empty list if the request fails or parsing errors occur.
     */
    private suspend fun fetchGroupsWithCsvLibrary(): List<GroupInfo> {
        val groups = mutableListOf<GroupInfo>()
        val url = "${BASE_URL_YUGIOH}Groups.csv"
        Log.d(TAG, "fetchGroupsWithCsvLibrary: Requesting URL: $url")
        val request = Request.Builder().url(url).build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful || response.body == null) {
                    Log.e(TAG, "fetchGroupsWithCsvLibrary: Error: ${response.message}, Code: ${response.code}")
                    return emptyList()
                }
                Log.d(TAG, "fetchGroupsWithCsvLibrary: Successfully fetched groups CSV. Parsing...")
                response.body!!.byteStream().use { inputStream ->
                    csvReader()
                        .readAllWithHeader(inputStream).forEach { rowMap: Map<String, String> ->
                            val csvGroupId = rowMap["groupId"]
                            val csvName = rowMap["name"]

                            if (csvGroupId != null && csvName != null) {
                                groups.add(GroupInfo(groupIdInSet = csvGroupId, name = csvName))
                            } else {
                                Log.w(TAG, "fetchGroupsWithCsvLibrary: Skipping row with missing groupId or name: $rowMap")
                            }
                        }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchGroupsWithCsvLibrary: Exception", e)
        }
        Log.d(TAG, "fetchGroupsWithCsvLibrary: Returning ${groups.size} groups.")
        return groups
    }

    /**
     * Fetches product (card) data for a specific group (set) from its `ProductsAndPrices.csv` file.
     *
     * Uses the kotlin-csv library to parse the CSV data, accessing fields by their header names.
     * It filters out items that are not individual cards (e.g., booster packs) based on the
     * absence of `extNumber` and `extRarity`. It also combines `subTypeName` (like "1st Edition")
     * with the base rarity.
     *
     * @param groupIdFromGroupCsv The ID of the group/set for which to fetch products. This ID is used
     *                            to construct the URL for the specific `ProductsAndPrices.csv`.
     * @param groupNameFromGroupCsv The name of the group/set, used to populate the `setName` field
     *                              in the [CardData] objects.
     * @return A list of [CardData] objects for the specified group. Returns an empty list if the
     *         request fails, parsing errors occur, or no valid card products are found.
     */
    private suspend fun fetchProductsForGroupWithCsvLibrary(
        groupIdFromGroupCsv: String,
        groupNameFromGroupCsv: String
    ): List<CardData> {
        val products = mutableListOf<CardData>()
        val url = "$BASE_URL_YUGIOH$groupIdFromGroupCsv/ProductsAndPrices.csv"
        Log.d(TAG, "fetchProductsForGroupWithCsvLibrary: Requesting URL for group '$groupNameFromGroupCsv': $url")
        val request = Request.Builder().url(url).build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful || response.body == null) {
                    Log.e(TAG, "fetchProductsForGroupWithCsvLibrary: Error fetching products for group $groupIdFromGroupCsv ($groupNameFromGroupCsv): ${response.message}, Code: ${response.code}")
                    return emptyList()
                }
                Log.d(TAG, "fetchProductsForGroupWithCsvLibrary: Successfully fetched CSV for group '$groupNameFromGroupCsv'. Parsing...")

                response.body!!.byteStream().use { inputStream ->
                    csvReader()
                        .readAllWithHeader(inputStream).forEach { rowMap: Map<String, String> ->

                            val productId = rowMap["productId"]
                            val name = rowMap["name"]

                            // Essential fields check
                            if (productId.isNullOrBlank() || name.isNullOrBlank()) {
                                Log.w(TAG, "fetchProductsForGroupWithCsvLibrary: Skipping row for group '$groupNameFromGroupCsv' due to missing productId or name. Row: $rowMap")
                                return@forEach
                            }

                            val extNumber = rowMap["extNumber"]
                            val extRarityFromCsv = rowMap["extRarity"]

                            // Skip non-card items based on extNumber and extRarity
                            if (extNumber.isNullOrBlank() && extRarityFromCsv.isNullOrBlank()) {
                                Log.v(TAG, "fetchProductsForGroupWithCsvLibrary: Skipping non-card item in group '$groupNameFromGroupCsv': $name (missing extNumber & extRarity)")
                                return@forEach
                            }

                            val imageUrl = rowMap["imageUrl"]
                            // Ensure imageUrl is present
                            if (imageUrl.isNullOrBlank()) {
                                Log.w(TAG, "fetchProductsForGroupWithCsvLibrary: Skipping card in group '$groupNameFromGroupCsv' due to missing imageUrl. Product: $name")
                                return@forEach
                            }

                            // Combine subTypeName with rarity
                            val subTypeName = rowMap["subTypeName"]
                            val finalExtRarity = if (!subTypeName.isNullOrBlank() &&
                                !extRarityFromCsv.isNullOrBlank() &&
                                !extRarityFromCsv.contains(subTypeName, ignoreCase = true)
                            ) {
                                "$extRarityFromCsv ($subTypeName)"
                            } else {
                                extRarityFromCsv ?: ""
                            }

                            products.add(
                                CardData(
                                    productId = productId,
                                    name = name,
                                    cleanName = rowMap["cleanName"] ?: name,
                                    setName = groupNameFromGroupCsv,
                                    imageUrl = imageUrl,
                                    extNumber = extNumber ?: "",
                                    extRarity = finalExtRarity,
                                    groupId = rowMap["groupId"]?.toIntOrNull() ?: 0,
                                    categoryId = rowMap["categoryId"]?.toIntOrNull() ?: 0,
                                    marketPrice = rowMap["marketPrice"]?.toDoubleOrNull() ?: 0.0,
                                    storageUrl = null, // storageUrl is not available from this CSV source
                                    count = 1 // Default count
                                )
                            )
                        }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchProductsForGroupWithCsvLibrary: Exception for group $groupIdFromGroupCsv ($groupNameFromGroupCsv)", e)
        }
        Log.d(TAG, "fetchProductsForGroupWithCsvLibrary: Returning ${products.size} products for group '$groupNameFromGroupCsv'.")
        return products
    }

    /**
     * Internal data class to hold information parsed from `Groups.csv`.
     *
     * @property groupIdInSet The unique identifier for the card group/set.
     * @property name The human-readable name of the card group/set.
     */
    private data class GroupInfo(val groupIdInSet: String, val name: String)
}