package com.hugodev.red_up.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hugodev.red_up.MainActivity
import com.hugodev.red_up.R
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.core.sync.PushTokenRegistrar
import com.hugodev.red_up.core.sync.SyncWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpRedFirebaseMessagingService : FirebaseMessagingService() {
    private companion object {
        const val TAG = "UpRedFCM"
        const val GROUP_KEY_UPRED = "upred_group"
        const val CHANNEL_ID = "upred_push"
        const val SUMMARY_ID = 1
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            AuthPreferences(applicationContext).saveFcmToken(token)
            PushTokenRegistrar.syncNow(
                context = applicationContext,
                reason = "firebase_on_new_token",
                explicitFcmToken = token
            )
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

        Log.d(TAG, "Push received. dataKeys=${remoteMessage.data.keys}")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NotificationDeepLink.KEY_TARGET_TYPE, remoteMessage.data[NotificationDeepLink.KEY_TARGET_TYPE])
            putExtra(NotificationDeepLink.KEY_ROOM_ID, remoteMessage.data[NotificationDeepLink.KEY_ROOM_ID])
            putExtra(NotificationDeepLink.KEY_ROOM_NAME, remoteMessage.data[NotificationDeepLink.KEY_ROOM_NAME])
            putExtra(NotificationDeepLink.KEY_ROOM_TYPE, remoteMessage.data[NotificationDeepLink.KEY_ROOM_TYPE])
            putExtra(NotificationDeepLink.KEY_USER_ID, remoteMessage.data[NotificationDeepLink.KEY_USER_ID])
            putExtra(NotificationDeepLink.KEY_FOLLOWER_USER_ID, remoteMessage.data[NotificationDeepLink.KEY_FOLLOWER_USER_ID])
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

        val notificationId = stableNotificationId(remoteMessage)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(GROUP_KEY_UPRED)
            .setOnlyAlertOnce(true)
            .build()

        val manager = NotificationManagerCompat.from(this)
        manager.notify(notificationId, notification)

        // Notificación resumen para agrupar y evitar que se trabe la barra
        val summary = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("UPRed")
            .setContentText("Tienes nuevas notificaciones")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setGroup(GROUP_KEY_UPRED)
            .setGroupSummary(true)
            .setOnlyAlertOnce(true)
            .build()
        manager.notify(SUMMARY_ID, summary)
    }

    private fun stableNotificationId(remoteMessage: RemoteMessage): Int {
        val targetType = remoteMessage.data[NotificationDeepLink.KEY_TARGET_TYPE].orEmpty()
        val publicationId = remoteMessage.data[NotificationDeepLink.KEY_PUBLICATION_ID].orEmpty()
        val followerId = remoteMessage.data[NotificationDeepLink.KEY_FOLLOWER_USER_ID].orEmpty()
        val roomId = remoteMessage.data[NotificationDeepLink.KEY_ROOM_ID].orEmpty()
        val raw = listOf(targetType, publicationId, followerId, roomId).joinToString("|")
        return (raw.hashCode() and 0x7fffffff).takeIf { it != 0 } ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
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

}
