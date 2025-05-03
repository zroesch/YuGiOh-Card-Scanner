package com.example.yugiohcardscanner.data.models

/**
 * Enum class representing the different ways the card collection can be sorted.
 *
 * This enum provides options to sort cards by price, card number, name, or rarity,
 * in ascending or descending order. It also includes a NONE option for no sorting.
 */
enum class SortType {
    /** Sort by price, from lowest to highest. */
    PRICE_LOW_TO_HIGH,

    /** Sort by price, from highest to lowest. */
    PRICE_HIGH_TO_LOW,

    /** Sort by card number, from lowest to highest. */
    CARD_NUMBER_LOW_TO_HIGH,

    /** Sort by card number, from highest to lowest. */
    CARD_NUMBER_HIGH_TO_LOW,

    /** Sort by name, from A to Z. */
    NAME_A_TO_Z,

    /** Sort by name, from Z to A. */
    NAME_Z_TO_A,

    /** Sort by rarity, from lowest to highest. */
    RARITY_LOW_TO_HIGH,

    /** Sort by rarity, from highest to lowest. */
    RARITY_HIGH_TO_LOW,

    /** No sorting. */
    NONE;

    /**
     * Returns a [Comparator] based on the selected [SortType].
     *
     * The comparator is used to sort a list of [CardData] objects.
     *
     * @return A [Comparator] for sorting [CardData] objects.
     */
    fun getComparator(): Comparator<CardData> {
        return when (this) {
            PRICE_LOW_TO_HIGH -> compareBy { it.marketPrice }
            PRICE_HIGH_TO_LOW -> compareByDescending { it.marketPrice }
            CARD_NUMBER_LOW_TO_HIGH -> compareBy { it.extNumber }
            CARD_NUMBER_HIGH_TO_LOW -> compareByDescending { it.extNumber }
            NAME_A_TO_Z -> compareBy { it.name }
            NAME_Z_TO_A -> compareByDescending { it.name }
            RARITY_LOW_TO_HIGH -> compareBy { rarityRank(it.extRarity) }
            RARITY_HIGH_TO_LOW -> compareByDescending { rarityRank(it.extRarity) }
            NONE -> compareBy { it.name }
        }
    }

    /**
     * Determines the rank of a given card rarity.
     *
     * @param rarity The rarity string of the card.
     * @return An integer representing the rank of the rarity. Lower numbers indicate lower
     *     rarities.
     */
    private fun rarityRank(rarity: String?): Int {
        return when (rarity?.lowercase()?.trim()) {
            "common" -> 1
            "short print" -> 2
            "rare" -> 3
            "super rare" -> 4
            "ultra rare" -> 5
            "secret rare" -> 6
            "ultimate rare" -> 7
            "ghost rare" -> 8
            "collector's rare" -> 9
            "starlight rare" -> 10
            else -> 99
        }
    }
}