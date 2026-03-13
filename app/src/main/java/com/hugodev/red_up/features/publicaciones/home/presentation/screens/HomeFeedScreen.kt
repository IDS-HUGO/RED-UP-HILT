package com.hugodev.red_up.features.home.presentation.screens

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hugodev.red_up.R
import com.hugodev.red_up.features.home.presentation.viewmodels.HomeViewModel
import com.hugodev.red_up.features.publications.domain.entities.Publications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToCreatePublication: () -> Unit = {},
    onNavigateToGroupChat: (Long, String) -> Unit = { _, _ -> },
    onNavigateToIndividualChat: (String, String) -> Unit = { _, _ -> },
    onNavigateToComments: (Long) -> Unit = {},
    onNavigateToQrScanner: () -> Unit = {} // Nuevo parámetro
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .drawBehind {
                                    drawCircle(color = Color.White, radius = size.minDimension / 4f)
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(Color.White, Color.White.copy(alpha = 0.1f), Color.Transparent),
                                            radius = size.minDimension / 2f
                                        ),
                                        radius = size.minDimension / 2f
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_redup_png_sf),
                                contentDescription = "Logo",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("RED-UP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Comunidad UP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    // BOTÓN DEL ESCÁNER QR
                    IconButton(onClick = onNavigateToQrScanner) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Escanear QR",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePublication,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (uiState.publicaciones.isEmpty()) {
                item { EmptyStateCard() }
            } else {
                items(uiState.publicaciones) { publication ->
                    PublicationCard(
                        publication = publication,
                        onCommentsClick = { onNavigateToComments(publication.id) }
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Deseas salir de la aplicación?") },
            confirmButton = {
                Button(onClick = { viewModel.logout(onNavigateToLogin) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Salir")
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun EmptyStateCard() {
    Card(modifier = Modifier.fillMaxWidth().padding(32.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Article, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text("No hay publicaciones", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PublicationCard(publication: Publications, onCommentsClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("${publication.autorNombre.firstOrNull() ?: ""}${publication.autorApellido.firstOrNull() ?: ""}", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${publication.autorNombre} ${publication.autorApellido}", fontWeight = FontWeight.Bold)
                    Text(publication.publicadaEn, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(publication.titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(publication.contenido, style = MaterialTheme.typography.bodyMedium)
            if (!publication.imagenUrl.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                AsyncImage(model = publication.imagenUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, null, tint = Color.Red) }
                IconButton(onClick = onCommentsClick) { Icon(Icons.Default.ChatBubbleOutline, null, tint = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}
