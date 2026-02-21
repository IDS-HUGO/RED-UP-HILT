package com.hugodev.red_up.features.publications.data.di

import com.hugodev.red_up.features.publications.data.repositories.PublicationRepositoryImpl
import com.hugodev.red_up.features.publications.domain.repositories.PublicationRepository
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
    abstract fun bindPublicationRepository(
        publicationRepositoryImpl: PublicationRepositoryImpl
    ): PublicationRepository
}