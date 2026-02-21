package com.hugodev.red_up.features.publications.data.di

import com.hugodev.red_up.core.di.UpRedRetrofit
import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpRedNetworkModule {
    @Provides
    @Singleton
    fun provideUpRedApi(@UpRedRetrofit retrofit: Retrofit): UpRedApi {
        return retrofit.create(UpRedApi::class.java)
    }
}