package com.hugodev.red_up.features.groups.data.di

import com.hugodev.red_up.core.di.UpRedRetrofit
import com.hugodev.red_up.features.groups.data.datasources.remote.api.GroupsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GroupsNetworkModule {
    @Provides
    @Singleton
    fun provideGroupsApi(@UpRedRetrofit retrofit: Retrofit): GroupsApi {
        return retrofit.create(GroupsApi::class.java)
    }
}
