# Diagrama de Flujo: FCM + WorkManager + Room

## Arquitectura de Sincronización

```mermaid
graph TD
    A[FCM Token Received] --> B[Save Token to AuthPreferences]
    B --> C[Enqueue Token Sync Work]
    C --> D[WorkManager Executes SyncWorker]

    D --> E{Is User Logged In?}
    E -->|No| F[Skip Sync]
    E -->|Yes| G[Build API Client with Auth]

    G --> H[Sync FCM Token to Backend]
    H --> I[Sync Remote Notification Config]
    I --> J[Sync Notification Summary Cache]
    J --> K[Flush Pending Events]

    K --> L[Update Sync Status in Room]
    L --> M[Work Success]

    D --> N{Error Occurred?}
    N -->|Yes| O[Retry Work]
    O --> D
    N -->|No| M
```

## Estrategia Room: Online-First + Fallback + TTL + Refresh Explícito

```mermaid
graph TD
    A[User Requests Data] --> B{Is Network Available?}
    B -->|Yes| C[Fetch from API]
    B -->|No| D[Check Local Cache]

    C --> E{API Success?}
    E -->|Yes| F[Save to Room with TTL]
    E -->|No| D

    D --> G{Valid Cache Exists?}
    G -->|Yes| H[Return Cached Data]
    G -->|No| I[Show Empty/Error State]

    F --> H
    H --> J[Display Data]

    I --> K[User Clicks Retry]
    K --> B
```

## Estados de Sincronización

- **Última Sync**: Timestamp de última sincronización exitosa
- **Pendientes**: Número de eventos pendientes por enviar
- **Errores**: Último error de sincronización
- **Botón Reintentar**: Fuerza sync inmediato

## Checklist de Pruebas Offline/Online

### Online Tests
- [ ] FCM token se registra correctamente al backend
- [ ] Sync periódico funciona cada 15 minutos
- [ ] Configuración remota se actualiza
- [ ] Eventos pendientes se envían cuando hay red
- [ ] Cache se refresca automáticamente

### Offline Tests
- [ ] App funciona sin red usando cache
- [ ] Eventos se almacenan localmente
- [ ] Sync se reintenta cuando regresa internet
- [ ] Pantalla de sync muestra estado correcto
- [ ] Notificaciones push llegan sin conexión a internet

### Edge Cases
- [ ] Cambio de red durante sync
- [ ] Token FCM expira y se renueva
- [ ] Backend no responde temporalmente
- [ ] Usuario cierra app durante sync
- [ ] Múltiples sync simultáneos