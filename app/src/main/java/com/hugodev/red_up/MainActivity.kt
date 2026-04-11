package com.hugodev.red_up

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.hugodev.red_up.core.data.AuthPreferences
import com.hugodev.red_up.core.ui.theme.RED_UPTheme
import com.hugodev.red_up.navigation.NavigationGraph
import com.hugodev.red_up.navigation.NavigationManager
import com.hugodev.red_up.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var authPreferences: AuthPreferences

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar el control manual de insets para que adjustResize funcione mejor con Compose
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()

        val hasSession = runBlocking {
            !authPreferences.tokenFlow.firstOrNull().isNullOrBlank()
        }
        val startDestination = if (hasSession) Screen.Main.route else Screen.Login.route

        setContent {
            RED_UPTheme {
                val navController = rememberNavController()
                NavigationGraph(
                    navController = navController,
                    navigationManager = navigationManager,
                    startDestination = startDestination
                )
            }
        }

        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
