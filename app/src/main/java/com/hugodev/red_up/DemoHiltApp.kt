package com.hugodev.red_up

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.hugodev.red_up.core.sync.SyncWork
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DemoHiltApp : MultiDexApplication() {
	override fun onCreate() {
		super.onCreate()
		SyncWork.schedulePeriodicSync(this)
	}
}