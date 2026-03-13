package com.hugodev.red_up.features.chat.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.features.chat.domain.entities.ChatMessage
import com.hugodev.red_up.features.chat.presentation.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomId: String,
    roomName: String,
    roomType: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.connectToChat() }
    LaunchedEffect(isConnected) { if (isConnected) viewModel.joinRoom(roomId, roomName, roomType) }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Groups, null, modifier = Modifier.size(22.dp)) }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = roomName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = if (isConnected) "Conectado" else "Desconectado", style = MaterialTheme.typography.labelSmall, color = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // Elimina espacios automáticos
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.ime) // Pega al teclado
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)), // Respeta barra de gestos
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                MessageInput(
                    message = uiState.newMessage,
                    onMessageChange = viewModel::updateMessage,
                    onSendClick = viewModel::sendMessage,
                    isConnected = isConnected,
                    isConnectingToRoom = uiState.currentRoomId.isNullOrBlank()
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.messages) { message ->
                MessageItem(message = message, isOwnMessage = message.senderId == uiState.currentUserId)
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage, isOwnMessage: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isOwnMessage) Arrangement.End else Arrangement.Start) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(16.dp, 16.dp, if (isOwnMessage) 16.dp else 4.dp, if (isOwnMessage) 4.dp else 16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isOwnMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = if (isOwnMessage) "TÚ" else message.senderName ?: "Usuario", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = message.message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun MessageInput(message: String, onMessageChange: (String) -> Unit, onSendClick: () -> Unit, isConnected: Boolean, isConnectingToRoom: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Escribe algo...") },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )
        IconButton(onClick = onSendClick, enabled = message.isNotBlank() && isConnected && !isConnectingToRoom, modifier = Modifier.background(if (message.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
        }
    }
}

private fun formatTimestamp(timestamp: String): String = ""
