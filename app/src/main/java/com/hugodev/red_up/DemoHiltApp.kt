package com.hugodev.red_up

import android.app.Application
import com.hugodev.red_up.core.sync.SyncWork
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoHiltApp : Application() {
	override fun onCreate() {
		super.onCreate()
		SyncWork.schedulePeriodicSync(this)
	}
}