package com.hugodev.red_up.navigation.di

import com.hugodev.red_up.navigation.NavigationManager
import com.hugodev.red_up.navigation.NavigationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindNavigationManager(
        navigationManagerImpl: NavigationManagerImpl
    ): NavigationManager
}
