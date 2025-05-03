package com.example.yugiohcardscanner.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.yugiohcardscanner.data.local.AllCardDao
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

/**
 * Dagger Hilt module for providing application-level dependencies.
 *
 * This module provides various dependencies like the application context,
 * Room database, DAOs, and Firebase Firestore. It is installed in the
 * [SingletonComponent], making these dependencies available throughout the app.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context.
     *
     * @param app The application instance.
     * @return The application context.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(app: Application): Context = app.applicationContext

    /**
     * Provides the Room database instance for cards.
     *
     * @param app The application instance.
     * @return The [CardDatabase] instance.
     */
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

    /**
     * Provides the [CardDao] instance.
     *
     * @param database The [CardDatabase] instance.
     * @return The [CardDao] instance.
     */
    @Provides
    @Singleton
    fun provideCardDao(database: CardDatabase): CardDao {
        return database.cardDao()
    }

    /**
     * Provides the [CollectionRepository] instance.
     *
     * @param cardDao The [CardDao] instance.
     * @return The [CollectionRepository] instance.
     */
    @Provides
    @Singleton
    fun provideCollectionRepository(cardDao: CardDao): CollectionRepository {
        return CollectionRepository(cardDao)
    }

    /**
     * Provides the Firebase Firestore instance.
     *
     * Initializes and configures Firestore with an emulator address.
     *
     * @return The [FirebaseFirestore] instance.
     */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            useEmulator("192.168.4.113", 8080) // Emulator IP from Android device
        }
    }

    /**
     * Provides the [AllCardDao] instance.
     *
     * @param database The [CardDatabase] instance.
     * @return The [AllCardDao] instance.
     */
    @Provides
    fun provideAllCardDao(database: CardDatabase): AllCardDao {
        return database.allCardDao()
    }
}