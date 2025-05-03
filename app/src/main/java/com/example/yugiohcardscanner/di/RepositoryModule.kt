package com.example.yugiohcardscanner.di

import com.example.yugiohcardscanner.repository.CardRepository
import com.example.yugiohcardscanner.repository.FirebaseCardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing repository dependencies.
 *
 * This module is responsible for binding the [CardRepository] interface
 * to its concrete implementation, [FirebaseCardRepository]. It is installed
 * in the [SingletonComponent], making these bindings available throughout the app.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the [CardRepository] interface to the [FirebaseCardRepository] implementation.
     *
     * This binding is marked as a singleton, meaning only one instance of
     * [FirebaseCardRepository] will be created and shared throughout the app.
     *
     * @param impl The [FirebaseCardRepository] implementation to be bound.
     * @return An instance of [CardRepository].
     */
    @Binds
    @Singleton
    abstract fun bindCardRepository(
        impl: FirebaseCardRepository
    ): CardRepository
}