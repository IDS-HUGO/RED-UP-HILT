package com.hugodev.red_up.features.groups.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.hugodev.red_up.features.groups.domain.entities.GroupMember
import com.hugodev.red_up.features.groups.presentation.viewmodels.GroupDetailViewModel
import com.hugodev.red_up.features.profile.presentation.screens.ShowQrDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    onBackClick: () -> Unit,
    onInviteMembersClick: (Long) -> Unit,
    onChatClick: (Long, String) -> Unit,
    viewModel: GroupDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showQrDialog by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetail(groupId)
    }

    if (showQrDialog && uiState.group != null) {
        ShowQrDialog(
            title = "QR del Grupo",
            content = "GROUP-${uiState.group!!.id}",
            onDismiss = { showQrDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.group?.nombre ?: "Detalles del Grupo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // BOTÓN QR PARA EL GRUPO
                    if (uiState.group != null) {
                        IconButton(onClick = { showQrDialog = true }) {
                            Icon(Icons.Default.QrCode, contentDescription = "QR del grupo")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.group != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.canInviteMembers) {
                        FloatingActionButton(
                            onClick = { onInviteMembersClick(groupId) },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Agregar miembros")
                        }
                    }
                    FloatingActionButton(
                        onClick = { onChatClick(groupId, uiState.group!!.nombre) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "Abrir chat")
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.group != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Groups, null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = uiState.group!!.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                }
                                if (uiState.group!!.descripcion.isNotEmpty()) {
                                    Text(text = uiState.group!!.descripcion, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                    // Lista de miembros...
                    items(uiState.group!!.miembros) { member ->
                        MemberListItem(member = member)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(24.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onTertiaryContainer)
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Composable
fun MemberListItem(member: GroupMember) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = member.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "${member.nombre} ${member.apellidoPaterno}", fontWeight = FontWeight.Bold)
                Text(text = member.rolMiembro.uppercase(), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
