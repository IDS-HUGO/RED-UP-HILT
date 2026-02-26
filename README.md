# 🎓 RED-UP - Red Universitaria de Publicaciones

Aplicación de chat grupal e individual con publicaciones para estudiantes universitarios.

**Stack**: Kotlin/Jetpack Compose (Android) + Python/FastAPI (Backend) + MySQL

---

## 📱 Características Principales

### ✅ Implementadas
- 🏠 **Feed de Publicaciones** - Ver todas las publicaciones
- 👥 **Chat Grupal** - Crear grupos y enviar mensajes
- 💬 **Chat Individual** - Conversaciones privadas
- 🔑 **Autenticación** - Login/Register con JWT
- 🟢 **Indicador de Estado Online** - Ver quién está conectado
- ⌨️ **Indicador de Escritura** - "Usuario está escribiendo..."

### 🔄 En Desarrollo
- Búsqueda de usuarios por email
- Multimedia en chats
- Notificaciones push

---

## 🏗️ Arquitectura

```
RED-UP App (Android)
├── Navigation
│   ├── LoginScreen / RegisterScreen
│   └── MainScreen (BottomNavigation)
│       ├── 🏠 HOME - HomeFeedScreen (publicaciones)
│       ├── 👥 GRUPOS - GroupsChatListScreen  
│       └── 💬 MENSAJES - IndividualChatListScreen
│
└── Features (Clean Architecture)
    ├── home/ (domain, data, presentation)
    ├── groups_chat/ (domain, data, presentation)
    ├── individual_chat/ (domain, data, presentation)
    ├── auth/ (login/register)
    └── core/ (DI, auth, network)

API Backend (FastAPI)
├── routers/
│   ├── auth.py (login, register, logout)
│   ├── publicaciones.py (create, get, delete)
│   ├── usuarios.py (search por email)
│   ├── grupos.py (create, manage miembros)
│   └── mensajes.py (salas de chat, historial)
│
└── websocket_server.py (eventos real-time)
    ├── send_direct_message
    ├── send_group_message
    ├── typing / stop_typing
    └── get_online_users
```

---

## 🚀 Quick Start

### 1️⃣ Prerequisites
```bash
- Android Studio 2021.3+
- Android SDK 33+
- Python 3.10+
- MySQL 5.7+
```

### 2️⃣ Setup Backend
```bash
cd ../API_UPRed
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt

# Configure local.properties
# db.host=localhost
# db.port=3306
# db.user=root

python run.py
# Backend runs on http://localhost:8000
```

### 3️⃣ Setup Android App
```bash
cd RED-UP

# Create local.properties
cat > local.properties << EOF
sdk.dir=$ANDROID_SDK_PATH
api.base_url=http://10.0.2.2:8000/api/
api.websocket_url=ws://10.0.2.2:8000
EOF

# Build and run
./gradlew assembleDebug
./gradlew installDebug
```

### 4️⃣ Verify Setup
```bash
# Test backend
curl http://localhost:8000/api/

# Open app on emulator/device
# Login with test credentials
```

---

## 📂 Estructura de Carpetas

```
RED-UP/
├── app/src/main/kotlin/com/hugodev/red_up/
│   ├── MainActivity.kt
│   ├── navigation/
│   │   ├── Screen.kt (rutas centralizadas)
│   │   └── NavigationGraph.kt (orquestación)
│   │
│   ├── features/
│   │   ├── home/
│   │   │   ├── domain/
│   │   │   ├── data/
│   │   │   └── presentation/
│   │   │       ├── screens/HomeFeedScreen.kt
│   │   │       └── viewmodels/HomeViewModel.kt
│   │   │
│   │   ├── groups_chat/
│   │   │   ├── presentation/
│   │   │   │   ├── screens/GroupsChatListScreen.kt
│   │   │   │   └── viewmodels/GroupsChatViewModel.kt
│   │   │   ├── data/ + domain/
│   │   │   └── di/GroupsChatModule.kt
│   │   │
│   │   ├── individual_chat/
│   │   │   ├── presentation/
│   │   │   │   ├── screens/IndividualChatListScreen.kt
│   │   │   │   └── viewmodels/IndividualChatViewModel.kt
│   │   │   ├── data/ + domain/
│   │   │   └── di/IndividualChatModule.kt
│   │   │
│   │   ├── auth/
│   │   │   ├── presentation/
│   │   │   │   ├── screens/LoginScreen.kt
│   │   │   │   ├── screens/RegisterScreen.kt
│   │   │   │   └── viewmodels/AuthViewModel.kt
│   │   │   └── data/
│   │   │
│   │   └── main/
│   │       └── presentation/screens/MainScreen.kt (BottomNav)
│   │
│   ├── core/
│   │   ├── auth/ (tokens, interceptors)
│   │   ├── di/ (Hilt modules)
│   │   ├── network/ (API, WebSocket)
│   │   └── theme/ (Compose styling)
│   │
│   └── utils/ (constants, extensions, mappers)
│
├── build.gradle.kts
├── local.properties (tu config local)
├── gradlew / gradlew.bat
└── README.md (este archivo)
```

---

## 🔌 API Endpoints

### Auth
```http
POST   /api/auth/login           # {"email", "password"}
POST   /api/auth/register        # {"nombre", "email", "password"}
POST   /api/auth/logout          # headers: Authorization
```

### Publicaciones
```http
GET    /api/publicaciones        # todas las publicaciones
POST   /api/publicaciones        # crear nueva
GET    /api/publicaciones/{id}   # detalle
DELETE /api/publicaciones/{id}   # solo del autor
```

### Usuarios
```http
GET    /api/usuarios            # lista (con filtros)
GET    /api/usuarios/por-correo/{correo}  # buscar por email
GET    /api/usuarios/{id}       # perfil
```

### Grupos
```http
GET    /api/grupos              # mis grupos
POST   /api/grupos              # crear grupo
GET    /api/grupos/{id}         # detalle
POST   /api/grupos/{id}/miembros/invitar-por-correo?correo=email@upred.com
```

### Mensajes / Chat
```http
GET    /api/mensajes/salas              # mis salas de chat
GET    /api/mensajes/salas/{sala_id}/historial
POST   /api/mensajes/salas/directa-por-correo?correo=email@upred.com
```

---

## 🔌 WebSocket Events

**Conexión**: `ws://localhost:8000`

### Mensajes Individuales
```json
// enviar
{
  "event": "send_direct_message",
  "data": {
    "sender_id": "123",
    "recipient_id": "456",
    "mensaje": "Hola!",
    "timestamp": "2026-02-26T10:30:00"
  }
}

// recibir
{
  "event": "receive_message",
  "data": {
    "sender_id": "456",
    "nombre": "María",
    "apellido": "García",
    "mensaje": "¡Hola!",
    "timestamp": "2026-02-26T10:30:05"
  }
}
```

### Mensajes Grupales
```json
{
  "event": "send_group_message",
  "data": {
    "sender_id": "123",
    "grupo_id": "G001",
    "mensaje": "Update del proyecto"
  }
}
```

### Indicadores
```json
{
  "event": "typing",
  "data": { "user_id": "123", "recipient_id": "456" }
}

{
  "event": "stop_typing",
  "data": { "user_id": "123" }
}
```

### Presencia Online
```json
{
  "event": "get_online_users",
  "data": {}
}

// respuesta
{
  "event": "online_users_list",
  "data": { "online_users": ["123", "789", "456"] }
}
```

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
./gradlew test --tests=*ViewModelTest
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

### Coverage
```bash
./gradlew testDebugUnitTestCoverage
# Reporte en: app/build/reports/coverage/debug/index.html
```

---

## 🐛 Troubleshooting

| Problema | Solución |
|----------|----------|
| **Gradle sync falla** | `./gradlew clean` + `./gradlew build` |
| **"10.0.2.2 refused"** | Emulator: OK. Device: usa tu IP (e.g., 192.168.1.100) en `local.properties` |
| **WebSocket no conecta** | Verifica backend corriendo, URL en local.properties correcta |
| **"Module not found"** | `./gradlew --refresh-dependencies` |
| **ProGuard errors** | Comenta ProGuard en build.gradle.kts, rebuild |
| **Emulator lento** | Usa `-gpu swiftshader`, aumenta RAM a 2GB+ |

---

## 📋 Estado del Proyecto

| Feature | Status | Responsable |
|---------|--------|-------------|
| Bottom Navigation | ✅ | Equipos 1-3 |
| Home Feed | ✅ | Equipo 1 |
| Groups Chat | 🔄 | Equipo 2 |
| Individual Chat | 🔄 | Equipo 3 |
| Auth (Login/Register) | ✅ | Core |
| WebSocket Backend | ✅ | Backend |
| Database | ✅ | Backend |
| Deployment | 📋 | Para después |

---

## 🛠️ Comandos Útiles

```bash
# Build
./gradlew build                  # Build completo
./gradlew assembleDebug         # APK debug
./gradlew assembleRelease       # APK release (requiere keystore)

# Clean
./gradlew clean                 # Limpiar build/

# Tests
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # UI tests
./gradlew lint                  # Lint checks

# Debug
adb logcat | grep RED-UP        # Ver logs
adb shell                        # Shell en device
adb install app/build/outputs/apk/debug/app-debug.apk

# Gradle
./gradlew dependencies          # Ver dependencias
./gradlew --refresh-dependencies # Limpiar cache
./gradlew wrapper --gradle-version 8.0 # Update gradle
```

---

## 📚 Recursos

- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Kotlin**: https://kotlinlang.org/docs/
- **Hilt DI**: https://dagger.dev/hilt/
- **Retrofit**: https://square.github.io/retrofit/
- **FastAPI**: https://fastapi.tiangolo.com/
- **WebSocket**: https://fastapi.tiangolo.com/advanced/websockets/

---

## 👥 Contribuyendo

1. Crear rama: `git checkout -b feature/tu-feature`
2. Hacer cambios + tests
3. Push y Create Pull Request
4. Code review antes de merge
5. Deploy a producción después de approve

---

## 📞 Contacto & Soporte

- **Issues**: Abre issue en GitHub
- **Docs**: Refiere a esta guía
- **Backend**: Ve a API_UPRed/README.md  
- **Android**: Ve a app/README.md (si existe)

---

## 📄 Licencia

Este proyecto es propiedad de la Universidad de Pura Vida. Derechos reservados.

---

**Última actualización**: Feb 26, 2026  
**Versión**: 2.2.0 - Bottom Navigation Architecture  
**Status**: ✅ Ready for Team Development
