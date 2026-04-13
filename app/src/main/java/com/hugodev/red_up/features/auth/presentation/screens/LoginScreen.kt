package com.hugodev.red_up.features.auth.presentation.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hugodev.red_up.R
import com.hugodev.red_up.features.auth.presentation.viewmodels.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotCode by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricManager = remember(context) { BiometricManager.from(context) }
    val canAuthenticateWithFingerprint = remember(biometricManager) {
        biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(uiState.email) {
        if (forgotEmail.isBlank()) {
            forgotEmail = uiState.email
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "UPRED UNIVERSITY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate back if needed */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            
            // Logo without background
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

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome title
            Text(
                text = "Bienvenido a Red-UP",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Inicia Sesión para interactuar con la\n comunidad UP",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email field with validation icon
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text(text = "Correo electrónico") },
                placeholder = { Text(text = "student@upred.edu") },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Mail, 
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    ) 
                },
                trailingIcon = {
                    if (uiState.email.isNotBlank() && uiState.email.contains("@")) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Valid",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = CircleShape
                                )
                                .padding(2.dp)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = uiState.emailError != null,
                supportingText = uiState.emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (uiState.emailError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (uiState.emailError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text(text = "Contraseña") },
                placeholder = { Text(text = "Ingresa tu contraseña") },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Lock, 
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
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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

            Spacer(modifier = Modifier.height(8.dp))

            // Remember me checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.rememberMe,
                    onCheckedChange = viewModel::onRememberMeChange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recordar sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (canAuthenticateWithFingerprint && uiState.hasBiometricCredentials) {
                Button(
                    onClick = {
                        if (activity == null) {
                            viewModel.clearError()
                            return@Button
                        }

                        val executor = ContextCompat.getMainExecutor(activity)
                        val biometricPrompt = BiometricPrompt(
                            activity,
                            executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                    viewModel.loginWithBiometric()
                                }

                                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                    viewModel.clearError()
                                }
                            }
                        )

                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Acceso con huella")
                            .setSubtitle("Confirma tu identidad para iniciar sesión")
                            .setNegativeButtonText("Cancelar")
                            .build()

                        biometricPrompt.authenticate(promptInfo)
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        text = "Entrar con huella",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Forgot password link
            TextButton(
                onClick = {
                    forgotEmail = uiState.email
                    showForgotDialog = true
                    viewModel.clearForgotState()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Olvide mi contrasena",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            if (!uiState.error.isNullOrBlank()) {

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotDialog = false
                viewModel.clearForgotState()
            },
            title = { Text("Recuperar contrasena") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Correo institucional") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = forgotCode,
                        onValueChange = { forgotCode = it },
                        label = { Text("Codigo (6 digitos)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = forgotNewPassword,
                        onValueChange = { forgotNewPassword = it },
                        label = { Text("Nueva contrasena") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!uiState.forgotMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.forgotMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (!uiState.forgotDebugCode.isNullOrBlank()) {
                        Text(
                            text = "Codigo de prueba: ${uiState.forgotDebugCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmPasswordReset(
                            email = forgotEmail,
                            code = forgotCode,
                            newPassword = forgotNewPassword
                        )
                    },
                    enabled = !uiState.isForgotLoading
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { viewModel.requestPasswordReset(forgotEmail) },
                        enabled = !uiState.isForgotLoading
                    ) {
                        Text("Enviar codigo")
                    }
                    TextButton(
                        onClick = {
                            showForgotDialog = false
                            viewModel.clearForgotState()
                        }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        )
    }
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sign in button with gradient effect
            Button(
                onClick = viewModel::login,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (uiState.isLoading) 0.dp else 6.dp,
                        shape = RoundedCornerShape(12.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ),
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
                        Text(
                            text = "Signing In...",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                } else {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create account text
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Aún no tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(
                    onClick = onRegisterClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Regístrate",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
