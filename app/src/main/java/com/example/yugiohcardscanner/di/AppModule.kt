package com.example.yugiohcardscanner.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.yugiohcardscanner.data.local.AllCardDao
import com.example.yugiohcardscanner.data.local.CardDao
import com.example.yugiohcardscanner.data.local.CardDatabase
import com.example.yugiohcardscanner.data.remote.TcgCsvDataSource
import com.example.yugiohcardscanner.repository.CardRepository
import com.example.yugiohcardscanner.repository.CsvCardRepository
import com.example.yugiohcardscanner.ui.collection.CollectionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing application-level dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the application context.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(app: Application): Context = app.applicationContext

    /**
     * Provides the Room database instance for cards.
     */
    @Provides
    @Singleton
    fun provideCardDatabase(app: Application): CardDatabase {
        return Room.databaseBuilder(
            app,
            CardDatabase::class.java,
            "card_database"
        ).fallbackToDestructiveMigration() // Be mindful of data loss during development.
            // For production, implement proper migrations.
            .build()
    }

    /**
     * Provides the [CardDao] instance for collection management.
     */
    @Provides
    @Singleton
    fun provideCardDao(database: CardDatabase): CardDao {
        return database.cardDao()
    }

    /**
     * Provides the [CollectionRepository] instance.
     */
    @Provides
    @Singleton
    fun provideCollectionRepository(cardDao: CardDao): CollectionRepository {
        return CollectionRepository(cardDao)
    }

    /**
     * Provides the [AllCardDao] instance for the main card cache.
     */
    @Provides
    @Singleton
    fun provideAllCardDao(database: CardDatabase): AllCardDao {
        return database.allCardDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            // Add any specific configurations like timeouts, interceptors if needed
            .build()
    }

    @Provides
    @Singleton
    fun provideTcgCsvDataSource(okHttpClient: OkHttpClient): TcgCsvDataSource {
        return TcgCsvDataSource(okHttpClient)
    }
}