package com.example.yugiohcardscanner.di

import com.example.yugiohcardscanner.repository.CardCacheOperations
import com.example.yugiohcardscanner.repository.CardCacheRepository
import com.example.yugiohcardscanner.repository.CardRepository
import com.example.yugiohcardscanner.repository.CsvCardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        impl: CsvCardRepository
    ): CardRepository

    @Binds
    @Singleton
    abstract fun bindCardCacheOperations(
        impl: CardCacheRepository
    ): CardCacheOperations
}