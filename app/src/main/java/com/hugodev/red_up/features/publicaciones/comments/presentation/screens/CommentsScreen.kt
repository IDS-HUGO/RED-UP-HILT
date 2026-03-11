package com.hugodev.red_up.features.publicaciones.comments.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hugodev.red_up.features.publicaciones.comments.presentation.viewmodels.CommentsViewModel
import com.hugodev.red_up.features.publicaciones.comments.presentation.viewmodels.Comment

@Composable
fun CommentsBottomSheetContent(
    publicacionId: Long,
    viewModel: CommentsViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.commentsState.collectAsState()
    var nuevoComentario by remember { mutableStateOf("") }

    LaunchedEffect(publicacionId) {
        viewModel.loadComments(publicacionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comentarios (${state.totalComentarios})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        Divider()

        // Lista de comentarios
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(state.comentarios) { comentario ->
                CommentItem(
                    comment = comentario,
                    onDelete = { viewModel.deleteComment(publicacionId, comentario.id) }
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (state.error != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
            }
        }

        Divider()

        // Campo de entrada de comentario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nuevoComentario,
                onValueChange = { nuevoComentario = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Escribe un comentario...") },
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (nuevoComentario.isNotEmpty()) {
                        viewModel.addComment(publicacionId, nuevoComentario)
                        nuevoComentario = ""
                    }
                },
                enabled = nuevoComentario.isNotEmpty() && !state.estaEscribiendo
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    onDelete: () -> Unit,
    onReply: () -> Unit = {}
) {
    var mostrarOpciones by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = comment.usuarioNombre.firstOrNull()?.toString() ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Info del usuario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${comment.usuarioNombre} ${comment.usuarioApellido}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = comment.creadoEn,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Opciones
            Box {
                IconButton(
                    onClick = { mostrarOpciones = !mostrarOpciones },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = mostrarOpciones,
                    onDismissRequest = { mostrarOpciones = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        onClick = {
                            onDelete()
                            mostrarOpciones = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Contenido del comentario
        Text(
            text = comment.contenido,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 48.dp)
        )

        // Respuestas si las hay
        if (comment.respuestas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { /* Mostrar respuestas */ },
                modifier = Modifier.padding(start = 48.dp)
            ) {
                Text(
                    text = "Ver ${comment.respuestas.size} respuesta${if (comment.respuestas.size > 1) "s" else ""}",
                    fontSize = 12.sp
                )
            }
        }
    }

    Divider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    publicacionId: Long,
    viewModel: CommentsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.commentsState.collectAsState()
    var nuevoComentario by remember { mutableStateOf("") }

    LaunchedEffect(publicacionId) {
        viewModel.loadComments(publicacionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        TopAppBar(
            title = { Text("Comentarios (${state.totalComentarios})", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        )

        // Lista de comentarios
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(state.comentarios) { comentario ->
                CommentItem(
                    comment = comentario,
                    onDelete = { viewModel.deleteComment(publicacionId, comentario.id) }
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        Divider()

        // Campo de entrada de comentario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nuevoComentario,
                onValueChange = { nuevoComentario = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Escribe un comentario...") },
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (nuevoComentario.isNotEmpty()) {
                        viewModel.addComment(publicacionId, nuevoComentario)
                        nuevoComentario = ""
                    }
                },
                enabled = nuevoComentario.isNotEmpty() && !state.estaEscribiendo
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar comentario")
            }
        }
    }
}
