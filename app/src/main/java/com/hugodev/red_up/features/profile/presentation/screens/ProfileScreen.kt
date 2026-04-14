package com.hugodev.red_up.features.profile.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.hugodev.red_up.core.ui.components.QrCodeView
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileViewModel
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToSyncStatus: () -> Unit,
    onNavigateToNotificationCenter: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    var showQrDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUserProfile()
    }

    if (showQrDialog && state.userProfile != null) {
        ShowQrDialog(
            title = "Mi Código QR",
            // Incluimos también el nombre para mejorar la experiencia al escanear
            content = "PROFILE-${state.userProfile!!.id}|${state.userProfile!!.nombre} ${state.userProfile!!.apellidoPaterno}",
            onDismiss = { showQrDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                // BOTÓN PARA MOSTRAR QR
                IconButton(onClick = { showQrDialog = true }) {
                    Icon(Icons.Filled.QrCode, contentDescription = "Mostrar QR")
                }
                IconButton(onClick = onNavigateToNotificationCenter) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Centro de notificaciones")
                }
                IconButton(onClick = onNavigateToSyncStatus) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Estado de sincronizacion")
                }
                IconButton(onClick = onNavigateToEditProfile) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar perfil")
                }
            }
        )

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.userProfile != null -> {
                ProfileContent(state = state, onFollowClick = {}, onUnfollowClick = {}, isMyProfile = true)
            }
        }
    }
}

@Composable
fun ShowQrDialog(title: String, content: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Genera el código QR real aquí
                QrCodeView(content = content, modifier = Modifier.size(220.dp))
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pide a un compañero que escanee este código para que te encuentre rápidamente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: Long,
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
        viewModel.loadUserStats(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.userProfile != null -> {
                ProfileContent(
                    state = state,
                    onFollowClick = { viewModel.followUser(userId) },
                    onUnfollowClick = { viewModel.unfollowUser(userId) },
                    isMyProfile = state.esMismoUsuario
                )
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "No se pudo cargar el perfil",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Perfil no encontrado",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
    isMyProfile: Boolean = false
) {
    val profile = state.userProfile ?: return
    val stats = state.userStats

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(100.dp).clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            if (!profile.fotoPerfil.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(profile.fotoPerfil),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = profile.nombre.firstOrNull()?.toString() ?: "",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "${profile.nombre} ${profile.apellidoPaterno}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = profile.correoInstitucional, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (profile.carrera != null) {
            Text(text = profile.carrera, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }

        if (!profile.biografia.isNullOrBlank() || !profile.telefono.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!profile.biografia.isNullOrBlank()) {
                    Text(
                        text = profile.biografia,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                if (!profile.telefono.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Telefono: ${profile.telefono}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (stats != null) {
            Row(
                modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("Publicaciones", stats.totalPublicaciones)
                Divider(modifier = Modifier.width(1.dp).height(40.dp))
                StatItem("Seguidores", stats.totalSeguidores)
                Divider(modifier = Modifier.width(1.dp).height(40.dp))
                StatItem("Siguiendo", stats.totalSiguiendo)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isMyProfile) {
            if (state.yaSegue) {
                Button(onClick = onUnfollowClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors()) {
                    Text("Dejar de seguir")
                }
            } else {
                Button(onClick = onFollowClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Seguir")
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
