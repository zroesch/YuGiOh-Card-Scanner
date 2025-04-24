package com.example.yugiohcardscanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CollectionCardEntity::class, AllCardEntity::class],
    version = 1
)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun allCardDao(): AllCardDao
}
