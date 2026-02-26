package com.hugodev.red_up.features.individual_chat.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualChatScreen(
    userId: String,
    userName: String,
    userEmail: String
) {

    var message by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userName) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // Mensajes (temporalmente fake)
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(
                    listOf(
                        "Hola 👋",
                        "¿Cómo estás?",
                        "Este será el chat real pronto 🔥"
                    )
                ) { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = msg,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Input mensaje
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        // Aquí luego conectaremos con ViewModel
                        message = ""
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}