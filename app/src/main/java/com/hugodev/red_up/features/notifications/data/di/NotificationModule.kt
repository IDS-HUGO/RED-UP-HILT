package com.hugodev.red_up.features.notifications.data.di

import com.hugodev.red_up.features.notifications.data.repositories.NotificationRepositoryImpl
import com.hugodev.red_up.features.notifications.domain.repositories.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository
}