# TutoMatch — Arquitectura

Plataforma universitaria (UCE) que conecta estudiantes con tutores por materia: reserva de bloques de disponibilidad, pago con tokens y reseñas.

## Stack

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 4.0.7, Java 17, Maven |
| Frontend | Thymeleaf + HTML/CSS/JS (SSR), WebSocket (chat) |
| Seguridad | Spring Security + JWT (cookie httpOnly), BCrypt |
| Datos | PostgreSQL 16+, Spring Data JPA (Hibernate `ddl-auto=update`) |
| Importación | Apache POI (lectura de materias desde Excel) |
| Tests | Spring Boot Test + JPA Test |

## Estructura de paquetes (`com.uce.Tutomatch`)

| Paquete | Responsabilidad |
|---|---|
| `security/` | JWT (provider, filter, handshake WS), `CustomUserDetailsService` |
| `config/` | `SecurityConfig`, beans de configuración, seed de admin (`DbSeed`), advice global |
| `controller/` | 17 controllers: páginas (SSR), REST de chat, WS |
| `service/` | 14 servicios: lógica de dominio y orquestación |
| `repository/` | Repositorios Spring Data JPA |
| `model/` | 14 entidades + enums |
| `dto/` | Objetos de transferencia |
| `validation/` `exception/` `util/` | Validadores, manejo de errores, utilidades (`AuthUtil`) |

## Dominio y entidades

| Entidad | Rol |
|---|---|
| `Usuario` | Estudiante o tutor; relación unidireccional `PerfilTutor → Usuario` (LAZY) |
| `PerfilTutor` | Datos de tutoría: bio, materias, calificación promedio |
| `Materia` / `TutorMateria` | Catálogo de materias y relación tutor↔materia |
| `Disponibilidad` / `BloqueSemanal` | Bloques de tiempo ofertados por el tutor (semanal) |
| `Reserva` | Cita estudiante↔tutor con ciclo de vida por estados |
| `WalletToken` / `TransaccionToken` | Billetera de tokens y su historial |
| `Resena` | Valoración del estudiante tras la tutoría |
| `Conversacion` / `MensajeChat` | Chat en tiempo real estudiante↔tutor |
| `Notificacion` | Notificaciones in-app al usuario |
| `ConfiguracionSistema` | Parámetros globales (admin) |

## Ciclo de vida de una reserva

```
PENDIENTE ──(tutor confirma)──> CONFIRMADA ──(tutor marca impartida)──> PENDIENTE_PAGO
   │                                                                        │
   └──(cualquiera cancela)──> CANCELADA <────────(estudiante paga tokens)──┘
                                                                             │
                                                                             ▼
                                                                      FINALIZADA
```

- Al **confirmar**, el tutor confirma la reserva; el costo queda fijo en **1 token** (sin mecanismo actual para cambiarlo).
- Al **pagar**, `ReservaPagoService` debita la wallet del **estudiante** y acredita a la del **tutor**.
- `FINALIZADA` habilita la reseña.
- **Cancelación**: el tutor solo puede cancelar reservas `PENDIENTES`; en `PENDIENTE_PAGO` solo el estudiante puede cancelar.

## Economía de tokens (Fase 2.1)

- El estudiante recibe **5 tokens de bienvenida** al registrarse.
- `WalletConsultaService` / `WalletOperacionService`: interfaces ISP — separa lectura de escritura (clientes dependen solo de lo que usan).
- **Concurrencia**: `@Lock(PESSIMISTIC_WRITE)` en el repositorio + `@Version` en la entidad — evita doble débito si dos pagos se disparan a la vez.
- `ReservaPagoService` orquesta: validar saldo → debitar → cambiar estado → registrar transacción.

## Chat en tiempo real

- `ChatWsController` + `WebSocketConfig` + `JwtHandshakeInterceptor`: autenticación JWT en el handshake.
- Mensajes persistentes vía `ChatService`; historial por conversación.

## Búsqueda

- `SearchController`: autocompletado de **materias** con `unaccent` + `ILIKE`; búsqueda de tutores por materia, categoría o nombre (también `unaccent` + `ILIKE`).

## Decisiones y deudas técnicas

| Tema | Estado |
|---|---|
| Relación `PerfilTutor → Usuario` LAZY unidireccional | Riesgo de `LazyInitializationException` al acceder fuera de transacción (mitigado con `JOIN FETCH` en repositorios) |
| Fase 3 (masterclass) | Pendiente |
| `TokenServiceTest` y `ReservaPagoServiceTest` | Pendientes (cobertura de la economía de tokens) |
| Seed de admin | `DbSeed` crea el admin inicial con credenciales de `application-{profile}.properties` |

## Docs relacionados

- [Instalación](INSTALACION.md)
- [API y rutas](API.md)
- [Índice](INDICE.md)