# Contenido

- [I. Requerimientos de negocio](#i-requerimientos-de-negocio)
  - [I.1. Generales](#i1-generales)
    - [I.1.1 Situación actual](#i11-situación-actual)
    - [I.1.2 Declaración de la visión de la solución](#i12-declaración-de-la-visión-de-la-solución)
    - [I.1.3 Funcionalidades principales](#i13-funcionalidades-principales)
  - [I.2.2 Limitaciones y exclusiones](#i22-limitaciones-y-exclusiones)
  - [I.3. Clases y características de usuarios](#i3-clases-y-características-de-usuarios)
    - [I.3.1 Perfil de los usuarios finales](#i31-perfil-de-los-usuarios-finales)
    - [I.3.2 Ambiente de operación](#i32-ambiente-de-operación)
  - [I.4. Procesos de negocio (épicas)](#i4-procesos-de-negocio-épicas)
    - [I.4.1 Identificar los procesos de negocio](#i41-identificar-los-procesos-de-negocio)
    - [I.4.1 Historias de usuario](#i41-historias-de-usuario)
  - [I.4. Productos mínimos viables](#i4-productos-mínimos-viables)
    - [I.4.1 Identificar los MVP](#i41-identificar-los-mvp)

---

# I. Requerimientos de negocio

## I.1. Generales

### I.1.1 Situación actual

En el contexto universitario, la comunicación entre estudiantes suele estar fragmentada entre redes sociales generales, grupos cerrados y canales informales, lo que dificulta:

- compartir información académica relevante de forma ordenada,
- encontrar compañeros para resolver dudas o colaborar,
- mantener conversaciones por temas (grupos) y por contacto directo (chat individual),
- conservar un historial útil para consulta posterior.

RED-UP surge como una aplicación móvil para centralizar publicaciones y comunicación en tiempo real dentro de una comunidad universitaria, con autenticación de usuarios y funcionalidades orientadas al entorno estudiantil.

### I.1.2 Declaración de la visión de la solución

Al implementarse RED-UP, los estudiantes dispondrán de una plataforma móvil unificada para:

- publicar contenido de interés académico/comunitario,
- participar en grupos de conversación,
- comunicarse mediante chats individuales,
- mantenerse conectados con indicadores en tiempo real (estado en línea y escritura),
- operar en un entorno confiable con sesión autenticada.

La visión considera una solución factible sobre arquitectura Android moderna (Jetpack Compose + Hilt + Navigation + Room + Retrofit + Socket), escalable por módulos y alineada a restricciones de costo/tiempo de desarrollo universitario.

### I.1.3 Funcionalidades principales

1. Registro e inicio de sesión de usuarios (correo/contraseña y biometría).
2. Feed de publicaciones con visualización, imagen, creación, edición y eliminación de contenido.
3. Comentarios en publicaciones: crear, listar y eliminar.
4. Perfil de usuario con datos reales, estadísticas y edición de biografía/teléfono.
5. Chat individual entre usuarios con historial e indicadores en tiempo real.
6. Chat grupal por sala con historial e indicadores en tiempo real.
7. Búsqueda de usuarios para iniciar conversaciones.
8. Indicadores en tiempo real (usuarios en línea y “escribiendo…”).
9. Cierre de sesión y control de acceso por autenticación JWT.

## I.2.2 Limitaciones y exclusiones

Para esta versión del producto, se excluyen o limitan las siguientes capacidades:

- Notificaciones push completas (en desarrollo).
- Intercambio multimedia avanzado en chats (en desarrollo).
- Cambio de foto de perfil desde la app (solo URL externa).
- Moderación avanzada de contenido (reportes, sanciones, panel administrativo).
- Analítica avanzada de uso y tableros de gestión.
- Soporte multiplataforma nativo fuera de Android.
- Operación offline completa para toda la mensajería (se prioriza tiempo real con conectividad).

## I.3. Clases y características de usuarios

### I.3.1 Perfil de los usuarios finales

La clasificación principal de usuarios para RED-UP es la siguiente:

| Id | Título del usuario | Descripción del rol | Frecuencia de uso |
|---|---|---|---|
| USER_CANDIDATO | Candidato | Estudiante de nivel medio superior o aspirante que busca conocer la oferta académica/comunidad universitaria y tener un primer contacto con estudiantes o grupos. | Regular durante la semana |
| USER_ESTUDIANTE | Estudiante activo | Usuario universitario que publica, participa en grupos y mantiene chats individuales para actividades académicas y sociales. | Alta (diaria) |
| USER_COLABORADOR | Colaborador académico/comunidad | Usuario que apoya difusión de información, coordinación en grupos y respuesta en conversaciones. | Regular-alta |

### I.3.2 Ambiente de operación

- **Distribución geográfica:** usuarios dispersos (campus, hogares, movilidad), con uso remoto.
- **Ventanas de mayor uso:** horarios fuera de clase, tardes/noches y periodos de entrega de actividades.
- **Origen de datos:** datos de publicaciones, usuarios, grupos y mensajes se generan desde la app móvil y se centralizan en backend.
- **Infraestructura técnica:** app Android (Jetpack Compose) + API REST + WebSocket para eventos en tiempo real.
- **Seguridad requerida:** autenticación de sesión, manejo de token, control básico de acceso a recursos y resguardo de credenciales para login biométrico.
- **Continuidad operativa:** uso frecuente en sesiones cortas con posibilidad de interrupciones de red; la app debe recuperar estado de forma robusta.

## I.4. Procesos de negocio (épicas)

### I.4.1 Identificar los procesos de negocio

Los procesos de negocio principales identificados para RED-UP son:

1. **PN01. Gestión de autenticación y acceso**
2. **PN02. Gestión de publicaciones**
3. **PN03. Gestión de chats individuales**
4. **PN04. Gestión de grupos y chats grupales**

### I.4.1 Historias de usuario

#### PN01. Gestión de autenticación y acceso

| Prioridad | Como | Necesito | Para | Estimación (hrs) |
|---|---|---|---|---|
| 01 | Estudiante | Registrarme con mis datos básicos | Crear una cuenta y acceder a la plataforma | 4 |
| 02 | Estudiante | Iniciar sesión con correo y contraseña | Usar mis funcionalidades personalizadas | 3 |
| 03 | Estudiante recurrente | Iniciar sesión con biometría cuando exista credencial guardada | Reducir fricción de acceso | 4 |

#### PN02. Gestión de publicaciones

| Prioridad | Como | Necesito | Para | Estimación (hrs) |
|---|---|---|---|-|
| 01 | Estudiante | Ver un feed de publicaciones con imágenes | Mantenerme informado de novedades universitarias | 4 |
| 02 | Estudiante | Crear publicaciones con imagen opcional | Compartir información con la comunidad | 5 |
| 03 | Autor de publicación | Editar/eliminar mi publicación | Corregir o retirar contenido cuando sea necesario | 5 |
| 04 | Estudiante | Ver y agregar comentarios en publicaciones | Interactuar y dar retroalimentación | 4 |

#### PN03. Gestión de chats individuales

| Prioridad | Como | Necesito | Para | Estimación (hrs) |
|---|---|---|---|---|
| 01 | Estudiante | Buscar usuarios por nombre/correo | Iniciar conversaciones directas | 4 |
| 02 | Estudiante | Enviar y recibir mensajes en tiempo real | Coordinar actividades académicas | 6 |
| 03 | Estudiante | Ver estado en línea y escritura | Mejorar la experiencia conversacional | 5 |

#### PN04. Gestión de grupos y chats grupales

| Prioridad | Como | Necesito | Para | Estimación (hrs) |
|---|---|---|---|---|
| 01 | Estudiante | Crear grupos y ver mis grupos | Organizar conversaciones por tema/equipo | 5 |
| 02 | Integrante de grupo | Entrar al chat grupal y enviar mensajes | Colaborar en tiempo real con el grupo | 6 |
| 03 | Creador o integrante autorizado | Invitar miembros al grupo | Ampliar el trabajo colaborativo | 4 |

## I.4. Productos mínimos viables

### I.4.1 Identificar los MVP

La evolución del sistema se plantea en incrementos funcionales.

#### MVP 01. Objetivo: Acceso y base social inicial  **[Completado]**

**Lista de funcionalidades esperadas**

- Registro de usuario.
- Inicio de sesión con correo/contraseña.
- Inicio de sesión biométrico (huella digital).
- Cierre de sesión.
- Navegación principal de la aplicación (barra inferior: Publicaciones / Chats / Perfil).

#### MVP 02. Objetivo: Comunidad y contenido  **[Completado]**

**Lista de funcionalidades esperadas**

- Visualización de feed con imágenes.
- Creación de publicaciones con imagen opcional.
- Edición/eliminación de publicaciones propias.
- Comentarios en publicaciones (crear, listar, eliminar).
- Perfil de usuario con datos reales, estadísticas y edición de biografía/teléfono.

#### MVP 03. Objetivo: Comunicación en tiempo real  **[Completado]**

**Lista de funcionalidades esperadas**

- Chat individual funcional con historial.
- Chat grupal funcional con historial.
- Búsqueda de usuarios para iniciar conversación.
- Indicadores de presencia y escritura en tiempo real.

#### MVP 04. Objetivo: Consolidación y mejoras  **[Pendiente]**

**Lista de funcionalidades esperadas**

- Notificaciones push.
- Cambio de foto de perfil desde la app.
- Moderación básica de contenido (reportar publicaciones).
- Mejoras de estabilidad, optimización de rendimiento y soporte offline parcial.

---

## Nota de trazabilidad con el proyecto

Este documento se construye en función del estado actual del repositorio RED-UP (Android) y su backend API_UPRed. Los MVP 01, 02 y 03 se han completado satisfactoriamente: autenticación con biometría, feed de publicaciones con imagen y comentarios funcionales, perfil con datos reales desde la API, y comunicación en tiempo real por WebSocket. La navegación inferior simplificada a tres secciones (Publicaciones, Chats, Perfil) refleja las funcionalidades disponibles y validadas en el producto.
