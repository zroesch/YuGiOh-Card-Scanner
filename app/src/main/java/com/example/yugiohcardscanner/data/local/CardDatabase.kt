package com.example.yugiohcardscanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database class for managing the card database.
 *
 * This class defines the database entities and the version of the database.
 * It also provides access to the Data Access Objects (DAOs) for interacting
 * with the database.
 */
@Database(
    entities = [CollectionCardEntity::class, AllCardEntity::class],
    version = 1
)
abstract class CardDatabase : RoomDatabase() {
    /**
     * Provides access to the [CardDao] for the user's collection.
     */
    abstract fun cardDao(): CardDao

    /**
     * Provides access to the [AllCardDao] for the all cards cache.
     */
    abstract fun allCardDao(): AllCardDao
}