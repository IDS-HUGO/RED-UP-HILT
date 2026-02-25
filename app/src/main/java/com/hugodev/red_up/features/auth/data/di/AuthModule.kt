package com.hugodev.red_up.features.auth.data.di

import com.hugodev.red_up.core.di.UpRedRetrofit
import com.hugodev.red_up.features.auth.data.datasources.remote.api.AuthApi
import com.hugodev.red_up.features.auth.data.repositories.AuthRepositoryImpl
import com.hugodev.red_up.features.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApi(@UpRedRetrofit retrofit: Retrofit): AuthApi {
            return retrofit.create(AuthApi::class.java)
        }
    }
}
