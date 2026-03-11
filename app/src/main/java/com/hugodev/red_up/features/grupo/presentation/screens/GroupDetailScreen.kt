package com.hugodev.red_up.features.groups.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
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

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetail(groupId)
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
                        if (uiState.group != null) {
                            Text(
                                text = "${uiState.group!!.totalMiembros} miembro${if (uiState.group!!.totalMiembros != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                            Icon(
                                Icons.Default.PersonAdd,
                                contentDescription = "Agregar miembros",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = { onChatClick(groupId, uiState.group!!.nombre) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "Abrir chat",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = uiState.error ?: "Error desconocido",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                            Button(
                                onClick = { viewModel.loadGroupDetail(groupId) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
            uiState.group != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Group info
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Icono y nombre
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(64.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Groups,
                                                contentDescription = null,
                                                modifier = Modifier.size(36.dp),
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = uiState.group!!.nombre,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                if (uiState.group!!.descripcion.isNotEmpty()) {
                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Text(
                                        text = uiState.group!!.descripcion,
                                        style = MaterialTheme.typography.bodyLarge,
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoChip(
                                        label = "Privacidad",
                                        value = if (uiState.group!!.privacidad.equals("publico", true)) "Público" else "Privado",
                                        icon = if (uiState.group!!.privacidad.equals("publico", true)) Icons.Default.LockOpen else Icons.Default.Lock,
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoChip(
                                        label = "Miembros",
                                        value = uiState.group!!.totalMiembros.toString(),
                                        icon = Icons.Default.People,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Members section header
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Miembros",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                ) {
                                    Text(
                                        text = "${uiState.group!!.miembros.size}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.group!!.miembros) { member ->
                        MemberListItem(member = member)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun MemberListItem(member: GroupMember) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            if (member.fotoPerfilUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(member.fotoPerfilUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${member.nombre.firstOrNull()?.uppercase() ?: ""}${member.apellidoPaterno.firstOrNull()?.uppercase() ?: ""}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Member info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${member.nombre} ${member.apellidoPaterno}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = when (member.rolMiembro.lowercase()) {
                        "dueno" -> MaterialTheme.colorScheme.primary
                        "admin" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.tertiary
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (member.rolMiembro.lowercase()) {
                            "dueno" -> "DUEÑO"
                            "admin" -> "ADMIN"
                            else -> "MIEMBRO"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (member.rolMiembro.lowercase()) {
                            "dueno" -> MaterialTheme.colorScheme.onPrimary
                            "admin" -> MaterialTheme.colorScheme.onSecondary
                            else -> MaterialTheme.colorScheme.onTertiary
                        }
                    )
                }
            }

            // Status badge
            if (member.estadoMembresia == "pendiente") {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Pendiente",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
