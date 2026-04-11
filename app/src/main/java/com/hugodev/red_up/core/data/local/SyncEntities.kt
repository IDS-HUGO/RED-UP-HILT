package com.hugodev.red_up.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey val id: Int = 1,
    val lastSyncAt: Long? = null,
    val pendingCount: Int = 0,
    val lastError: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pending_sync_events")
data class PendingSyncEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventType: String,
    val payloadJson: String,
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis(),
    val lastAttemptAt: Long? = null,
    val attempts: Int = 0
)

@Entity(tableName = "notification_summary_cache")
data class NotificationSummaryEntity(
    @PrimaryKey val id: Int = 1,
    val unreadCount: Int = 0,
    val lastFetchedAt: Long = 0,
    val source: String = "remote"
)
