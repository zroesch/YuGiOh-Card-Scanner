package com.example.yugiohcardscanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CollectionCardEntity::class, AllCardEntity::class], version = 1, exportSchema = false)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}
