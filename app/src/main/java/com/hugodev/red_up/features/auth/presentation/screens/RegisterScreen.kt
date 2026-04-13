package com.hugodev.red_up.features.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.R
import com.hugodev.red_up.features.auth.presentation.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CREAR CUENTA",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            val haloColor = MaterialTheme.colorScheme.primary

            // Logo with solar glow
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .drawBehind {
                        val outerRadius = size.minDimension / 2f
                        val innerRadius = 120.dp.toPx() / 2f
                        val innerStop = (innerRadius / outerRadius).coerceIn(0f, 1f)
                        drawCircle(
                            color = Color.White,
                            radius = innerRadius
                        )
                        val glowBrush = Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                innerStop to Color.Transparent,
                                (innerStop + 0.08f).coerceAtMost(1f) to haloColor.copy(alpha = 0.22f),
                                1f to haloColor.copy(alpha = 0.5f)
                            ),
                            radius = outerRadius
                        )
                        drawCircle(
                            brush = glowBrush,
                            radius = outerRadius
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_redup_png_sf),
                    contentDescription = "UPRed logo",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa tu perfil para unirte a la comunidad UPRed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.nombre,
                        onValueChange = viewModel::onNombreChange,
                        label = { Text(text = "Nombre") },
                        placeholder = { Text(text = "Tu nombre") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.nombreError != null,
                        supportingText = uiState.nombreError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.nombreError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (uiState.nombreError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.apellidoPaterno,
                        onValueChange = viewModel::onApellidoPaternoChange,
                        label = { Text(text = "Apellido paterno") },
                        placeholder = { Text(text = "Tu apellido paterno") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.apellidoPaternoError != null,
                        supportingText = uiState.apellidoPaternoError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.apellidoPaternoError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (uiState.apellidoPaternoError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.apellidoMaterno,
                        onValueChange = viewModel::onApellidoMaternoChange,
                        label = { Text(text = "Apellido materno") },
                        placeholder = { Text(text = "Tu apellido materno") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.correo,
                        onValueChange = viewModel::onCorreoChange,
                        label = { Text(text = "Correo institucional") },
                        placeholder = { Text(text = "student@upred.edu") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.correoError != null,
                        supportingText = uiState.correoError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.correoError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (uiState.correoError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.fechaNacimiento,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(8)
                            val formatted = buildString {
                                append(digits.take(4))
                                if (digits.length > 4) {
                                    append('-')
                                    append(digits.drop(4).take(2))
                                }
                                if (digits.length > 6) {
                                    append('-')
                                    append(digits.drop(6).take(2))
                                }
                            }
                            viewModel.onFechaNacimientoChange(formatted)
                        },
                        label = { Text(text = "Fecha de nacimiento") },
                        placeholder = { Text(text = "YYYY-MM-DD") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.fechaNacimientoError != null,
                        supportingText = uiState.fechaNacimientoError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.fechaNacimientoError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (uiState.fechaNacimientoError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = { Text(text = "Contraseña") },
                        placeholder = { Text(text = "Crea una contraseña segura") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.passwordError != null,
                        supportingText = uiState.passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.passwordError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (uiState.passwordError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    Text(
                        text = "Debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = { Text(text = "Confirmar contraseña") },
                        placeholder = { Text(text = "Repite tu contraseña") },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = uiState.confirmPasswordError != null,
                        supportingText = uiState.confirmPasswordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (uiState.confirmPasswordError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (uiState.confirmPasswordError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    if (!uiState.error.isNullOrBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = uiState.error.orEmpty(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = viewModel::register,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        if (uiState.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = "Creando...")
                            }
                        } else {
                            Text(text = "Crear cuenta")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Inicia Sesión",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
