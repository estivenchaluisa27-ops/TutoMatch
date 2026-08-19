# TutoMatch — API y rutas

Aplicación SSR con Thymeleaf: la mayoría de rutas devuelven páginas HTML. Las de `/api/` y los POST de `/auth` son JSON. Autenticación vía cookie JWT.

## Páginas (SSR)

### Autenticación

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/auth/registro` · `/auth/login` | Formularios (páginas SSR) |
| POST | `/auth/registro` | Registrar usuario (estudiante o tutor) — JSON |
| POST | `/auth/login` | Iniciar sesión — JSON |
| POST | `/auth/logout` | Cerrar sesión — JSON |
| GET | `/auth/perfil` | Datos del usuario autenticado — JSON |

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
| POST | `/reservas/{id}/confirmar` | Tutor confirma la reserva (costo fijo: 1 token) |
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
| GET | `/tutor/disponibilidad/semanal` | Disponibilidad semanal |
| POST | `/tutor/disponibilidad/semanal/guardar` | Guardar disponibilidad semanal |
| POST | `/tutor/disponibilidad/semanal/limpiar` | Limpiar disponibilidad semanal |

### Reseñas — `ResenaController` (`/resenas`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/resenas/nueva/{reservaId}` | Formulario de reseña (solo reserva `FINALIZADA`) |
| POST | `/resenas/guardar` | Guardar reseña |
| POST | `/resenas/eliminar/{id}` | Eliminar reseña (solo administradores) |

### Wallet — `TokenController`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/wallet` | Saldo e historial de transacciones (paginado) |

### Admin

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/admin` | Dashboard de administración |
| GET | `/admin/tutores` | Listado de tutores |
| POST | `/admin/tutores/{id}/verificar` | Verificar tutor |
| GET | `/admin/materias` | Gestión de materias |
| POST | `/admin/materias/agregar` · `/admin/materias/{id}/editar` · `/admin/materias/{id}/eliminar` | CRUD de materias |
| GET | `/admin/resenas` | Gestión de reseñas |
| POST | `/admin/resenas/{id}/eliminar` | Eliminar reseña (admin) |

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