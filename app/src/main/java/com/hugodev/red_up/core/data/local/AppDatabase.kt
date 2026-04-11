package com.hugodev.red_up.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hugodev.red_up.features.publications.data.datasources.local.PublicationDao
import com.hugodev.red_up.features.publications.data.datasources.local.PublicationEntity

@Database(
    entities = [
        PublicationEntity::class,
        SyncStatusEntity::class,
        PendingSyncEventEntity::class,
        NotificationSummaryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun publicationDao(): PublicationDao
    abstract fun syncDao(): SyncDao
}
