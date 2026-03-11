# 🔴 FLUJO COMPLETO DEL CHAT GRUPAL - RED-UP

---

## 📊 ESTRUCTURA DE CARPETAS (¿POR QUÉ ESTÁ HECHA ASÍ?)

```
chat/                          ← MÓDULO CENTRAL DE CHAT
├── data/                       ← Lógica de comunicación (REST + WebSocket)
│   ├── repositories/
│   │   └── SocketIoChatRepository.kt  (Singleton con Socket.IO)
│   └── di/
│       └── ChatModule.kt       (Inyecta: ChatRepository)
├── domain/                     ← Lógica de negocio pura
│   ├── repositories/
│   │   └── ChatRepository.kt   (Interfaz)
│   ├── entities/
│   │   └── ChatMessage.kt      (Datos del mensaje)
│   └── usecases/               ← 8 casos de uso
│       ├── ConnectToChatUseCase
│       ├── SendChatMessageUseCase
│       ├── JoinGroupChatUseCase
│       ├── ObserveChatMessagesUseCase
│       └── ... más
└── presentation/
    ├── viewmodels/
    │   └── ChatViewModel.kt    (La orquesta, inyecta TODOS los Use Cases)
    └── screens/
        └── ChatScreen.kt       (UI, consume el ViewModel)

groups_chat/                   ← SOLO PARA LISTAR GRUPOS
├── presentation/
│   ├── screens/
│   │   └── GroupsChatListScreen.kt  (Muestra los grupos del usuario)
│   └── viewmodels/
│       └── GroupsChatViewModel.kt   (Inyecta GetMyGroupsUseCase)
└── di/
    └── GroupsChatModule.kt     (VACÍO - Solo para organización)

individual_chat/               ← SOLO PARA LISTAR CHATS 1A1
├── presentation/
│   ├── screens/
│   │   └── IndividualChatListScreen.kt  (Muestra usuarios disponibles)
│   └── viewmodels/
│       └── IndividualChatViewModel.kt   (Inyecta SearchUsersUseCase)
└── di/
    └── IndividualChatModule.kt (VACÍO - Solo para organización)

groups/                        ← MÓDULO DE GESTIÓN DE GRUPOS
├── data/
│   ├── repositories/
│   │   └── GroupRepositoryImpl.kt  (REST API)
│   └── datasources/remote/api/
│       └── GroupsApi.kt        (Retrofit)
├── domain/
│   ├── repositories/
│   │   └── GroupRepository.kt
│   ├── entities/
│   │   └── Group.kt
│   └── usecases/
│       ├── CreateGroupUseCase
│       ├── GetMyGroupsUseCase
│       ├── InviteMemberUseCase
│       └── ...
└── presentation/
    ├── screens/
    │   ├── GroupDetailScreen.kt  (Ver grupo y agregar miembros)
    │   ├── CreateGroupScreen.kt  (Crear grupo)
    │   └── InviteMembersScreen.kt
    └── viewmodels/
        └── GroupsViewModel.kt
```

---

## ⚠️ ¿POR QUÉ LAS CARPETAS `groups_chat` E `individual_chat` ESTÁN SEMI-VACÍAS?

### Porque son **SOLO PANTALLAS DE LISTADO**, NO hacen lógica de chat

```
groups_chat/                        individual_chat/
├── Muestra: Lista de grupos    ├── Muestra: Lista de usuarios
├── Al hacer click: Navega      ├── Al hacer click: Navega
└── A ChatScreen (del módulo chat)  └── A ChatScreen (del módulo chat)
```

**Analógía**: `groups_chat` es como una "cartelera de cines", no es el cine.
El cine es `chat` que hace el trabajo real.

---

## 🔄 FLUJO PASO A PASO: CREAR GRUPO → AGREGAR PERSONAS → CHATEAR

### FASE 1: CREAR UN GRUPO
```
┌─ CreateGroupScreen
│
├─ Usuario ingresa:
│  ├── Nombre: "Proyecto IA"
│  ├── Descripción: "Chat para proyecto"
│  ├── Carrera: Ingeniería en Sistemas
│  └── Privacidad: "privado"
│
├─ [Button] Crear Grupo
│  │
│  └─► GroupsViewModel.createGroup()
│      │
│      └─► CreateGroupUseCase() ◄─────────────────┐
│          │                                       │
│          └─► GroupRepository.createGroup()       │ INYECCIÓN
│              │                                   │ DE HILT
│              └─► GroupsApi.createGroup()         │
│                  │                               │
│                  └─── HTTP POST ──────────────┐  │
│                                               │  │
│                         ┌──────────────────┐  │  │
│                         │ SERVIDOR REST    │  │  │
│                         ├──────────────────┤  │  │
│                         │ /api/grupos/     │◄─┘  │
│                         │ POST             │     │
│                         │                  │     │
│                         │ Crea grupo en BD │     │
│                         │ Returns: GroupId=42    │
│                         └──────────────────┘     │
│                                                  │
└─ Navega a: GroupDetailScreen(groupId=42) ──────┘
```

---

### FASE 2: AGREGAR MIEMBROS AL GRUPO

```
┌─ GroupDetailScreen(groupId=42)
│
├─ Muestra:
│  ├── Nombre: "Proyecto IA"
│  ├── Descripción: "Chat para proyecto"
│  └── Botón: "Agregar Miembros"
│
├─ [Button] + Agregar Miembros
│  │
│  └─► Navega a: InviteMembersScreen(groupId=42)
│
┌─────────────────────────────────────────────────┐
│ InviteMembersScreen                             │
├─────────────────────────────────────────────────┤
│                                                 │
│ Usuario busca: "carlos@mail.com"                │
│                                                 │
│ GroupsViewModel.searchUsers("carlos...")        │
│  │                                              │
│  └─► SearchUsersUseCase()                       │
│      └─► GroupRepository.searchUsers()          │
│          └─► GroupsApi.searchUsers()            │
│              └─ HTTP GET: /api/usuarios/buscar  │
│                                                 │
│ Retorna: [User(id=123, email="carlos@...")] │
│                                                 │
│ [Button] Invitar a carlos                       │
│  │                                              │
│  └─► GroupsViewModel.inviteMember()             │
│      │                                          │
│      └─► InviteMemberUseCase()                  │
│          └─► GroupRepository.inviteMember()     │
│              └─► GroupsApi.inviteMember()       │
│                  │                              │
│                  └─ HTTP POST:                  │
│                     /api/grupos/42/miembros/123 │
│                     /invitar                    │
│                                                 │
│ ✅ Miembro agregado                             │
│                                                 │
└─────────────────────────────────────────────────┘

Retorna a: GroupDetailScreen
```

---

### FASE 3: ABRIR CHAT GRUPAL

```
┌─ GroupDetailScreen(groupId=42)
│
├─ Muestra miembros: [Carlos, María, Juan]
│
├─ [Button] 💬 Abrir Chat
│  │
│  └─► Navega a: ChatScreen(
│                  roomId="42",
│                  roomName="Proyecto IA",
│                  roomType="grupal"  ◄─── IMPORTANTE!
│              )
│
├─────────────────────────────────────────────────────────┐
│ ChatScreen.kt                                           │
├─────────────────────────────────────────────────────────┤
│                                                         │
│ LaunchedEffect(Unit) {                                  │
│    viewModel.connectToChat()                            │
│    ↓                                                    │
│    Se llama: ChatViewModel.connectToChat()              │
│    │                                                    │
│    └─► ConnectToChatUseCase(userId="456")              │
│        │                                                │
│        └─► ChatRepository.connect(userId="456")         │
│            │                                            │
│            └─── SocketIoChatRepository.connect()        │
│                 │                                       │
│                 └─► new Socket(                          │
│                     url="wss://server",                 │
│                     options={"user_id": 456}            │
│                 )                                       │
│                                                         │
│ ✅ WebSocket conectado                                  │
│                                                         │
│ }                                                       │
│                                                         │
│ LaunchedEffect(isConnected) {                           │
│    if (isConnected) {                                   │
│       viewModel.joinRoom(                               │
│           roomId="42",                                  │
│           roomName="Proyecto IA",                       │
│           roomType="grupal"  ◄─── CLAVE!               │
│       )                                                 │
│       ↓                                                 │
│       ChatViewModel.joinRoom() detecta:                 │
│       when(roomType) {                                  │
│           "grupal" -> {                                 │
│               JoinGroupChatUseCase("42")                │
│               │                                         │
│               └─► ChatRepository.joinGroup("42")        │
│                   │                                     │
│                   └─► socket.emit(                      │
│                       "join_group",                     │
│                       {"group_id": "42"}                │
│                   )                                     │
│           }                                             │
│       }                                                 │
│    }                                                    │
│ }                                                       │
│                                                         │
└─────────────────────────────────────────────────────────┘

🔌 SERVIDOR RECIBE: "join_group" con group_id=42
   │
   └─ Responde: "group_joined" con {
       "group_id": "42",
       "sala_uuid": "aabbcc1234"  ◄─ ID único de la sala
   }

SocketIoChatRepository recibe "group_joined":
│
└─► groupJoinedFlow.emit("aabbcc1234")
    │
    └─► ChatViewModel via observeJoinedRoom()
        │
        └─► _uiState.currentRoomId = "aabbcc1234"

✅ Ahora el usuario está en la sala grupal
```

---

### FASE 4: ENVIAR MENSAJE

```
┌─ ChatScreen (usuario ve la lista de mensajes)
│
├─ Usuario escribe: "Hola equipo! 👋"
│
├─ _uiState.newMessage = "Hola equipo! 👋"
│
├─ [Button] Enviar
│  │
│  └─► ChatViewModel.sendMessage()
│      │
│      ├─ Crea: ChatMessage(
│      │   id = "msg_123",
│      │   to = "aabbcc1234",       ◄─ sala_uuid (grupal)
│      │   message = "Hola equipo! 👋",
│      │   senderId = "456",         ◄─ MI usuario
│      │   senderName = "Hugo",
│      │   timestamp = "2026-02-27T14:30:00",
│      │   type = "grupal"
│      │ )
│      │
│      └─► SendChatMessageUseCase(message)
│          │
│          └─► ChatRepository.sendMessage(message)
│              │
│              └─► SocketIoChatRepository.sendMessage()
│                  │
│                  └─► socket.emit(
│                      "send_message",
│                      {
│                        "to": "aabbcc1234",
│                        "message": "Hola equipo! 👋",
│                        "sender_id": "456",
│                        "sender_name": "Hugo",
│                        "timestamp": "2026-02-27T14:30:00",
│                        "type": "grupal"
│                      }
│                  )
│
├─ Se agrega a: _uiState.pendingMessages (viendo en tiempo real)
│  └─ UI muestra el mensaje con "enviando..." ⏳
│
└─ Se emite al servidor WebSocket

🔌 SERVIDOR RECIBE:
   ├─ Valida el mensaje
   ├─ Guarda en base de datos
   │
   └─ Emite a TODOS los usuarios en la sala "aabbcc1234":
      │
      └─ socket.broadcast.to("aabbcc1234").emit(
          "receive_message",
          {
            "id": "msg_123",
            "to": "aabbcc1234",
            "message": "Hola equipo! 👋",
            "sender_id": "456",
            "sender_name": "Hugo",
            "timestamp": "2026-02-27T14:30:00",
            "type": "grupal"
          }
      )
```

---

### FASE 5: RECIBIR MENSAJE

```
Otros usuarios conectados a la sala "aabbcc1234":
├─ Carlos 🟢 online
├─ María 🟢 online
└─ Juan 🟢 online

🔌 RECIBEN EVENT: "receive_message"
   │
   └─► SocketIoChatRepository escucha:
       │
       on("receive_message") { args ->
           │
           ├─ Parsea el JSON
           │
           ├─ Crea: ChatMessage(...)
           │
           └─► messagesFlow.emit(message)
               │
               └─► Observable en ChatViewModel
                   │
                   └─► observeChatMessagesUseCase()
                       (ya está observando)
                       │
                       └─► Recibe el flow.collect { message ->
                           │
                           ├─ Verifica: message.to == _uiState.currentRoomId
                           │  (¿Pertenece a mi sala actual?)
                           │
                           ├─ SÍ: Agrega a _uiState.messages
                           │  │
                           │  └─► La UI se recompone automáticamente
                           │      (Jetpack Compose - MutableStateFlow)
                           │
                           └─► ACTUALIZACIÓN EN PANTALLA:
                               
                               [14:30] Hugo: "Hola equipo! 👋"
                               ✅ Entregado
       }

┌─────────────────────────────────────────────┐
│ PANTALLA DEL USUARIO SUELE MOSTRARSE:       │
├─────────────────────────────────────────────┤
│                                             │
│ [14:25] Carlos: "¿Cuándo empezamos?"        │
│ [14:28] María: "En 5 minutos"               │
│ [14:30] Hugo: "Hola equipo! 👋"             │
│ [14:30] Carlos: "¡Hola!"                    │
│                                             │
└─────────────────────────────────────────────┘

✅ Mensaje recibido en tiempo real!
```

---

## 💉 INYECCIÓN DE DEPENDENCIAS (HILT/DAGGER)

### ¿CÓMO FUNCIONA?

Imagina que necesitas un **automóvil**:

```
SIN inyección (INCORRECTO):
├─ Class: AutoViewModel
│  └─ init {
│     engine = Engine()           // Creas el motor
│     fuel = FuelTank()            // Creas el tanque
│     wheels = Wheels()            // Creas las ruedas
│     car = Car(engine, fuel, wheels)  // Montas el auto
│  }

CON inyección (CORRECTO):
├─ class AutoViewModel(
│    private val car: Car  ◄─ Te lo dan hecho
│ ) : ViewModel()
│
└─ Hilt dice: "Yo armo todo"
   ├─ Crea Engine()
   ├─ Crea FuelTank()
   ├─ Crea Wheels()
   ├─ Crea Car(engine, fuel, wheels)
   └─ Te lo pasa al ViewModel
```

---

### EN RED-UP: FLUJO DE INYECCIÓN DEL CHAT GRUPAL

```
┌──────────────────────────────────────┐
│ 1. Hilt ve: @HiltViewModel             │
│    class ChatViewModel @Inject        │
│    constructor(...)                  │
└──────────────────────────────────────┘
   │
   └─► Hilt analiza qué necesita:
       ├─ ConnectToChatUseCase
       ├─ SendChatMessageUseCase
       ├─ JoinGroupChatUseCase
       ├─ ObserveChatMessagesUseCase
       ├─ ... más use cases
       └─ SocketIoChatRepository
           │
           └─► ¿Dónde consigo estos?
               │
               ┌─────────────────────────┐
               │ BÚSQUEDA EN MÓDULOS DI  │
               └─────────────────────────┘
               │
               ├─► ChatModule.kt
               │   │
               │   @Binds
               │   @Singleton
               │   fun bindChatRepository(
               │       socketIoChatRepository: SocketIoChatRepository
               │   ): ChatRepository
               │   │
               │   └─ "Para ChatRepository, usa SocketIoChatRepository"
               │      Y guárdalo como @Singleton (1 sola instancia)
               │
               ├─► Cada UseCase @Inject constructor(
               │   private val repository: ChatRepository
               │ ) {
               │   // Hilt busca ChatRepository
               │   // Encuentra: SocketIoChatRepository (Singleton)
               │   // Lo inyecta aquí
               │ }
               │
               └─► SocketIoChatRepository @Inject constructor(
                   @SocketBaseUrl private val socketBaseUrl: String
               ) {
                   // Hilt busca @SocketBaseUrl
                   // Lo encuentra en CoreModule
                   // Lo inyecta aquí
               }

┌──────────────────────────────────────┐
│ RESULTADO FINAL:                     │
├──────────────────────────────────────┤
│                                      │
│ ChatViewModel recibe:                │
│ ├─ ConnectToChatUseCase              │
│ │  └─ conectado a ChatRepository    │
│ │     └─ que es SocketIoChatRepository
│ │                                    │
│ ├─ SendChatMessageUseCase            │
│ │  └─ conectado a ChatRepository    │
│ │     └─ que es SocketIoChatRepository
│ │                                    │
│ ├─ JoinGroupChatUseCase              │
│ │  └─ conectado a ChatRepository    │
│ │     └─ que es SocketIoChatRepository
│ │                                    │
│ └─ SocketIoChatRepository (SINGLETON)│
│    └─ 1 sola instancia para toda    │
│       la aplicación (importante!)   │
│                                      │
└──────────────────────────────────────┘
```

---

### VISUALIZACIÓN: TODA LA CADENA DE INYECCIÓN

```
┌─────────────────────────────────────────────────────────────┐
│ USUARIO ABRE: ChatScreen(roomId="42", roomType="grupal")    │
└─────────────────────────────────────────────────────────────┘
   │
   └─► ChatScreen se crea
       │
       └─► viewModel = hiltViewModel()
           │
           └─► Hilt busca: ChatViewModel
               │
               ├─► ¿Cómo lo construyo?
               │   │
               │   ├─ Necesito: ConnectToChatUseCase
               │   │  │
               │   │  └─► Hilt busca: ConnectToChatUseCase
               │   │      │
               │   │      └─► @Inject constructor(
               │   │          private val repository: ChatRepository
               │   │      )
               │   │          │
               │   │          ├─ ¿Dónde está ChatRepository?
               │   │          │
               │   │          └─► En ChatModule:
               │   │              @Binds
               │   │              fun bindChatRepository(
               │   │                  socketIoChatRepository: SocketIoChatRepository
               │   │              ): ChatRepository
               │   │              │
               │   │              └─► @Singleton
               │   │                  class SocketIoChatRepository @Inject
               │   │                  constructor(
               │   │                      @SocketBaseUrl private val socketBaseUrl: String
               │   │                  )
               │   │                  │
               │   │                  └─► Crea socket.io
               │   │
               │   ├─ Necesito: SendChatMessageUseCase
               │   │  └─► [igual proceso]
               │   │
               │   ├─ Necesito: JoinGroupChatUseCase
               │   │  └─► [igual proceso]
               │   │
               │   └─ ... más use cases
               │
               └─► ✅ ChatViewModel construido y listo

┌─────────────────────────────────────────────────────────────┐
│ VENTAJA: Todas estas instancias usan el MISMO              │
│ SocketIoChatRepository (porque es @Singleton)              │
│                                                             │
│ = UN SOLO WebSocket para toda la app                       │
│ = NO se crean múltiples conexiones                         │
│ = Eficiente! 🚀                                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 FLUJO DE DATOS (REACTIVE)

```
┌──────────────────┐
│ SocketIoChatRepository │
│ (Singleton)          │
└──────────────────┘
   │
   ├─ MutableSharedFlow<ChatMessage> messagesFlow
   │  │
   │  └─ on("receive_message") { args ->
   │     messagesFlow.emit(message)
   │  }
   │
   ├─ MutableStateFlow<Boolean> connectionFlow
   │  │
   │  └─ on(Socket.EVENT_CONNECT) {
   │     connectionFlow.value = true
   │  }
   │
   └─ MutableSharedFlow<String> groupJoinedFlow
      │
      └─ on("group_joined") { args ->
         groupJoinedFlow.emit(salaUuid)
      }

         │
         ▼

┌──────────────────────────┐
│ ObserveChatMessagesUseCase │
│ (Inyectado en ViewModel) │
└──────────────────────────┘
   │
   └─► repository.observeMessages()
       │
       └─► messagesFlow.Flow<ChatMessage>


         │
         ▼

┌──────────────────────────┐
│ ChatViewModel      │
│ (Observe Flow)     │
└──────────────────────────┘
   │
   observeMessages() {
       viewModelScope.launch {
           observeChatMessagesUseCase().collect { message ->
               _uiState.value = _uiState.value.copy(
                   messages = _uiState.value.messages + message
               )
           }
       }
   }

         │
         ▼

┌──────────────────────────┐
│ uiState StateFlow  │
│ (Reactivo)         │
└──────────────────────────┘
   │
   val uiState: StateFlow<ChatUiState> = ...

         │
         ▼

┌──────────────────────────┐
│ ChatScreen             │
│ (Collect State)        │
└──────────────────────────┘
   │
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()
   │
   LazyColumn {
       items(uiState.messages) { message ->
           MessageCard(message)  ◄─ ¡RENDERIZA!
       }
   }

         │
         ▼

   📱 PANTALLA ACTUALIZADA EN TIEMPO REAL!
```

---

## 📝 RESUMEN DE ARCHIVOS VACÍOS

### ¿POR QUE **groups_chat/di/GroupsChatModule.kt** ESTÁ VACÍO?

```kt
@Module
@InstallIn(SingletonComponent::class)
object GroupsChatModule {
    // VACÍO - porque groups_chat NO crea dependencias propias
    // Solo es una pantalla de LISTADO de grupos
    // Las dependencias que necesita (GetMyGroupsUseCase)
    // ya las crea el módulo "groups" (que SÍ tiene data y domain)
}
```

**Razón**: No hay lógica de chat grupal aquí, solo listado.

### ¿POR QUE **individual_chat/di/IndividualChatModule.kt** ESTÁ VACÍO?

```kt
@Module
@InstallIn(SingletonComponent::class)
object IndividualChatModule {
    // TODO: Agregar dependencias...
    // VACÍO - porque individual_chat también es solo LISTADO
    // La lógica de chat 1a1 va en el módulo "chat"
}
```

**Razón**: Mismo que groups_chat - es solo una pantalla de listado.

---

## ⚙️ MÓDULOS QUE SÍ TIENEN CÓDIGO:

```
chat/di/ChatModule.kt                      ✅ TIENE CÓDIGO
├─ Inyecta ChatRepository
├─ ChatRepository → SocketIoChatRepository
└─ Como @Singleton (una sola instancia)

groups/di/ (probablemente)                 ✅ DEBERÍA TENER
├─ Inyecta GroupRepository
├─ GroupRepository → GroupRepositoryImpl
└─ Inyecta GroupsApi (Retrofit)

core/di/ (probablemente)                   ✅ DEBERÍA TENER
├─ Inyecta @SocketBaseUrl
├─ Inyecta AuthPreferences
└─ Inyecta Retrofit, OkHttpClient, etc.
```

---

## 🎬 RESUMEN VISUAL: FLUJO COMPLETO EN 1 VISTAZO

```
1️⃣ CREAR GRUPO
   ExampleActivity
   └─► CreateGroupScreen
       └─► GroupsViewModel.createGroup()
           └─► CreateGroupUseCase
               └─► GroupRepository.createGroup()
                   └─► REST API POST /api/grupos/

2️⃣ AGREGAR MIEMBROS
   GroupDetailScreen
   └─► InviteMembersScreen
       └─► GroupsViewModel.inviteMember()
           └─► InviteMemberUseCase
               └─► GroupRepository.inviteMember()
                   └─► REST API POST /api/grupos/{id}/miembros/{id}/invitar

3️⃣ ABRIR CHAT
   GroupDetailScreen [Button] Chat
   └─► ChatScreen(roomId="42", roomType="grupal")
       ├─► ChatViewModel.connectToChat()
       │   └─► ConnectToChatUseCase
       │       └─► SocketIoChatRepository.connect()
       │           └─ WebSocket CONECTA
       │
       └─► ChatViewModel.joinRoom()
           └─► JoinGroupChatUseCase
               └─► socket.emit("join_group", {"group_id":"42"})
                   └─ Servidor responde: "group_joined"

4️⃣ ENVIAR MENSAJE
   ChatScreen [TextField] + [Button] Enviar
   └─► ChatViewModel.sendMessage()
       └─► SendChatMessageUseCase
           └─► socket.emit("send_message", {...})
               └─ Servidor lo envía a TODOS en la sala

5️⃣ RECIBIR MENSAJE
   SocketIoChatRepository detecta: "receive_message"
   └─► messagesFlow.emit(message)
       └─► ChatViewModel.observeMessages()
           └─► _uiState.messages += message
               └─► ChatScreen se recompone
                   └─ ¡MENSAJE APARECEENLA PANTALLA! ✅
```

---

## 🔑 CONCEPTOS CLAVE

| Concepto | Definición | En RED-UP |
|----------|-----------|----------|
| **@Inject** | "Hilt, necesito esto" | En constructores de ViewModels y UseCases |
| **@Module** | "Aquí hay dependencias" | ChatModule, GroupsChatModule |
| **@Binds** | "Si pides interfaz X, dame implementación Y" | ChatRepository → SocketIoChatRepository |
| **@Singleton** | "Solo 1 instancia en toda la app" | SocketIoChatRepository (socket único) |
| **Flow<T>** | "Observable reactivo asincrónico" | messagesFlow, connectionFlow |
| **StateFlow<T>** | "Observable que siempre tiene valor" | uiState, isConnected |
| **emit()** | "Emitir valor a los listeners" | messagesFlow.emit(message) |
| **collect()** | "Escuchar los valores del Flow" | observeChatMessagesUseCase().collect { } |

---

## ✅ CONCLUSIÓN

**Chat Grupal = Flujo completo:**
1. Crear grupo (REST API)
2. Agregar miembros (REST API)
3. Abrir chat (WebSocket + inyección Hilt)
4. Enviar mensajes (WebSocket)
5. Recibir mensajes (WebSocket + Flows reactivos)

**Inyección Hilt = Construcción automática de objetos:**
- Hilt busca los `@Module` 
- Encuentra las bindings
- Construye la cadena de dependencias
- Te pasa todo listo al ViewModel

**Por qué hay carpetas semivacías:**
- `groups_chat/` y `individual_chat/` = solo pantallas de listado
- El trabajo real está en `chat/` (WebSocket + inyección)
