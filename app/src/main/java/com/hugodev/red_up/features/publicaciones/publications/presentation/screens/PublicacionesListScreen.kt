package com.hugodev.red_up.features.publications.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hugodev.red_up.features.publications.domain.entities.Publications
import com.hugodev.red_up.features.publications.presentation.viewmodels.PublicacionesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionesListScreen(
    onCreateClick: () -> Unit,
    viewModel: PublicacionesListViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUserId = uiState.currentUserId ?: 0L
    val tabs = listOf("Latest", "Top Rated", "Remote Only", "Internships")
    var selectedTab by remember { mutableIntStateOf(0) }
    var publicacionAEliminar by remember { mutableStateOf<Publications?>(null) }

    // Debug: Log para verificar IDs
    LaunchedEffect(currentUserId, uiState.publicaciones) {
        android.util.Log.d("PublicacionesScreen", "CurrentUserId: $currentUserId")
        uiState.publicaciones.forEach { pub ->
            android.util.Log.d("PublicacionesScreen", "Publicacion ${pub.id}: autorId=${pub.autorId}, isOwner=${pub.autorId == currentUserId}")
        }
    }

    LaunchedEffect(Unit) { viewModel.loadPublicaciones() }

    // Dialogo confirmar eliminacion
    publicacionAEliminar?.let { pub ->
        AlertDialog(
            onDismissRequest = { publicacionAEliminar = null },
            title = { Text("Eliminar publicación") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePublicacion(pub.id)
                    publicacionAEliminar = null
                }) { Text("Eliminar", color = colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { publicacionAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    // Dialogo editar
    if (uiState.mostrarDialogoEditar) {
        uiState.publicacionAEditar?.let { pub ->
            EditarPublicacionDialog(
                publicacion = pub,
                onDismiss = { viewModel.ocultarDialogoEditar() },
                onConfirm = { titulo, contenido ->
                    viewModel.editarPublicacion(pub.id, titulo, contenido)
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── TopBar ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Red UP",
                    color = colorScheme.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Tabs ──────────────────────────────────────────────────────────
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = colorScheme.background,
                contentColor = colorScheme.primary,
                edgePadding = 0.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) colorScheme.primary else colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // ── Contenido ─────────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }
                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.error ?: "",
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { viewModel.loadPublicaciones() },
                                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                            ) { Text("Reintentar") }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.publicaciones) { publicacion ->
                            PublicacionCard(
                                publicacion = publicacion,
                                isOwner = publicacion.autorId == currentUserId,
                                onDeleteClick = { publicacionAEliminar = publicacion },
                                onEditClick = { viewModel.mostrarDialogoEditar(publicacion) }
                            )
                        }
                    }
                }
            }
        }

        // ── FAB ───────────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick = onCreateClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
        }
    }
}

// ── Card de publicación ───────────────────────────────────────────────────────
@Composable
fun PublicacionCard(
    publicacion: Publications,
    isOwner: Boolean,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var liked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableIntStateOf(publicacion.totalReacciones) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar con iniciales
                val iniciales = buildString {
                    publicacion.autorNombre.firstOrNull()?.let { append(it) }
                    publicacion.autorApellido.firstOrNull()?.let { append(it) }
                }.uppercase().take(2)

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF607D8B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales.ifEmpty { "?" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${publicacion.autorNombre} ${publicacion.autorApellido}".trim(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = publicacion.publicadaEn,
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                // Tag audiencia
                if (publicacion.audiencia.isNotEmpty()) {
                    val tagColor = when (publicacion.audiencia.uppercase()) {
                        "ENGINEERING" -> colorScheme.secondaryContainer
                        "LAW" -> colorScheme.tertiaryContainer
                        "BUSINESS" -> colorScheme.primaryContainer
                        else -> colorScheme.surfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(tagColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = publicacion.audiencia.uppercase(),
                            fontSize = 11.sp,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text = publicacion.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Contenido ─────────────────────────────────────────────────────
            Text(
                text = publicacion.contenido,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            if (!publicacion.imagenUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = publicacion.imagenUrl,
                    contentDescription = "Imagen de publicación",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Footer ────────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Like
                IconButton(
                    onClick = {
                        liked = !liked
                        likesCount = if (liked) likesCount + 1 else likesCount - 1
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        tint = if (liked) colorScheme.primary else colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = likesCount.toString(),
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 2.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Comentarios
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comentarios",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = publicacion.totalComentarios.toString(),
                    fontSize = 13.sp,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Editar y Eliminar (solo propietario)
                if (isOwner) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Botón acción
                TextButton(onClick = {}) {
                    Text(
                        text = "Read more >",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ── Diálogo editar ────────────────────────────────────────────────────────────
@Composable
fun EditarPublicacionDialog(
    publicacion: Publications,
    onDismiss: () -> Unit,
    onConfirm: (titulo: String, contenido: String) -> Unit
) {
    var titulo by remember { mutableStateOf(publicacion.titulo) }
    var contenido by remember { mutableStateOf(publicacion.contenido) }

    Dialog(onDismissRequest = onDismiss) {
        val colorScheme = MaterialTheme.colorScheme
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Editar publicación",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    minLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(titulo, contenido) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                    ) { Text("Guardar") }
                }
            }
        }
    }
}
