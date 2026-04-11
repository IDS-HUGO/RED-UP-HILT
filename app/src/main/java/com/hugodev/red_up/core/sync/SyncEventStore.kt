package com.hugodev.red_up.core.sync

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.hugodev.red_up.core.data.local.AppDatabase
import com.hugodev.red_up.core.data.local.PendingSyncEventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SyncEventStore {

    private val gson = Gson()

    suspend fun queueEvent(context: Context, eventType: String, payload: Map<String, Any?>) {
        withContext(Dispatchers.IO) {
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "upred_local.db")
                .fallbackToDestructiveMigration()
                .build()

            db.syncDao().insertPendingEvent(
                PendingSyncEventEntity(
                    eventType = eventType,
                    payloadJson = gson.toJson(payload)
                )
            )
        }
    }
}
