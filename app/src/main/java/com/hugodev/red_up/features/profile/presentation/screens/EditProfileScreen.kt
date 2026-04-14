package com.hugodev.red_up.features.profile.presentation.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.hugodev.red_up.core.utils.compressImageForUpload
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val tag = "EditProfileImageUpload"
    val state by viewModel.profileState.collectAsState()
    val profile = state.userProfile
    var localError by remember { mutableStateOf<String?>(null) }
    var biografia by remember { mutableStateOf(profile?.biografia ?: "") }
    var telefono by remember { mutableStateOf(profile?.telefono ?: "") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(state.success) {
        if (!state.success.isNullOrBlank()) {
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                currentPhotoUri?.let {
                    Log.d(tag, "Camera image captured uri=$it")
                    fotoUri = it
                    localError = null
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                Log.d(tag, "Gallery image selected uri=$it")
                fotoUri = it
                localError = null
            }
        }
    )

    fun launchCamera() {
        val photoFile = File(context.externalCacheDir ?: context.cacheDir, "profile_edit_${System.currentTimeMillis()}.jpg")
        currentPhotoUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        val uri = currentPhotoUri
        if (uri != null) {
            cameraLauncher.launch(uri)
        }
    }

    fun launchGallery() {
        galleryLauncher.launch("image/*")
    }

    fun createMultipartBodyPart(uri: Uri): MultipartBody.Part? {
        return try {
            val bytes = compressImageForUpload(context, uri) ?: return null
            Log.d(tag, "Compressed image uri=$uri bytes=${bytes.size}")
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("foto_perfil", "profile_image.jpg", requestBody)
        } catch (e: Exception) {
            Log.e(tag, "Error creating multipart uri=$uri", e)
            null
        }
    }

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
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { /* TODO: show options */ },
                contentAlignment = Alignment.Center
            ) {
                if (fotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(fotoUri),
                        contentDescription = "Foto de perfil seleccionada",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (profile?.fotoPerfil != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profile.fotoPerfil),
                        contentDescription = "Foto de perfil actual",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (profile?.nombre?.firstOrNull()?.toString() ?: ""),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { launchCamera() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = "Tomar foto",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cámara")
                }

                Button(
                    onClick = { launchGallery() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Elegir de galería",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Galería")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre (solo lectura)
            if (profile != null) {
                Text(
                    text = "${profile.nombre} ${profile.apellidoPaterno}",
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

            // Botones
            Button(
                onClick = {
                    val fotoPart = fotoUri?.let { createMultipartBodyPart(it) }
                    if (fotoUri != null && fotoPart == null) {
                        Log.e(tag, "Multipart creation failed for selected uri=$fotoUri")
                        localError = "No se pudo procesar la imagen seleccionada"
                        return@Button
                    }
                    localError = null
                    Log.d(tag, "Submitting profile update with image=${fotoPart != null}")
                    viewModel.updateProfile(
                        biography = biografia.takeIf { it.isNotEmpty() },
                        telefono = telefono.takeIf { it.isNotEmpty() },
                        fotoPart = fotoPart
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
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
            if (localError != null || state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = localError ?: state.error.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
