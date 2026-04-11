package com.hugodev.red_up.core.di

import android.content.Context
import androidx.room.Room
import com.hugodev.red_up.core.data.local.AppDatabase
import com.hugodev.red_up.core.data.local.SyncDao
import com.hugodev.red_up.features.publications.data.datasources.local.PublicationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "upred_local.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePublicationDao(database: AppDatabase): PublicationDao {
        return database.publicationDao()
    }

    @Provides
    @Singleton
    fun provideSyncDao(database: AppDatabase): SyncDao {
        return database.syncDao()
    }
}
