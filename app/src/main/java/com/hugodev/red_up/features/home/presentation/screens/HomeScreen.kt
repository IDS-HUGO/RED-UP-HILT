package com.hugodev.red_up.features.home.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.features.home.presentation.viewmodels.HomeViewModel
import com.hugodev.red_up.features.publications.domain.entities.Publications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToChat: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var toUserId by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.connectSocket()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.disconnectSocket()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "UPRed") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                ConnectionCard(
                    isConnected = uiState.isSocketConnected,
                    userId = uiState.userId
                )
            }

            item {
                ChatQuickAccess(onNavigateToChat = onNavigateToChat)
            }

            item {
                MessageComposer(
                    toUserId = toUserId,
                    messageText = messageText,
                    onToUserChange = { toUserId = it },
                    onMessageChange = { messageText = it },
                    onSend = {
                        viewModel.sendMessage(toUserId, messageText)
                        messageText = ""
                    }
                )
            }

            if (uiState.mensajes.isNotEmpty()) {
                item {
                    Text(
                        text = "Mensajes recientes",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(uiState.mensajes) { message ->
                    MessageCard(message)
                }
            }

            item {
                Text(
                    text = "Anuncios",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(uiState.publicaciones) { publication ->
                PublicationCard(publication)
            }
        }
    }
}

@Composable
private fun ChatQuickAccess(
    onNavigateToChat: (String, String, String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Chat en Tiempo Real",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Únete a conversaciones grupales",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Button(
                onClick = { onNavigateToChat("grupo_carrera", "Grupo Carrera", "grupal") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Groups, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Ir a Chat Grupal")
            }
        }
    }
}

@Composable
private fun ConnectionCard(isConnected: Boolean, userId: String?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "WebSocket", style = MaterialTheme.typography.titleSmall)
            Text(
                text = if (isConnected) "Conectado" else "Desconectado",
                color = if (isConnected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            if (!userId.isNullOrBlank()) {
                Text(text = "Usuario: $userId", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun MessageComposer(
    toUserId: String,
    messageText: String,
    onToUserChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Enviar mensaje", style = MaterialTheme.typography.titleSmall)

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = toUserId,
                onValueChange = onToUserChange,
                label = { Text(text = "Destino (user_id o group_id)") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = messageText,
                    onValueChange = onMessageChange,
                    label = { Text(text = "Mensaje") }
                )

                IconButton(onClick = onSend) {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun MessageCard(message: ChatMessage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "De: ${message.senderId}", fontWeight = FontWeight.SemiBold)
            Text(text = message.message)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = message.timestamp, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PublicationCard(publication: Publications) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = publication.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = publication.contenido)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${publication.autorNombre} ${publication.autorApellido}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = publication.publicadaEn,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Comentarios: ${publication.totalComentarios}  Reacciones: ${publication.totalReacciones}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
