# Implementación de Nuevas Funcionalidades - RED-UP App

## Cambios en Kotlin/Compose

### 1. HomeScreen.kt

**Cambios principales:**
```kotlin
// Nuevo FAB para enviar mensajes
FloatingActionButton(
    onClick = { showMessageDialog = true },
    containerColor = MaterialTheme.colorScheme.primary
) {
    Icon(Icons.Default.Mail, contentDescription = "Enviar mensaje")
}

// Nuevo diálogo de tipo de mensaje
if (showMessageDialog) {
    MessageTypeDialog(...)
}

// Nuevo diálogo de logout
if (showLogoutDialog) {
    AlertDialog(
        onDismissRequest = { showLogoutDialog = false },
        title = { Text("Cerrar Sesión") },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.logout(onNavigateToLogin)
                }
            ) {
                Text("Sí, cerrar sesión")
            }
        }
    )
}
```

**Nuevos Composables:**
- `MessageTypeDialog()`: Diálogo con opciones Individual/Grupal
- `PublicationCard()`: Tarjeta mejorada para publicaciones

**TopAppBar mejorado:**
```kotlin
TopAppBar(
    title = { Text(text = "UPRed - Feed") },
    actions = {
        IconButton(onClick = { showLogoutDialog = true }) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
        }
    }
)
```

### 2. HomeViewModel.kt

**Nuevo método:**
```kotlin
fun logout(onNavigateToLogin: () -> Unit) {
    viewModelScope.launch {
        try {
            chatRepository.disconnect()
            authPreferences.clear()
            onNavigateToLogin()
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = "Error al cerrar sesión: ${e.message}")
            }
        }
    }
}
```

**Funcionalidades:**
- Desconecta el WebSocket
- Limpia tokens y datos del usuario
- Navega a Login de forma segura

### 3. NavigationGraph.kt

**Actualización en composable Home:**
```kotlin
composable(Screen.Home.route) {
    HomeScreen(
        onNavigateToChat = { roomId, roomName, roomType ->
            navController.navigate(Screen.Chat.createRoute(roomId, roomName, roomType))
        },
        onNavigateToGroups = {
            navController.navigate(Screen.GroupsList.route)
        },
        onNavigateToLogin = {  // ← NUEVO
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    )
}
```

## Estructura del Código

### Imports Añadidos
```kotlin
import androidx.compose.material.icons.filled.ExitToApp    // Icono logout
import androidx.compose.material.icons.filled.Mail         // Icono mensaje
import androidx.compose.material.icons.filled.Person       // Icono individual
import androidx.compose.material3.AlertDialog              // Diálogos
import androidx.compose.material3.FloatingActionButton     // FAB
import androidx.compose.material3.CardDefaults             // Estilos de tarjeta
```

## UI Components

### MessageTypeDialog
```kotlin
@Composable
private fun MessageTypeDialog(
    onDismiss: () -> Unit,
    onIndividual: () -> Unit,
    onGroup: () -> Unit
) {
    AlertDialog(...)
}
```
Muestra opciones:
- **Individual** (📧 Persona) → inicia chat 1 a 1
- **Group** (👥 Grupo) → va a lista de grupos

### PublicationCard (Mejorado)
```kotlin
@Composable
private fun PublicationCard(publication: Publications) {
    Card(...) {
        // Encabezado: Autor y fecha
        // Título
        // Contenido
        // Pie: Reacciones y comentarios
    }
}
```

Estructura:
```
┌─────────────────────────────┐
│ Juan García - 26/02/2026    │
├─────────────────────────────┤
│ Título de la Publicación    │
│                             │
│ Contenido principal del     │
│ mensaje o anuncio que       │
│ quiere compartir...         │
├─────────────────────────────┤
│ ❤️ 45  💬 12                │
└─────────────────────────────┘
```

## Estados en HomeUiState

```kotlin
data class HomeUiState(
    val publicaciones: List<Publications> = emptyList(),
    val mensajes: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSocketConnected: Boolean = false,
    val error: String? = null,
    val userId: String? = null
)
```

## Interacciones del Usuario

### 1. Enviar Mensaje Individual
```
Usuario presiona FAB (icono sobre)
  ↓
Se abre MessageTypeDialog
  ↓
Usuario elige "Individual"
  ↓
Se navega a Chat con roomType="individual"
  ↓
ChatScreen se abre para chat 1 a 1
```

### 2. Enviar Mensaje Grupal
```
Usuario presiona FAB (icono sobre)
  ↓
Se abre MessageTypeDialog
  ↓
Usuario elige "Grupal"
  ↓
Se navega a GroupsList
  ↓
Usuario selecciona grupo
  ↓
ChatScreen se abre para chat grupal
```

### 3. Cerrar Sesión
```
Usuario presiona icono salida (🚪) en TopAppBar
  ↓
Se abre diálogo de confirmación
  ↓
Usuario confirma
  ↓
Se ejecuta logout()
  ↓
Se limpia datos locales
  ↓
Se navega a Login (con popUpTo para limpiar stack)
```

## Lifecycle

```
HomeScreen aparece
  ↓
LaunchedEffect(Unit) → loadFeed()
  ↓
LifecycleEventEffect(ON_START) → connectSocket()
  ↓
LifecycleEventEffect(ON_STOP) → disconnectSocket()
  ↓
Usuario interactúa
  ↓
HomeScreen desaparece (eg: logout)
```

## Dependencias Utilizadas

```gradle
// Compose Material 3
androidx.compose.material3:material3
androidx.compose.material3:material3-window-size-class

// Icons
androidx.compose.material:material-icons-extended

// Lifecycle
androidx.lifecycle:lifecycle-runtime-compose
androidx.lifecycle:lifecycle-viewmodel-compose

// Navigation
androidx.navigation:navigation-compose

// Hilt
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose
```

## Testing

### Unit Tests (ViewModel)
```kotlin
@Test
fun testLogout() {
    // Given
    viewModel = HomeViewModel(...)
    
    // When
    var navigatedToLogin = false
    viewModel.logout { navigatedToLogin = true }
    
    // Then
    assert(navigatedToLogin)
}
```

### UI Tests (Compose)
```kotlin
@Test
fun testFABClicksShowDialog() {
    composeTestRule.setContent {
        HomeScreen()
    }
    
    composeTestRule.onNodeWithContentDescription("Enviar mensaje")
        .performClick()
    
    composeTestRule.onNodeWithText("Tipo de Mensaje")
        .assertIsDisplayed()
}
```

## Troubleshooting

### FAB No Aparece
- Verificar que el `Scaffold` tenga `floatingActionButton` configurado
- Verificar que `modifier = Modifier.fillMaxSize()` en `LazyColumn`

### Diálogos No Funcionan
- Asegurar que `showMessageDialog` y `showLogoutDialog` estén declarados con `remember`
- Verificar que los callbacks de `onDismiss` actualicen el estado correctamente

### Logout No Funciona
- Verificar que `AuthPreferences.clear()` borra todos los datos
- Verificar que `ChatRepository.disconnect()` cierra WebSocket
- Verificar que callback `onNavigateToLogin` se ejecute

### Publicaciones No Cargan
- Verificar filtro en `LazyColumn` (si lista está vacía, muestra mensaje)
- Verificar que `loadFeed()` se ejecute solo una vez (check `isLoading`)
- Verificar endpoint `/api/publicaciones`

## Performance

- **Lazy Loading**: Publicaciones se cargan con LazyColumn
- **Disconnection**: WebSocket se desconecta en ON_STOP para ahorrar recursos
- **Memory**: AuthPreferences se limpia completamente en logout

---

**Actualizado**: 26 de Febrero, 2026
**Versión**: 2.1.0
**Kotlin**: 1.8+
**Compose**: 1.5+
