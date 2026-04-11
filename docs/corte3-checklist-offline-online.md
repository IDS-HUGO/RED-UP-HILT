# Corte 3 - Checklist Offline/Online (Persona 1)

## Precondiciones

- Proyecto Firebase creado.
- `google-services.json` en `app/`.
- Backend con `FIREBASE_SERVICE_ACCOUNT_PATH` configurado.
- Usuario autenticado en app.

## Pruebas de token FCM

1. Instalar app y abrir sesión.
2. Verificar que `onNewToken` se ejecuta (logs).
3. Verificar en backend que dispositivo queda registrado.
4. Invalidar token y verificar reintento por `SyncWorker`.

## Pruebas push

1. Ejecutar `POST /api/notificaciones/push/test`.
2. Verificar notificación en foreground/background.
3. Tap en notificación de chat -> abre pantalla de chat.
4. Tap en notificación de publicación -> abre comentarios.

## Pruebas WorkManager

1. Forzar sync manual desde pantalla `sync_status`.
2. Verificar actualización de `lastSyncAt`.
3. Desactivar red y ejecutar sync -> debe quedar error/retry.
4. Reactivar red -> debe sincronizar y limpiar pendientes.

## Pruebas eventos diferidos

1. Generar evento no crítico (login/chat send metric).
2. Confirmar inserción en `pending_sync_events`.
3. Ejecutar sync con red.
4. Confirmar envío a `/api/notificaciones/eventos/sync`.
5. Confirmar eliminación de eventos `synced`.

## Pruebas cache resumen

1. Ejecutar sync.
2. Confirmar actualización de `notification_summary_cache`.
3. Verificar contador en pantalla de estado.

## Criterios de aceptación

- Token FCM queda asociado al usuario/dispositivo.
- Push se recibe y navega correctamente.
- WorkManager opera en reintentos con conectividad.
- Room mantiene estado técnico y cola diferida.
- Pantalla de sincronización refleja estado real y permite retry manual.
