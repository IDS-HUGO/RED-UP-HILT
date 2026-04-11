package com.hugodev.red_up.features.home.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    onNavigateToComments: (Long) -> Unit = {},
    onNavigateToQrScanner: () -> Unit = {},
    onNavigateToUserProfile: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadFeed() }
    LifecycleEventEffect(Lifecycle.Event.ON_START) { viewModel.connectSocket() }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) { viewModel.disconnectSocket() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.img_redup_png_sf), contentDescription = null, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RED-UP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToQrScanner) { Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary) }
                    IconButton(onClick = { showLogoutDialog = true }) { Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error) }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePublication, containerColor = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.publicaciones) { pub ->
                PublicationCard(
                    publication = pub,
                    onCommentsClick = { onNavigateToComments(pub.id) },
                    onAuthorClick = { onNavigateToUserProfile(pub.autorId) }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Salir") },
            text = { Text("¿Cerrar sesión?") },
            confirmButton = { Button(onClick = { viewModel.logout(onNavigateToLogin) }) { Text("Sí") } },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("No") } }
        )
    }
}

@Composable
private fun PublicationCard(
    publication: Publications,
    onCommentsClick: () -> Unit,
    onAuthorClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onAuthorClick)
            ) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) { Text(publication.autorNombre.take(1)) }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("${publication.autorNombre} ${publication.autorApellido}", fontWeight = FontWeight.Bold)
                    Text(publication.publicadaEn, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(publication.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(publication.contenido, style = MaterialTheme.typography.bodyMedium)
            if (!publication.imagenUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(model = publication.imagenUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                    Text(" ${publication.totalReacciones}", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onCommentsClick() }) {
                    Icon(Icons.Default.ChatBubbleOutline, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(" ${publication.totalComentarios}", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
