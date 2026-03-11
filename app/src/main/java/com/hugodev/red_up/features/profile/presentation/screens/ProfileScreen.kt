package com.hugodev.red_up.features.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileViewModel
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileUiState

@Composable
fun MyProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUserProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        TopAppBar(
            title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = onNavigateToEditProfile) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                }
            }
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
            }
            state.userProfile != null -> {
                ProfileContent(
                    state = state,
                    onFollowClick = {},
                    onUnfollowClick = {},
                    isMyProfile = true
                )
            }
        }

        // Snackbar para mensajes de éxito
        if (state.success != null) {
            LaunchedEffect(state.success) {
                viewModel.clearMessages()
            }
        }
    }
}

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
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
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
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.nombre.firstOrNull()?.toString() ?: "",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre
        Text(
            text = "${profile.nombre} ${profile.apellidoPaterno}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // Email
        Text(
            text = profile.correoInstitucional,
            fontSize = 12.sp,
            color = Color.Gray
        )

        // Carrera
        if (profile.carrera != null) {
            Text(
                text = profile.carrera,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Biografía
        if (profile.biografia != null && profile.biografia.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = profile.biografia,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats
        if (stats != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("Publicaciones", stats.totalPublicaciones)
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                )
                StatItem("Seguidores", stats.totalSeguidores)
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                )
                StatItem("Siguiendo", stats.totalSiguiendo)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de acción
        if (!isMyProfile) {
            if (state.yaSegue) {
                Button(
                    onClick = onUnfollowClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Dejar de seguir")
                }
            } else {
                Button(
                    onClick = onFollowClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seguir")
                }
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
