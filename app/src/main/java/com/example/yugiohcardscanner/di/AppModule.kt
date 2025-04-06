package com.example.yugiohcardscanner.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.yugiohcardscanner.data.local.CardDao
import com.example.yugiohcardscanner.data.local.CardDatabase
import com.example.yugiohcardscanner.repository.CardRepository
import com.example.yugiohcardscanner.repository.FirebaseCardRepository
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideCardDatabase(app: Application): CardDatabase {
        return Room.databaseBuilder(
            app,
            CardDatabase::class.java,
            "card_database"
        ).fallbackToDestructiveMigrationFrom()
            .build()
    }

    @Provides
    @Singleton
    fun provideCardDao(database: CardDatabase): CardDao {
        return database.cardDao()
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(cardDao: CardDao): CollectionRepository {
        return CollectionRepository(cardDao)
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            useEmulator("10.0.2.2", 8080) // Emulator IP from Android device
        }
    }
}