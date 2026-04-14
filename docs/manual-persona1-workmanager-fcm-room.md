# Manual Tecnico Persona 1 (Presentacion)

## Objetivo del manual

Este documento explica, de forma detallada y orientada a exposicion, todo lo integrado para Persona 1 en RED-UP:

1. WorkManager: que tipo de workers se usan, por que, donde y como.
2. Push Notifications con Firebase Cloud Messaging (FCM): arquitectura cliente-servidor, flujo de token y deep links.
3. Estrategia Room de sincronizacion: online-first con fallback, cache ligera y estado de sync.

---

## 1. Arquitectura general Persona 1

Se implemento un flujo end-to-end entre Android y API:

1. Android obtiene/actualiza token FCM.
2. Android registra token y dispositivo en backend.
3. Backend envia push por FCM cuando aplica.
4. Android recibe push y navega por deep link al modulo correcto.
5. Eventos diferidos y resumen de notificaciones se sincronizan con WorkManager.
6. Room mantiene estado local de sincronizacion y pendientes para resiliencia offline/online.

### Componentes clave (Android)

1. `SyncWork` y `SyncWorker` (WorkManager)
2. `UpRedFirebaseMessagingService` (recepcion FCM)
3. `NotificationDeepLink` + `MainScreen` (navegacion por payload)
4. `SyncDao` + `SyncEntities` + `AppDatabase` (estado local)
5. `SyncStatusScreen` (observabilidad de sync)

### Componentes clave (Backend)

1. `services/firebase_push_service.py` (envio FCM)
2. `routers/notificaciones.py` (registro token/dispositivo, sync y push test)
3. `routers/usuarios.py` (evento social: nuevo seguidor con push)

---

## 2. WorkManager: tipo, por que y donde

## 2.1 Que tipo de WorkManager se usa

En `SyncWork` se usan dos tipos de requests:

1. `OneTimeWorkRequest`:
   - Para acciones puntuales y reintentos confiables.
   - Acciones: `sync_token` y `force`.

2. `PeriodicWorkRequest`:
   - Para sincronizacion recurrente cada 15 minutos.
   - Accion: `periodic`.

Archivo principal:

1. `app/src/main/java/com/hugodev/red_up/core/sync/SyncWork.kt`

## 2.2 Politica de unicidad y por que

Se usan trabajos unicos (`enqueueUniqueWork` / `enqueueUniquePeriodicWork`) para evitar duplicados:

1. `UNIQUE_TOKEN_WORK`
2. `UNIQUE_FORCE_SYNC_WORK`
3. `UNIQUE_PERIODIC_SYNC_WORK`

Politicas:

1. `ExistingWorkPolicy.REPLACE` en `OneTime`: si llega un nuevo trigger mas reciente, reemplaza el anterior.
2. `ExistingPeriodicWorkPolicy.UPDATE` en `Periodic`: actualiza configuracion sin duplicar workers.

Beneficio:

1. Menos consumo de bateria.
2. Evita tormenta de jobs.
3. Estado determinista para debug y soporte.

## 2.3 Constraints y por que

Se exige red conectada:

1. `NetworkType.CONNECTED`

Motivo:

1. Las operaciones son de red (token, resumen, eventos, configuracion).
2. Si no hay red, WorkManager difiere ejecucion automaticamente.

## 2.4 Donde se agenda el periodic sync

En la clase `Application`:

1. `app/src/main/java/com/hugodev/red_up/DemoHiltApp.kt`

Durante `onCreate()`, se llama:

1. `SyncWork.schedulePeriodicSync(this)`

Esto garantiza que el ciclo de sincronizacion quede activo desde el arranque de la app.

## 2.5 Que hace exactamente `SyncWorker`

Archivo:

1. `app/src/main/java/com/hugodev/red_up/core/sync/SyncWorker.kt`

Flujo por accion:

1. `sync_token`:
   - Lee token FCM de `AuthPreferences`.
   - Registra dispositivo (`POST /api/notificaciones/dispositivos`).
   - Actualiza token (`PUT /api/notificaciones/dispositivos/token`).

2. `force` y `periodic`:
   - Repite sync de token.
   - Sincroniza config remota (`GET/PUT /api/notificaciones/configuracion`).
   - Actualiza resumen ligero (`GET /api/notificaciones/resumen`) y lo guarda en Room.
   - Envía en batch eventos diferidos (`POST /api/notificaciones/eventos/sync`).

Manejo de errores:

1. Si falla cualquier paso, persiste `lastError` en Room y retorna `Result.retry()`.
2. Si no hay sesion/token JWT, retorna `Result.success()` y solo actualiza estado local.

---

## 3. Push Notifications con FCM: donde, por que y como

## 3.1 Cliente Android

### Registro en Manifest

Archivo:

1. `app/src/main/AndroidManifest.xml`

Elementos importantes:

1. Permiso `POST_NOTIFICATIONS` (Android 13+).
2. Servicio `UpRedFirebaseMessagingService` con action `com.google.firebase.MESSAGING_EVENT`.

### Solicitud de permiso runtime

Archivo:

1. `app/src/main/java/com/hugodev/red_up/MainActivity.kt`

En Android 13+ se solicita `Manifest.permission.POST_NOTIFICATIONS` al iniciar.

### Servicio FCM

Archivo:

1. `app/src/main/java/com/hugodev/red_up/core/notifications/UpRedFirebaseMessagingService.kt`

Responsabilidades:

1. `onNewToken(token)`:
   - Guarda token en `AuthPreferences`.
   - Encola `SyncWork.enqueueTokenSync` para registrar en backend.

2. `onMessageReceived(remoteMessage)`:
   - Construye notificacion local.
   - Inserta extras de deep link en `Intent` a `MainActivity`.
   - Soporta payload de chat, publicacion y perfil social.

## 3.2 Modelo de deep link

Archivo:

1. `app/src/main/java/com/hugodev/red_up/core/notifications/NotificationDeepLink.kt`

Claves principales:

1. `target_type`
2. `room_id`, `room_name`, `room_type`
3. `publication_id`
4. `user_id`, `follower_user_id`

Targets definidos:

1. `chat`
2. `publicacion`
3. `perfil`

## 3.3 Consumo del deep link en UI

Archivo:

1. `app/src/main/java/com/hugodev/red_up/features/chat/main_shell/presentation/screens/MainScreen.kt`

Logica:

1. Lee extras del `intent` al iniciar.
2. Si `target_type=chat`, abre pantalla de chat.
3. Si `target_type=publicacion`, abre comentarios de la publicacion.
4. Si `target_type=perfil` o llega `follower_user_id` sin target, abre perfil de usuario.
5. Limpia extras para evitar re-navegacion accidental.

## 3.4 Backend FCM

### Servicio de envio

Archivo:

1. `API_UPRed/services/firebase_push_service.py`

Hace:

1. Inicializa Firebase Admin SDK si existe `FIREBASE_SERVICE_ACCOUNT_PATH`.
2. Envía `notification` + `data` con prioridad alta Android.

### Endpoints de soporte de notificaciones

Archivo:

1. `API_UPRed/routers/notificaciones.py`

Endpoints clave:

1. `POST /api/notificaciones/dispositivos`
2. `PUT /api/notificaciones/dispositivos/token`
3. `GET /api/notificaciones/configuracion`
4. `PUT /api/notificaciones/configuracion`
5. `POST /api/notificaciones/eventos/sync`
6. `GET /api/notificaciones/resumen`
7. `POST /api/notificaciones/push/test`

### Evento social: nuevo seguidor

Archivo:

1. `API_UPRed/routers/usuarios.py`

En `POST /api/usuarios/{usuario_id}/seguir`:

1. Crea relacion de seguimiento.
2. Crea notificacion interna `tipo=nuevo_seguidor`.
3. Intenta enviar push FCM al ultimo dispositivo activo del usuario seguido.
4. Incluye `follower_user_id` para deep link al perfil del seguidor.

---

## 4. Estrategia Room (online-first + fallback)

## 4.1 Donde esta implementada

Archivos:

1. `app/src/main/java/com/hugodev/red_up/core/data/local/SyncEntities.kt`
2. `app/src/main/java/com/hugodev/red_up/core/data/local/SyncDao.kt`
3. `app/src/main/java/com/hugodev/red_up/core/data/local/AppDatabase.kt`
4. `app/src/main/java/com/hugodev/red_up/core/sync/SyncEventStore.kt`

## 4.2 Entidades clave

1. `SyncStatusEntity`:
   - ultima ejecucion,
   - pendientes,
   - ultimo error.

2. `PendingSyncEventEntity`:
   - cola de eventos no criticos,
   - intentos,
   - ultimo intento,
   - estado (`pending`, `synced`).

3. `NotificationSummaryEntity`:
   - cache ligera de no leidas,
   - timestamp de ultima consulta.

## 4.3 Patron de sincronizacion

1. Online-first: cuando hay red, `SyncWorker` intenta enviar y actualizar remoto.
2. Fallback: si falla, conserva pendientes y deja error en estado local.
3. Reintento: WorkManager reprograma con `Result.retry()`.
4. Refresh explicito: desde UI, boton `Reintentar ahora`.

## 4.4 TTL basico

Se maneja TTL basico con marcas de tiempo (`lastFetchedAt`, `lastSyncAt`) para:

1. Evitar consultas innecesarias en ciclos cortos.
2. Mostrar antiguedad de datos en exposicion y diagnostico.

---

## 5. Pantalla de estado de sincronizacion

Archivos:

1. `app/src/main/java/com/hugodev/red_up/features/sync/presentation/viewmodels/SyncStatusViewModel.kt`
2. `app/src/main/java/com/hugodev/red_up/features/sync/presentation/screens/SyncStatusScreen.kt`

Muestra:

1. Ultima sincronizacion.
2. Cantidad de pendientes por enviar.
3. Cantidad de no leidas en cache.
4. Ultimo error.
5. Boton de reintento manual (`SyncWork.enqueueImmediateSync`).

Valor para presentacion:

1. Prueba de observabilidad operativa.
2. Evidencia de robustez offline/online.

---

## 6. Justificacion tecnica (argumentos para exponer)

## 6.1 Por que WorkManager y no solo coroutines directas

1. Sobrevive a cierre de app y reinicios del proceso.
2. Respeta restricciones del sistema Android moderno.
3. Reintentos y constraints gestionados por framework.
4. Mejor para tareas diferibles y no criticas en background.

## 6.2 Por que FCM con token por dispositivo

1. Un usuario puede tener multiples dispositivos.
2. Permite alta/baja/rotacion de token sin friccion.
3. Habilita notificaciones en tiempo real desacopladas del socket.

## 6.3 Por que Room para estado de sync

1. Estado persistente aun si app se cierra.
2. Auditoria de errores y pendientes local.
3. Mejor UX al mostrar estado real al usuario.

---

## 7. Flujos de demo para presentacion (recomendado)

## Escenario A: Token + registro de dispositivo

1. Instalar app y loguear.
2. Verificar `onNewToken`.
3. Ejecutar sync y validar backend en `dispositivos_usuario`.

## Escenario B: Push con deep link a chat/publicacion

1. Disparar `POST /api/notificaciones/push/test`.
2. Recibir push.
3. Tap en notificacion y verificar navegacion.

## Escenario C: Evento social nuevo seguidor

1. Usuario A sigue a Usuario B.
2. Usuario B recibe push.
3. Tap en notificacion abre perfil del seguidor.

## Escenario D: Offline/online con cola diferida

1. Forzar modo sin red.
2. Generar eventos diferidos.
3. Reconectar red.
4. Ver `pending -> synced` en pantalla de estado.

---

## 8. Riesgos conocidos y mejoras futuras

1. Endurecer migraciones Room (evitar `fallbackToDestructiveMigration` en produccion).
2. Añadir observabilidad remota (Sentry/OpenTelemetry) para fallos de sync.
3. Añadir canalizacion de envio de codigo forgot-password por email real.
4. Afinar reglas TTL por tipo de cache (chat/social/notificaciones).

---

## 9. Resumen ejecutivo para cerrar exposicion

Persona 1 queda cubierta con una arquitectura movil resiliente:

1. Push real-time (FCM) con deep links funcionales.
2. WorkManager para confiabilidad en segundo plano.
3. Persistencia Room para continuidad offline/online.
4. Pantalla de estado para transparencia operativa.
5. Integracion backend para token, sync y eventos sociales.

Con esto, la app no depende de que el usuario tenga la aplicacion abierta para mantener sincronizacion, ni pierde eventos criticos cuando hay intermitencia de red.