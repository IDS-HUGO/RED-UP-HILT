package com.hugodev.red_up.features.notifications.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.features.notifications.presentation.viewmodels.NotificationSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de Notificaciones") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SwitchSetting(
                title = "Notificaciones Push",
                subtitle = "Recibir notificaciones push",
                checked = uiState.pushEnabled,
                onCheckedChange = viewModel::updatePushEnabled
            )
            SwitchSetting(
                title = "Chats",
                subtitle = "Notificaciones de mensajes",
                checked = uiState.chatEnabled,
                onCheckedChange = viewModel::updateChatEnabled
            )
            SwitchSetting(
                title = "Grupos",
                subtitle = "Notificaciones de grupos",
                checked = uiState.groupsEnabled,
                onCheckedChange = viewModel::updateGroupsEnabled
            )
            SwitchSetting(
                title = "Social",
                subtitle = "Notificaciones sociales",
                checked = uiState.socialEnabled,
                onCheckedChange = viewModel::updateSocialEnabled
            )
        }
    }
}

@Composable
private fun SwitchSetting(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}