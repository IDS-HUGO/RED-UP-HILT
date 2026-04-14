package com.hugodev.red_up.features.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.profileState.collectAsState()
    var biografia by remember { mutableStateOf(state.userProfile?.biografia ?: "") }
    var telefono by remember { mutableStateOf(state.userProfile?.telefono ?: "") }
    var fotoUrl by remember { mutableStateOf(state.userProfile?.fotoPerfil ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        TopAppBar(
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        text = (state.userProfile?.nombre?.firstOrNull()?.toString() ?: ""),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre (solo lectura)
            if (state.userProfile != null) {
                Text(
                    text = "${state.userProfile!!.nombre} ${state.userProfile!!.apellidoPaterno}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "(No se puede cambiar)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Biografía
            OutlinedTextField(
                value = biografia,
                onValueChange = { biografia = it },
                label = { Text("Biografía") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                placeholder = { Text("Cuéntanos sobre ti...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tu número de teléfono") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Foto URL
            OutlinedTextField(
                value = fotoUrl,
                onValueChange = { fotoUrl = it },
                label = { Text("URL de foto de perfil") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://ejemplo.com/foto.jpg") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones
            Button(
                onClick = {
                    isSaving = true
                    viewModel.updateProfile(
                        biography = biografia.takeIf { it.isNotEmpty() },
                        telefono = telefono.takeIf { it.isNotEmpty() },
                        fotoUrl = fotoUrl.takeIf { it.isNotEmpty() }
                    )
                    isSaving = false
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Guardar cambios")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }

            // Mensajes de error
            if (state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
