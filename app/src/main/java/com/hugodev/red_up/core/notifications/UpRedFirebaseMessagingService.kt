package com.hugodev.red_up.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hugodev.red_up.MainActivity
import com.hugodev.red_up.R
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.core.sync.SyncWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpRedFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            AuthPreferences(applicationContext).saveFcmToken(token)
            SyncWork.enqueueTokenSync(applicationContext)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        createChannelIfNeeded()

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "UPRed"
        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "Tienes una nueva notificacion"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NotificationDeepLink.KEY_TARGET_TYPE, remoteMessage.data[NotificationDeepLink.KEY_TARGET_TYPE])
            putExtra(NotificationDeepLink.KEY_ROOM_ID, remoteMessage.data[NotificationDeepLink.KEY_ROOM_ID])
            putExtra(NotificationDeepLink.KEY_ROOM_NAME, remoteMessage.data[NotificationDeepLink.KEY_ROOM_NAME])
            putExtra(NotificationDeepLink.KEY_ROOM_TYPE, remoteMessage.data[NotificationDeepLink.KEY_ROOM_TYPE])
            remoteMessage.data[NotificationDeepLink.KEY_PUBLICATION_ID]?.toLongOrNull()?.let {
                putExtra(NotificationDeepLink.KEY_PUBLICATION_ID, it)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "UPRed Notificaciones",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de mensajes, grupos y actividad social"
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "upred_push"
    }
}
