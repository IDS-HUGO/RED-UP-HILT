package com.hugodev.red_up.core.sync

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hugodev.red_up.BuildConfig
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.core.data.local.AppDatabase
import com.hugodev.red_up.core.data.local.NotificationSummaryEntity
import com.hugodev.red_up.core.data.local.PendingSyncEventEntity
import com.hugodev.red_up.core.data.local.SyncStatusEntity
import com.hugodev.red_up.features.publications.data.datasources.remote.api.UpRedApi
import com.hugodev.red_up.features.publications.data.datasources.remote.models.DeviceRegistrationRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.DeviceTokenUpdateRequestDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.SyncEventDto
import com.hugodev.red_up.features.publications.data.datasources.remote.models.SyncEventsRequestDto
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val gson = Gson()

    override suspend fun doWork(): Result {
        val action = inputData.getString(SyncWork.KEY_ACTION) ?: SyncWork.ACTION_PERIODIC

        return try {
            val authPreferences = AuthPreferences(applicationContext)
            val token = authPreferences.tokenFlow.firstOrNull()
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "upred_local.db")
                .fallbackToDestructiveMigration()
                .build()
            val syncDao = db.syncDao()

            if (token.isNullOrBlank()) {
                syncDao.upsertSyncStatus(
                    SyncStatusEntity(
                        lastSyncAt = System.currentTimeMillis(),
                        pendingCount = syncDao.pendingEventsCount(),
                        lastError = null
                    )
                )
                return Result.success()
            }

            val api = buildApi(token)

            when (action) {
                SyncWork.ACTION_SYNC_TOKEN -> syncFcmToken(api, authPreferences)
                SyncWork.ACTION_FORCE, SyncWork.ACTION_PERIODIC -> {
                    syncFcmToken(api, authPreferences)
                    syncRemoteConfig(api)
                    syncNotificationSummary(api, syncDao)
                    flushPendingEvents(api, syncDao)
                }
            }

            syncDao.upsertSyncStatus(
                SyncStatusEntity(
                    lastSyncAt = System.currentTimeMillis(),
                    pendingCount = syncDao.pendingEventsCount(),
                    lastError = null
                )
            )
            Result.success()
        } catch (e: Exception) {
            val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "upred_local.db")
                .fallbackToDestructiveMigration()
                .build()
            val syncDao = db.syncDao()
            syncDao.upsertSyncStatus(
                SyncStatusEntity(
                    lastSyncAt = System.currentTimeMillis(),
                    pendingCount = syncDao.pendingEventsCount(),
                    lastError = e.message ?: "Error de sincronizacion"
                )
            )
            Result.retry()
        }
    }

    private fun buildApi(token: String): UpRedApi {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_UPRED)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(UpRedApi::class.java)
    }

    private suspend fun syncFcmToken(api: UpRedApi, authPreferences: AuthPreferences) {
        val fcmToken = authPreferences.fcmTokenFlow.firstOrNull()
        if (fcmToken.isNullOrBlank()) return

        val deviceUuid = authPreferences.getOrCreateDeviceUuid()

        api.registerDevice(
            DeviceRegistrationRequestDto(
                uuidDispositivo = deviceUuid,
                plataforma = "android",
                tokenPush = fcmToken
            )
        )

        api.updateDeviceToken(
            DeviceTokenUpdateRequestDto(
                uuidDispositivo = deviceUuid,
                tokenPush = fcmToken
            )
        )
    }

    private suspend fun syncRemoteConfig(api: UpRedApi) {
        val remoteConfig = api.getNotificationConfig()
        api.updateNotificationConfig(remoteConfig)
    }

    private suspend fun syncNotificationSummary(api: UpRedApi, syncDao: com.hugodev.red_up.core.data.local.SyncDao) {
        val summary = api.getNotificationSummary()
        syncDao.upsertNotificationSummary(
            NotificationSummaryEntity(
                unreadCount = summary.totalNoLeidas,
                lastFetchedAt = System.currentTimeMillis(),
                source = "remote"
            )
        )
    }

    private suspend fun flushPendingEvents(api: UpRedApi, syncDao: com.hugodev.red_up.core.data.local.SyncDao) {
        val pending = syncDao.getPendingEvents(limit = 100)
        if (pending.isEmpty()) return

        val events = pending.mapNotNull { event -> event.toSyncEventDto() }
        if (events.isEmpty()) return

        api.syncDeferredEvents(SyncEventsRequestDto(events))

        val now = System.currentTimeMillis()
        pending.forEach {
            syncDao.updatePendingEventStatus(it.id, "synced", now)
        }
        syncDao.deleteSyncedEvents()
    }

    private fun PendingSyncEventEntity.toSyncEventDto(): SyncEventDto? {
        return try {
            val mapType = object : TypeToken<Map<String, Any?>>() {}.type
            val payload: Map<String, Any?> = gson.fromJson(payloadJson, mapType)
            SyncEventDto(eventType = eventType, payload = payload, createdAt = createdAt)
        } catch (_: Exception) {
            null
        }
    }
}
