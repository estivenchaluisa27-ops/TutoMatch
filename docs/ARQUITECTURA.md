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
| `config/` | Beans de configuración, seed de admin (`DbSeed`), advice global |
| `security/` | JWT (provider, filter, handshake WS), `SecurityConfig`, `CustomUserDetailsService` |
| `controller/` | 17 controllers: páginas (SSR), REST de chat, WS |
| `service/` | 14 servicios: lógica de dominio y orquestación |
| `repository/` | Repositorios Spring Data JPA |
| `model/` | 14 entidades + enums |
| `dto/` | Objetos de transferencia |
| `validation/` `exception/` `util/` | Validadores, manejo de errores, utilidades (`AuthUtil`) |

## Dominio y entidades

| Entidad | Rol |
|---|---|
| `Usuario` | Estudiante o tutor; relación circular con `PerfilTutor` (deuda técnica conocida) |
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

- Al **confirmar** el tutor se fija el costo en tokens.
- Al **pagar**, `ReservaPagoService` debita de la wallet y acredita la reserva.
- `FINALIZADA` habilita la reseña.

## Economía de tokens (Fase 2.1)

- El estudiante recibe **5 tokens de bienvenida** al registrarse.
- `WalletConsultaService` / `WalletOperacionService`: interfaces ISP — separa lectura de escritura (clientes dependen solo de lo que usan).
- **Concurrencia**: `@Lock(PESSIMISTIC_WRITE)` en el repositorio + `@Version` en la entidad — evita doble débito si dos pagos se disparan a la vez.
- `ReservaPagoService` orquesta: validar saldo → debitar → cambiar estado → registrar transacción.

## Chat en tiempo real

- `ChatWsController` + `WebSocketConfig` + `JwtHandshakeInterceptor`: autenticación JWT en el handshake.
- Mensajes persistentes vía `ChatService`; historial por conversación.

## Búsqueda

- `SearchController`: autocompletado de materias/tutores con `unaccent` + `ILIKE` (insensible a acentos y mayúsculas en PostgreSQL).

## Decisiones y deudas técnicas

| Tema | Estado |
|---|---|
| Dependencia circular `Usuario` ↔ `PerfilTutor` | **ALTA** — riesgo de `LazyInitializationException`; requiere refactor (eliminar bidireccionalidad) |
| Fase 3 (masterclass) | Pendiente |
| `TokenServiceTest` y `ReservaPagoServiceTest` | Pendientes (cobertura de la economía de tokens) |
| Seed de admin | `DbSeed` crea el admin inicial con credenciales de `application-{profile}.properties` |

## Docs relacionados

- [Instalación](INSTALACION.md)
- [API y rutas](API.md)
- [Índice](INDICE.md)