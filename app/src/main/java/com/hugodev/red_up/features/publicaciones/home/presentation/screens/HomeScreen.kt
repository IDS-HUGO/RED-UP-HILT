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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
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
    onNavigateToChat: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToGroups: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showMessageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                title = { Text(text = "UPRed - Feed") },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMessageDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Mail, contentDescription = "Enviar mensaje")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (uiState.publicaciones.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "No hay publicaciones",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Publicaciones (${uiState.publicaciones.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                }

                items(uiState.publicaciones) { publication ->
                    PublicationCard(publication)
                }
            }
        }
    }

    // Diálogo para elegir tipo de mensaje
    if (showMessageDialog) {
        MessageTypeDialog(
            onDismiss = { showMessageDialog = false },
            onIndividual = { 
                showMessageDialog = false
                onNavigateToChat("direct", "Mensaje directo", "individual")
            },
            onGroup = {
                showMessageDialog = false
                onNavigateToGroups()
            }
        )
    }

    // Diálogo para confirmar logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onNavigateToLogin)
                    }
                ) {
                    Text("Sí, cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MessageTypeDialog(
    onDismiss: () -> Unit,
    onIndividual: () -> Unit,
    onGroup: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tipo de Mensaje") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("¿Qué tipo de mensaje deseas enviar?")
            }
        },
        confirmButton = {
            Button(onClick = onGroup) {
                Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Grupal")
            }
        },
        dismissButton = {
            Button(onClick = onIndividual) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Individual")
            }
        }
    )
}

@Composable
private fun PublicationCard(publication: Publications) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado: Autor y fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${publication.autorNombre} ${publication.autorApellido}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = publication.publicadaEn,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Título
            Text(
                text = publication.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido
            Text(
                text = publication.contenido,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pie: Reacciones y comentarios
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "❤️ ${publication.totalReacciones}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "💬 ${publication.totalComentarios}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
