package com.hugodev.red_up.core.di

import com.hugodev.red_up.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {
    @Provides
    @Singleton
    @SocketBaseUrl
    fun provideSocketBaseUrl(): String {
        return BuildConfig.WS_URL
    }
}
