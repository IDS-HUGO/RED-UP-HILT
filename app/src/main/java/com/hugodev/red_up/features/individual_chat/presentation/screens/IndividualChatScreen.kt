package com.hugodev.red_up.features.individual_chat.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.features.chat.presentation.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    userId: String,
    userName: String,
    userEmail: String,
    viewModel: ChatViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Join to sala directa
    LaunchedEffect(userId) {
        viewModel.joinRoom(
            roomId = userId,
            roomName = userName,
            roomType = "directo"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(userName)
                        Text(
                            text = if (uiState.isConnected) "Conectado" else "Conectando...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ===============================
            // LISTA DE MENSAJES REALES
            // ===============================
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {

                items(uiState.messages) { message ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(
                                text = "De: ${message.senderId}"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = message.message
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = message.timestamp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // ===============================
            // INPUT DE MENSAJE
            // ===============================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                OutlinedTextField(
                    value = uiState.newMessage,
                    onValueChange = { viewModel.updateMessage(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.sendMessage()
                    }
                ) {
                    Text("Enviar")
                }
            }

            // ===============================
            // ERROR SI EXISTE
            // ===============================
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    viewModel.clearError()
                }

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}