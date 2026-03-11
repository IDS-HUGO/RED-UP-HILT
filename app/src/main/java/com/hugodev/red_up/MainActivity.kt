package com.hugodev.red_up

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.hugodev.red_up.core.ui.theme.RED_UPTheme
import com.hugodev.red_up.navigation.NavigationGraph
import com.hugodev.red_up.navigation.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RED_UPTheme {
                val navController = rememberNavController()
                NavigationGraph(
                    navController = navController,
                    navigationManager = navigationManager
                )
            }
        }
    }
}
