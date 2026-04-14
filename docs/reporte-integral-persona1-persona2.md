# Reporte Integral Movil (Persona 1 y Persona 2)

## 1. Resumen Ejecutivo

RED-UP resuelve la necesidad de una red social universitaria con comunicacion academica y social en tiempo real, integrando publicaciones, grupos, chat, perfiles y notificaciones.

Propuesta de valor:

1. Conectividad universitaria centralizada (publicaciones, chat y perfiles en una sola app).
2. Operacion resiliente en escenarios de red intermitente.
3. Experiencia moderna con sincronizacion en background, push notifications y soporte de hardware movil.
4. Flujo social completo: seguimiento de usuarios, perfil publico y notificaciones por eventos relevantes.

---

## 2. Definicion de Requerimientos Funcionales (RF)

| ID | Requerimiento | Descripcion Detallada | Prioridad |
|---|---|---|---|
| RF-01 | Gestion de usuarios | Registro, inicio de sesion, cierre de sesion y recuperacion de contrasena con codigo temporal. | Alta |
| RF-02 | Sincronizacion offline | El usuario puede generar eventos no criticos sin internet; se envian en batch cuando vuelve la conectividad. | Alta |
| RF-03 | Publicaciones | Crear, editar, eliminar y listar publicaciones con estados de carga/error/reintento. | Alta |
| RF-04 | Comentarios y reacciones | Comentar y reaccionar a publicaciones, incluyendo conteo en UI. | Alta |
| RF-05 | Perfil y seguimiento | Ver perfil propio y de terceros, seguir/dejar de seguir, y navegar al perfil del autor desde una publicacion. | Alta |
| RF-06 | Notificaciones push | Recibir push de eventos sociales/chat y navegar por deep link a pantalla destino. | Alta |
| RF-07 | Estado de sincronizacion | Pantalla dedicada para ver ultima sync, pendientes, errores y boton de reintento. | Media |
| RF-08 | Chat | Soporte de chats individuales y grupales con flujo de navegacion unificado. | Alta |
| RF-09 | Notificacion por nuevo seguidor | Cuando un usuario te sigue, se genera notificacion interna y push. | Alta |
| RF-10 | Soporte claro/oscuro | Las pantallas principales usan MaterialTheme para consistencia en modo claro y oscuro. | Media |

---

## 3. Especificaciones de Requerimientos Tecnicos (RT)

### 3.1 Stack Tecnologico y Arquitectura

1. Lenguaje: Kotlin.
2. UI Framework: Jetpack Compose + Material 3.
3. Arquitectura: MVVM con separacion por capas (presentation/domain/data).
4. Inyeccion de dependencias: Hilt.
5. Networking: Retrofit + OkHttp.
6. Serializacion: Gson.
7. Background processing: WorkManager.
8. Push: Firebase Cloud Messaging (cliente) + Firebase Admin SDK (backend).

### 3.2 Gestion de Datos y Persistencia

1. Persistencia local: Room para estado de sincronizacion, cola de eventos diferidos y cache ligera de notificaciones.
2. Endpoints consumidos (principal):
   - `/api/notificaciones/dispositivos`
   - `/api/notificaciones/dispositivos/token`
   - `/api/notificaciones/configuracion`
   - `/api/notificaciones/eventos/sync`
   - `/api/notificaciones/resumen`
   - `/api/notificaciones/push/test`
   - `/api/auth/forgot-password/request`
   - `/api/auth/forgot-password/confirm`
3. Single Source of Truth:
   - UI observa estados desde ViewModel (`StateFlow`).
   - ViewModel coordina repositorio/remoto.
   - Room conserva estado operativo de sync para continuidad offline/online.

---

## 4. Implementacion de Componentes Avanzados

### 4.1 Background Processing (WorkManager)

Implementacion:

1. `OneTimeWorkRequest` para sync puntual (`sync_token`, `force`).
2. `PeriodicWorkRequest` cada 15 minutos para sincronizacion recurrente.
3. Constraint aplicado: `NetworkType.CONNECTED`.
4. Politicas de unicidad:
   - `ExistingWorkPolicy.REPLACE` para one-time.
   - `ExistingPeriodicWorkPolicy.UPDATE` para periodic.

Justificacion:

1. Evita trabajos duplicados.
2. Sobrevive cierre de app/proceso.
3. Reintentos automaticos con `Result.retry()`.

### 4.2 Hardware e Interaccion

Componentes usados:

1. Camara (captura/escaneo QR).
2. Biometria (acceso con huella para login).
3. Vibracion (feedback de interaccion en chats/eventos).

Permisos y ciclo de vida:

1. Permisos declarados en manifest (camara, biometria, notificaciones).
2. Solicitud runtime para notificaciones en Android 13+.
3. Manejo del ciclo de vida para recursos de UI/escaneo.

### 4.3 Notificaciones Push (FCM)

Flujo:

1. Firebase entrega token en Android (`onNewToken`).
2. App guarda token y encola sync a backend.
3. Backend registra dispositivo/token y envia push cuando aplica.
4. App recibe push (`onMessageReceived`) y crea notificacion local.
5. Al tocar la notificacion, deep link abre chat/publicacion/perfil.

Canal de notificacion:

1. Se crea canal `upred_push` con prioridad alta para Android O+.

---

## 5. Gestion de Estado y Flujo de Datos

### 5.1 StateFlow / SharedFlow

Patron aplicado:

1. Cada ViewModel expone `StateFlow<UiState>`.
2. UI Compose observa con `collectAsStateWithLifecycle()`.
3. Cambios de red/errores actualizan estado reactivo sin acoplar UI a la capa de datos.

### 5.2 UiState

Estructura operativa usada:

1. `isLoading` para operaciones en curso.
2. `error` para fallos recuperables.
3. Datos cargados para estado exitoso.
4. En sync: `lastSyncAt`, `pendingCount`, `unreadNotifications`, `lastError`.

Resultado:

1. Estados medibles, verificables y trazables para demo y debugging.

---

## 6. Conclusiones y Trabajo Futuro

### 6.1 Retos tecnicos

1. Coordinar sincronizacion resiliente entre WorkManager, Room y API sin duplicar eventos.
2. Diseñar deep links push robustos para multiples destinos.
3. Garantizar consistencia visual claro/oscuro en pantallas con estilos heredados.

### 6.2 Escalabilidad (v2.0)

1. Centro de notificaciones avanzado en app.
2. Configuracion granular de notificaciones por tipo/canal.
3. Envio real de codigo de recuperacion por email/SMS (actualmente pensado para integracion posterior).
4. Telemetria y observabilidad distribuida para sync/push.

---

## 7. Anexos

1. Repositorios:
   - App Android: https://github.com/IDS-HUGO/RED-UP-HILT
   - API: https://github.com/IDS-HUGO/API_UPRed
   - WebSocket: https://github.com/IDS-HUGO/WEBSOCKET-UPRED
2. Evidencia visual:
   - Capturas en modo claro y oscuro de Login, Home/Publicaciones, Perfil, Estado de sincronizacion y flujo de notificaciones.
3. Evidencia funcional sugerida:
   - Demo guiada de 3 escenarios: offline/online, push con deep link, recuperacion de contrasena.
