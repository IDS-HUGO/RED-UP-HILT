package com.hugodev.red_up

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.hugodev.red_up.core.ui.theme.RED_UPTheme
import com.hugodev.red_up.features.publications.presentation.screens.CreatePublicacionScreen
import com.hugodev.red_up.features.publications.presentation.screens.PublicacionesListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RED_UPTheme {
                var showCreate by rememberSaveable { mutableStateOf(false) }

                if (showCreate) {
                    CreatePublicacionScreen(
                        onNavigateBack = { showCreate = false },
                        onSuccess = { showCreate = false }
                    )
                } else {
                    PublicacionesListScreen(
                        onNavigateToCreate = { showCreate = true }
                    )
                }
            }
        }
    }
}
