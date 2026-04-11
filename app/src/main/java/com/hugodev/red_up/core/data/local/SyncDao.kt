package com.hugodev.red_up.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {

    @Query("SELECT * FROM sync_status WHERE id = 1")
    fun observeSyncStatus(): Flow<SyncStatusEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSyncStatus(status: SyncStatusEntity)

    @Query("SELECT COUNT(*) FROM pending_sync_events WHERE status = 'pending'")
    suspend fun pendingEventsCount(): Int

    @Query("SELECT * FROM pending_sync_events WHERE status = 'pending' ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPendingEvents(limit: Int = 100): List<PendingSyncEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingEvent(event: PendingSyncEventEntity)

    @Query("UPDATE pending_sync_events SET status = :status, attempts = attempts + 1, lastAttemptAt = :attemptAt WHERE id = :id")
    suspend fun updatePendingEventStatus(id: Long, status: String, attemptAt: Long)

    @Query("DELETE FROM pending_sync_events WHERE status = 'synced'")
    suspend fun deleteSyncedEvents()

    @Query("DELETE FROM pending_sync_events")
    suspend fun clearAllPendingEvents()

    @Query("SELECT * FROM notification_summary_cache WHERE id = 1")
    fun observeNotificationSummary(): Flow<NotificationSummaryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNotificationSummary(summary: NotificationSummaryEntity)
}
