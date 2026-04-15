package com.hugodev.red_up

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.core.sync.PushTokenRegistrar
import com.hugodev.red_up.core.sync.SyncWork
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class DemoHiltApp : Application() {
	override fun onCreate() {
		super.onCreate()
		SyncWork.schedulePeriodicSync(this)
		ensureFcmTokenAvailable()
	}

	private fun ensureFcmTokenAvailable() {
		FirebaseMessaging.getInstance().token
			.addOnSuccessListener { token ->
				Log.d("FCMTokenInit", "FCM token fetched on app start")
				CoroutineScope(Dispatchers.IO).launch {
					val authPreferences = AuthPreferences(applicationContext)
					authPreferences.saveFcmToken(token)
					PushTokenRegistrar.syncNow(
						context = applicationContext,
						reason = "app_start_fcm_fetch",
						explicitFcmToken = token
					)
					SyncWork.enqueueTokenSync(applicationContext)
				}
			}
			.addOnFailureListener { error ->
				Log.e("FCMTokenInit", "Failed to fetch FCM token on app start", error)
			}
	}
}