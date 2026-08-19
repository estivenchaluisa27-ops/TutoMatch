# TutoMatch — API y rutas

Aplicación SSR con Thymeleaf: la mayoría de rutas devuelven páginas HTML. Las de `/api/` son JSON (chat y utilidades). Autenticación vía cookie JWT.

## Páginas (SSR)

### Autenticación — `AuthController` (`/auth`)

| Método | Ruta | Descripción |
|---|---|---|
| GET/POST | `/auth/registro` | Registro de usuario (estudiante o tutor) |
| GET/POST | `/auth/login` | Inicio de sesión |
| POST | `/auth/logout` | Cerrar sesión |
| GET | `/auth/perfil` | Redirige a perfil propio |

### Home y búsqueda

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Portada (home) |
| GET | `/buscar` | Resultados de búsqueda por materia/tutor |
| GET | `/tutor/{id}` | Perfil público de un tutor |

### Reservas — `ReservaController` (`/reservas`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/reservas` | Listado de mis reservas |
| POST | `/reservas/crear` | Crear reserva de un bloque |
| POST | `/reservas/{id}/confirmar` | Tutor confirma (fija costo) |
| POST | `/reservas/{id}/cancelar` | Cancelar reserva |
| POST | `/reservas/{id}/marcar-impartida` | Tutor marca la sesión como impartida → `PENDIENTE_PAGO` |
| POST | `/reservas/{id}/pagar-token` | Estudiante paga con tokens → `FINALIZADA` |
| POST | `/reservas/{id}/cancelar-pago` | Cancelar antes del pago |

### Perfil de tutor — `TutorProfileController` (`/tutor`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/tutor/mi-perfil` | Edición del perfil |
| GET/POST | `/tutor/perfil` | Ver/actualizar perfil |
| POST | `/tutor/perfil/descripcion` | Actualizar descripción |
| POST | `/tutor/perfil/semestre` | Actualizar semestre |
| POST | `/tutor/perfil/materias` | Añadir materia al tutor |
| POST | `/tutor/perfil/materias/{materiaId}/eliminar` | Quitar materia |
| GET/POST | `/tutor/disponibilidad` | Gestión de bloques semanales |
| POST | `/tutor/disponibilidad/{id}/eliminar` | Eliminar bloque |

### Reseñas — `ResenaController` (`/resenas`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/resenas` | Mis reseñas |
| GET | `/resenas/nueva/{reservaId}` | Formulario de reseña (solo reserva `FINALIZADA`) |
| POST | `/resenas/guardar` | Guardar reseña |
| POST | `/resenas/eliminar/{id}` | Eliminar reseña propia |

### Wallet — `TokenController`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/wallet` | Saldo e historial de transacciones (paginado) |

### Admin — `AdminController` (`/admin`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/admin` | Dashboard de administración |
| — | `/admin/tutores` · `/admin/materias` · `/admin/resenas` · `/admin/configuracion` | Gestión de tutores, materias, reseñas y parámetros |

## API JSON

### Chat — `ChatRestController` (`/api/chat`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/chat/conversaciones` | Conversaciones del usuario autenticado |
| GET | `/api/chat/{convId}/mensajes` | Historial de mensajes |
| POST | `/api/chat/{convId}/leer` | Marcar conversación como leída |
| GET | `/api/chat/no-leidas` | Conteo de no leídas |
| GET | `/api/chat/{convId}/no-leidas` | Conteo por conversación |

WebSocket `/ws` (endpoint de `WebSocketConfig`) para mensajes en tiempo real; autenticado por token JWT en el handshake (`JwtHandshakeInterceptor`).

### Búsqueda

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/buscar/sugerencias` | Autocompletado (unaccent + ILIKE) |
| GET | `/api/me` | Datos del usuario actual |

### Notificaciones — `NotificacionController` (`/api/notificaciones`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/notificaciones` | Listado |
| GET | `/api/notificaciones/contar` | No leídas |
| POST | `/api/notificaciones/{id}/leer` | Marcar como leída |
| POST | `/api/notificaciones/leer-todas` | Marcar todas como leídas |

## Docs relacionados

- [Arquitectura](ARQUITECTURA.md)
- [Instalación](INSTALACION.md)
- [Índice](INDICE.md)