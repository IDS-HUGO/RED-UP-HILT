package com.hugodev.red_up.features.notifications.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración de Notificaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando configuración...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            uiState.error ?: "Error desconocido",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadSettings() }) {
                            Text("Reintentar")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Tipos de Notificaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        item {
                            NotificationSettingItem(
                                title = "Me gusta",
                                subtitle = "Notificaciones cuando alguien le da me gusta a tus publicaciones",
                                checked = uiState.settings.likes,
                                onCheckedChange = { viewModel.updateSetting("likes", it) }
                            )
                        }

                        item {
                            NotificationSettingItem(
                                title = "Comentarios",
                                subtitle = "Notificaciones cuando alguien comenta en tus publicaciones",
                                checked = uiState.settings.comments,
                                onCheckedChange = { viewModel.updateSetting("comments", it) }
                            )
                        }

                        item {
                            NotificationSettingItem(
                                title = "Seguidores",
                                subtitle = "Notificaciones cuando alguien te sigue",
                                checked = uiState.settings.follows,
                                onCheckedChange = { viewModel.updateSetting("follows", it) }
                            )
                        }

                        item {
                            NotificationSettingItem(
                                title = "Mensajes",
                                subtitle = "Notificaciones de nuevos mensajes en el chat",
                                checked = uiState.settings.messages,
                                onCheckedChange = { viewModel.updateSetting("messages", it) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Preferencias de Notificación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        item {
                            NotificationSettingItem(
                                title = "Sonido",
                                subtitle = "Reproducir sonido para notificaciones",
                                checked = uiState.settings.soundEnabled,
                                onCheckedChange = { viewModel.updateSetting("sound", it) }
                            )
                        }

                        item {
                            NotificationSettingItem(
                                title = "Vibración",
                                subtitle = "Vibrar el dispositivo para notificaciones",
                                checked = uiState.settings.vibrationEnabled,
                                onCheckedChange = { viewModel.updateSetting("vibration", it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}