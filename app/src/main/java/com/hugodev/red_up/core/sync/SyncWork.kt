package com.hugodev.red_up.core.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object SyncWork {
    const val KEY_ACTION = "sync_action"
    const val ACTION_SYNC_TOKEN = "sync_token"
    const val ACTION_PERIODIC = "periodic"
    const val ACTION_FORCE = "force"

    private const val UNIQUE_TOKEN_WORK = "upred_sync_token"
    private const val UNIQUE_FORCE_SYNC_WORK = "upred_sync_force"
    private const val UNIQUE_PERIODIC_SYNC_WORK = "upred_sync_periodic"

    fun enqueueTokenSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints())
            .setInputData(workDataOf(KEY_ACTION to ACTION_SYNC_TOKEN))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(UNIQUE_TOKEN_WORK, ExistingWorkPolicy.REPLACE, request)
    }

    fun enqueueImmediateSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints())
            .setInputData(workDataOf(KEY_ACTION to ACTION_FORCE))
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(UNIQUE_FORCE_SYNC_WORK, ExistingWorkPolicy.REPLACE, request)
    }

    fun schedulePeriodicSync(context: Context) {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(networkConstraints())
            .setInputData(workDataOf(KEY_ACTION to ACTION_PERIODIC))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_SYNC_WORK,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun networkConstraints(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
}
