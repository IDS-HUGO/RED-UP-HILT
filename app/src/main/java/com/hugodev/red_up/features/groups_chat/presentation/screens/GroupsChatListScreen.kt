package com.hugodev.red_up.features.groups_chat.presentation.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.features.groups_chat.presentation.viewmodels.GroupsChatViewModel
import com.hugodev.red_up.features.groups_chat.presentation.viewmodels.GroupsUiState

data class GroupChatItem(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val totalMiembros: Int,
    val ultimoMensaje: String = "",
    val timestamp: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsChatListScreen(
    viewModel: GroupsChatViewModel = hiltViewModel(),
    onNavigateToGroupDetail: (String) -> Unit = {},
    onNavigateToChatScreen: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToCreateGroup: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMyGroups()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Grupos") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateGroup,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear grupo")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            if (uiState.grupos.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Sin grupos",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Crea o únete a grupos para empezar a chatear",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Mis Grupos (${uiState.grupos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                items(uiState.grupos) { grupo ->
                    GroupChatCard(
                        nombre = grupo.nombre,
                        descripcion = grupo.descripcion,
                        miembros = grupo.totalMiembros,
                        onClick = {
                            onNavigateToChatScreen(
                                grupo.id,
                                grupo.nombre,
                                "grupal"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupChatCard(
    nombre: String,
    descripcion: String,
    miembros: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "👥 $miembros",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (descripcion.isNotEmpty()) {
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}
