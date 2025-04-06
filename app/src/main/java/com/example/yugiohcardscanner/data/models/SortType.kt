package com.example.yugiohcardscanner.data.models

enum class SortType {
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    CARD_NUMBER_LOW_TO_HIGH,
    CARD_NUMBER_HIGH_TO_LOW,
    NAME_A_TO_Z,
    NAME_Z_TO_A,
    RARITY_LOW_TO_HIGH,
    RARITY_HIGH_TO_LOW,
    NONE;

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
            else -> 99 // Unknown rarity goes last
        }
    }
}

