package com.hugodev.red_up.features.groups.data.di

import com.hugodev.red_up.features.groups.data.repositories.GroupRepositoryImpl
import com.hugodev.red_up.features.groups.domain.repositories.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GroupsModule {
    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        groupRepositoryImpl: GroupRepositoryImpl
    ): GroupRepository
}
