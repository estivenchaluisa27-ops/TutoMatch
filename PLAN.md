# TutoMatch - Plan Maestro de Implementación

> **Documento vivo** — se actualiza con cada fase completada, decisiones técnicas, errores encontrados y correcciones aplicadas.
>
> **Fecha de inicio:** 13/06/2026  
> **Última actualización:** 22/06/2026  
> **Versión del plan:** 5.0.0  
> **Estado general:** 🟢 En Desarrollo — Funcionalidad core completa + Rediseño UI/UX integral (iconos SVG, glass navbar, circuit board, hero con física 2D, sistema notificaciones funcional)

---

## 🧠 INSTRUCCIONES PARA LA IA (Contexto de Sesión)

### ¿Qué es este documento?
Es el **plan maestro** del proyecto TutoMatch. Contiene el alcance completo, las fases de implementación, las decisiones tomadas y el registro de errores. Es la **única fuente de verdad** sobre el estado del proyecto.

### ¿Cómo debe leerlo la IA?
1. **Al inicio de cada sesión**, leer completamente este documento.
2. Identificar la **fase activa** (la primera con estado ⏳ Pendiente o 🔄 En progreso).
3. Revisar las **decisiones técnicas** en la Sección 9 — no re-preguntar lo ya resuelto.
4. Revisar el **registro de errores** en la Sección 10 para no repetir soluciones.
5. Si el usuario pide algo que contradice el plan, señalarlo antes de implementar.

### ¿Cómo se actualiza?
| Cambio | Cómo se marca |
|--------|--------------|
| Fase iniciada | `⏳ Pendiente` → `🔄 En progreso` |
| Tarea completada dentro de fase | `⏳ Pendiente` → `✅ Completado` |
| Fase terminada | Estado de la fase pasa a `✅ Completado` |
| Decisión tomada | Agregar fila en **Sección 9** con fecha, decisión y contexto |
| Error encontrado | Agregar fila en **Sección 10** con fase, error, causa y solución |
| Pendiente futuro | Agregar fila en **Sección 11** |

### Convenciones del documento
- Estados: ⏳ Pendiente | 🔄 En progreso | ✅ Completado | ❌ Bloqueado
- Prioridades: 🔴 Crítica | 🟠 Alta | 🟡 Media | 🟢 Baja | 🔵 Optimización
- Los RF y RNF en la **Sección 8** se actualizan al mismo tiempo que las fases.
- Las **métricas** al final se recalculan al completar cada fase.

### Stack del proyecto (no cambiar sin consultar)
- Java 17 + Spring Boot 4.0.7 + PostgreSQL + Thymeleaf + Bootstrap
- Arquitectura N-capas: Controller → Service → Repository → Entity
- Frontend: Thymeleaf (no React/Vue) — mobile-first con Bootstrap

### Documentos de referencia
- `Especificaciones.md`: Documento fuente con definiciones detalladas de RF, datos de encuesta y prototipos. Consultar solo si se necesita el detalle original de algún requerimiento.
- `PLAN-ICONOS-MATERIAS.md`: Plano completo del sistema de iconos SVG por materia (constantes, backend, frontend, bugs conocidos).
- ~~`Arquitectura.md`: Eliminado — su propósito era educativo y el patrón de capas ya está documentado en la Sección 2.~~

### Regla de oro
> 📌 **Este documento es la única fuente de verdad.** Toda nueva funcionalidad, entidad, endpoint o tabla debe respetar los módulos, prioridades y reglas de negocio aquí descritos. Si una solicitud del usuario entra en conflicto con este documento, señalarlo antes de implementar. Para detalles específicos de un RF, consultar `Especificaciones.md`.

---

## 📋 Índice de Contenido

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Metodología de Trabajo](#2-metodología-de-trabajo)
3. [Reglas de Negocio](#3-reglas-de-negocio)
4. [Modelo de Datos (PostgreSQL)](#4-modelo-de-datos-postgresql)
5. [Casos de Uso](#5-casos-de-uso)
6. [Flujo Principal del Sistema](#6-flujo-principal-del-sistema)
7. [Fases de Implementación](#7-fases-de-implementación)
8. [Seguimiento de RF y RNF](#8-seguimiento-de-rf-y-rnf)
9. [Decisiones Técnicas](#9-decisiones-técnicas)
10. [Registro de Errores y Correcciones](#10-registro-de-errores-y-correcciones)
11. [Pendientes y Consideraciones Futuras](#11-pendientes-y-consideraciones-futuras)

---

## 1. Resumen Ejecutivo

**Proyecto:** TutoMatch — Plataforma de Tutorías Académicas  
**Universidad:** Universidad Central del Ecuador (UCE)  
**Stack:** Java 17 + Spring Boot 4.0.7 + PostgreSQL + Thymeleaf + Bootstrap  
**Arquitectura:** N-capas (Controller → Service → Repository → Entity)  
**Patrón:** Modelo guía del profesor aplicado al dominio de las especificaciones  
**Alcance:** MVP completo sin módulo de pagos (flujo simplificado: Pendiente → Confirmada → Finalizada)  

**Objetivo del plan:** Capturar todo el alcance de `Especificaciones.md`, priorizar funcionalidades por dependencia lógica e impacto, y mantener un registro de avance actualizable.

---

## 2. Metodología de Trabajo

### Patrón de Implementación por Fase
Cada fase seguirá el patrón estructural del modelo guía (Arquitectura.md):

```
   NAVEGADOR
       │
       ▼
   [Controller]  ← Recibe petición HTTP
       │
       ▼
   [Service]     ← Lógica de negocio + @Transactional
       │
       ▼
   [Repository]  ← Acceso a datos (JPA)
       │
       ▼
   [Model/Entity]  ← Mapeo a tabla PostgreSQL
       │
       ▼
   [Thymeleaf View] ← Vista renderizada
```

### Convenciones del Proyecto
| Aspecto | Convención |
|---------|------------|
| Paquetes | `com.uce.Tutomatch.{capa}` |
| Entidades | PascalCase singular (`Usuario`) |
| Tablas DB | snake_case plural (`usuarios`) |
| Endpoints | kebab-case (`/mis-tutorias`) |
| Variables | camelCase (`deudaPendiente`) |

---

## 3. Reglas de Negocio

> Reglas que gobiernan el comportamiento del sistema. Tomadas de `Especificaciones.md` (Sección 7) y complementadas con decisiones de la revisión.

| # | Regla | RF Relacionado |
|----|-------|----------------|
| 1 | **Registro institucional obligatorio**: el correo debe pertenecer al dominio institucional de la UCE; se valida en el registro. | RF01 |
| 2 | **Perfiles duales**: un mismo usuario puede tener un registro asociado en `perfil_tutor`; los roles `SOLICITANTE` y `TUTOR` no son mutuamente excluyentes. | RF01 |
| 3 | **Verificación de tutor**: un tutor no aparece en resultados de búsqueda hasta que su perfil esté marcado como `verificado = true` (semestre validado). | RF02, RF04 |
| 4 | **Reserva**: un bloque horario solo puede tener una reserva activa (estado distinto de `CANCELADA`); validar a nivel de BD (constraint único) y servicio. | RF08 |
| 5 | **Calificación**: solo se permite calificar si `estado_reserva = FINALIZADA`. | RF15 |
| 6 | **Estados de reserva**: `PENDIENTE` → `CONFIRMADA` → `FINALIZADA` (o `CANCELADA` desde cualquier estado previo a `FINALIZADA`). | RF08 |
| 7 | **Política de cancelación**: solicitante cancela en PENDIENTE o CONFIRMADA; tutor solo en PENDIENTE; admin en cualquier estado previo a FINALIZADA. El bloque horario vuelve a LIBRE automáticamente. Se notifica a ambas partes. | RF08, RF09 |

---

## 4. Modelo de Datos (PostgreSQL)

> Entidades, campos y relaciones. Nombres de tabla en `snake_case`. Clases Java equivalentes en `PascalCase`.

### `usuarios`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK (UUID/serial) | |
| `correo_institucional` | String | Unique, Not Null, dominio @uce.edu.ec |
| `password_hash` | String | Not Null (bcrypt) |
| `nombre_completo` | String | |
| `rol_solicitante` | Boolean | |
| `rol_tutor` | Boolean | |
| `rol_admin` | Boolean | |
| `fecha_creacion` | Timestamp | |

### `perfiles_tutor`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `usuario_id` | FK → `usuarios.id` | Unique (1:1) |
| `semestre` | Integer | |
| `descripcion` | Text | |
| `verificado` | Boolean | Default false |
| `visible` | Boolean | Controlado por límite de crédito |
| `deuda_pendiente` | Numeric | Default 0 |
| `calificacion_promedio` | Numeric | Calculado vía AVG() en reseñas |

### `materias`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `nombre` | String | Not Null |
| `categoria` | String | |
| `semestre_referencial` | Integer | |

### `tutor_materias`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `perfil_tutor_id` | FK → `perfiles_tutor.id` | |
| `materia_id` | FK → `materias.id` | |
| `tarifa_hora` | Numeric | |

### `disponibilidad`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `perfil_tutor_id` | FK → `perfiles_tutor.id` | |
| `dia_semana` | Integer | 1-7 (recurrente semanal, modo MVP) |
| `hora_inicio` | Time | |
| `hora_fin` | Time | |
| `estado` | Enum | `LIBRE`, `RESERVADO` |

### `reservas`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `solicitante_id` | FK → `usuarios.id` | |
| `disponibilidad_id` | FK → `disponibilidad.id` | Unique con estado activo |
| `materia_id` | FK → `materias.id` | |
| `estado` | Enum | `PENDIENTE`, `CONFIRMADA`, `FINALIZADA`, `CANCELADA` |
| `fecha_creacion` | Timestamp | |

### `resenas`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `reserva_id` | FK → `reservas.id` | Unique (1:1) |
| `calificacion` | Integer | 1-5 |
| `comentario` | Text | |
| `fecha` | Timestamp | |

### `notificaciones`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `id` | PK | |
| `usuario_id` | FK → `usuarios.id` | |
| `tipo` | Enum | `RESERVA_CONFIRMADA`, `RESERVA_FINALIZADA`, etc. |
| `mensaje` | Text | |
| `leida` | Boolean | |
| `fecha` | Timestamp | |

### `configuracion_sistema`
| Campo | Tipo | Restricciones |
|-------|------|---------------|
| `clave` | String | PK |
| `valor` | String | |

---

## 5. Casos de Uso

| Código | Caso de Uso | Actor principal | RF relacionado |
|--------|-------------|----------------|----------------|
| CU01 | Registrar cuenta e iniciar sesión | Solicitante / Tutor | RF01 |
| CU02 | Configurar perfil como tutor | Tutor | RF02 |
| CU03 | Gestionar disponibilidad horaria | Tutor | RF03 |
| CU04 | Buscar tutores con filtros | Solicitante | RF04, RF05 |
| CU05 | Ver recomendaciones de tutores | Solicitante | RF06 |
| CU06 | Ver perfil y reseñas de tutor | Solicitante | RF07 |
| CU07 | Agendar/reservar tutoría | Solicitante | RF08, RF09 |
| CU08 | Calificar tutoría | Solicitante | RF15 |
| CU09 | Administrar plataforma | Administrador | — |

---

## 6. Flujo Principal del Sistema

> Flujo completo de agendamiento simplificado (sin módulo de pagos).

```
Solicitante                    Sistema                        Tutor
    │                            │                             │
    ├── Selecciona materia ──────►                             │
    │    (F3)                    │                             │
    ├── Busca tutor (filtros) ──► Valida disponibilidad ──────┤
    │    (F3)                    │  Verifica verificación     │
    │◄── Lista tutores OK ───────┤                             │
    │                            │                             │
    ├── Selecciona bloque ──────► Crea reserva (PENDIENTE) ──►│
    │    (F4)                    │  Notifica                   │  Recibe notificación
    │                            │                             │
    │                            │◄── Tutor acepta ───────────┤
    │                            │  (F4)                       │
    │◄── Notificación ───────────┤  Reserva → CONFIRMADA       │
    │    confirmación            │                             │
    │                            │◄── Tutor finaliza ─────────┤
    │                            │  Reserva → FINALIZADA      │
    ├── Califica tutoría ───────► Actualiza promedio ─────────┤
    │    (F6)                    │  (RF15)                    │
    │                            │                             │
```

### Paso a paso textual:

1. **Solicitante** selecciona materia → tutor → bloque horario.
2. **Backend** valida disponibilidad del bloque.
3. Si está disponible → se crea la reserva en estado `PENDIENTE`.
4. **Tutor** acepta la solicitud desde "Mis Tutorías" → reserva pasa a `CONFIRMADA`. Se notifica al solicitante.
5. **Tutor** finaliza la tutoría → reserva pasa a `FINALIZADA`.
6. Se envían notificaciones internas a ambos usuarios en cada cambio de estado (RF09).
7. **Solicitante** califica la tutoría una vez `FINALIZADA` (RF15).

---

## 7. Fases de Implementación

### ✅ FASE 0 - Preparación y Configuración Inicial
> **Estado Actual:** Completado  
> **Prioridad:** Crítica (bloquea todas las demás)  
> **RF Cubiertas:** Ninguna directamente (infraestructura)  
> **RNF Cubiertas:** RNF20, RNF21

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Validar y limpiar `pom.xml` | ✅ Completado | Se agregaron dependencias de seguridad, validación y JWT |
| Estructurar paquetes base (`Model`, `Repository`, `Service`, `Controller`) | ✅ Completado | Seguir patrón del modelo guía |
| Configurar `application.properties` | ✅ Completado | Conexión DB, JPA, logging, seguridad |
| Configurar bean `PasswordEncoder` (bcrypt) | ✅ Completado | Necesario desde Fase 1 (registro/login) |
| Crear `ConfiguracionSistema` (entidad de parámetros globales) | ✅ Completado | Tabla `configuracion_sistema` para comisión % y límite de crédito |
| Seed básico de datos | ✅ Completado | Materias y parámetros globales insertados |

**Decisiones Técnicas:**
- `ddl-auto=update` para desarrollo histórico.
- Fecha/hora en UTC, conversión a UTC-5 en frontend.

**Dependencias de entorno de producción (no código):**
- RNF17 (disponibilidad ≥ 99%) y RNF18 (recuperación < 5 min) requieren configuración de infraestructura (backups automáticos, monitoreo, health checks), no son tareas de código.

**Errores/Bloqueos:**
- [ ] Pendiente de registro.

---

### ✅ FASE 1 - Autenticación, Usuarios y Perfiles (Cimientos)
> **Estado Actual:** Completado — app funcionando en localhost:8080, usuarios guardándose en PostgreSQL  
> **Prioridad:** Crítica (bloquea todas las demás)  
> **RF Cubiertas:** RF01, RF02 (parcial)  
> **RNF Cubiertas:** RNF09, RNF11, RNF12, RNF14, RNF15, RNF16

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Entidad `Usuario` (email institucional, password, roles) | ✅ Completado | Roles: SOLICITANTE, TUTOR, ADMIN (no mutuamente excluyentes) |
| Validación de dominio `@uce.edu.ec` | ✅ Completado | Custom validator `@InstitutionalEmail` |
| Spring Security + JWT | ✅ Completado | Tokens con expiración 30 min, cookie HttpOnly + Secure |
| Entidad `PerfilTutor` (1:1 con Usuario) | ✅ Completado | Semestre, verificado, visible, deuda_pendiente |
| CRUD básico de usuario (registro / login) | ✅ Completado | AuthController + UsuarioService + Repositories |
| Vistas: `login.html`, `registro.html` | ✅ Completado | Batch 4 - Mobile-first con Bootstrap, atributos aria |
| Endpoint `/` (Home público) | ✅ Completado | HomeController + home.html (Batch 4) |


**Decisiones Técnicas (Resueltas en ERS):**
- ✅ **JWT** (definido en RNF11, Especificaciones 3.85). Se almacena en **cookie HttpOnly** para proteger contra XSS (RNF15).
- ✅ **Almacenamiento de tokens:** Cookie HttpOnly + Secure (RNF15 - prevención XSS).
- ✅ **API Híbrida:** Backend como REST API (`@RestController`) consumido por vistas Thymeleaf SSR.

**Errores/Bloqueos:**
- 14/06/2026 | Fase 1 (Batch 3) | Error de integridad en registro de tutores | `perfilTutorRepository.save()` antes de `usuarioRepository.save()` causaba FK null | Corregido usando `CascadeType.ALL` y guardando solo el padre `Usuario` | ✅ Corregido
- 14/06/2026 | Fase 1 (Batch 3) | Códigos HTTP 500 en errores de validación | Falta `@ControllerAdvice` para mapear excepciones de dominio | Implementado `GlobalExceptionHandler` con códigos 400/401/409 | ✅ Corregido


---

### ✅ FASE 2 - Materias, Perfil Completo del Tutor y Disponibilidad
> **Estado Actual:** Completado  
> **Prioridad:** Crítica (bloquea búsqueda y agendamiento)  
> **RF Cubiertas:** RF02 (completo), RF03  
> **RNF Cubiertas:** RNF10 (verificación de tutores)

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Catálogo de `Materia` (entidad + seed) | ✅ Completado | Fase 0 — 12 materias en 4 categorías |
| Entidad `TutorMateria` (N:M tutor-materia con tarifa) | ✅ Completado | `TutorMateria.java` con `@ManyToOne` a PerfilTutor y Materia + tarifa_hora |
| Gestionar perfil de tutor (materias, descripción, tarifa, semestre) | ✅ Completado | `PerfilTutorService` — CRUD completo con validación de semestre y materias duplicadas |
| Entidad `Disponibilidad` (bloques horarios) | ✅ Completado | `Disponibilidad.java` con dia_semana, hora_inicio/fin, estado (LIBRE/RESERVADO) |
| CRUD de disponibilidad | ✅ Completado | `DisponibilidadService` — agregar/eliminar con validación de solapamiento |
| Verificación de tutores (admin) | ✅ Completado | `AdminController` — endpoint POST `/admin/tutores/{id}/verificar` + vista `admin-tutores.html` |
| Vistas: `perfil-tutor.html`, `configurar-disponibilidad.html`, `admin-tutores.html` | ✅ Completado | Formularios SSR con Bootstrap + flash messages |

**Decisiones Técnicas (Resueltas para MVP):**
- ✅ **Granularidad de bloques:** 1 hora por defecto, configurable desde `configuracion_sistema` (clave `duracion_bloque_horas`).
- ✅ **Disponibilidad:** Recurrente semanal (día de semana + hora). Modo puntual (fecha específica) se pospone a post-MVP.

**Errores/Bloqueos:**
- 14/06/2026 | `configurar-disponibilidad.html` | Whitelabel 500 al cargar página | `#numbers.sequence(0, 6)` no existe en Thymeleaf y `diaIter.index` intenta acceder a `.index` en Integer | Iterar sobre lista `dias` con `diaStat.index` | ✅ Corregido
- 14/06/2026 | `configurar-disponibilidad.html` | Whitelabel 500 al agregar bloque | Stream + lambda en SpEL (`bloques.stream().filter(b -> ...).toList()`) no parseable por Thymeleaf | Agrupar bloques por día en el Controller (`bloquesPorDia: Map<Integer, List<Disponibilidad>>`) | ✅ Corregido
- 14/06/2026 | `JwtAuthenticationFilter.java` | Whitelabel 500 con JWT de usuario eliminado | `UsernameNotFoundException` no atrapado en el filter | Try-catch + log warning, continúa sin autenticación | ✅ Corregido
- 14/06/2026 | `layout.html` | Mensaje genérico "Ha ocurrido un error" en overlap | Error del controller no mapeado en `th:switch` | Mostrar mensaje real con `param.error[0]` en caso default | ✅ Corregido

---

### ✅ FASE 3 - Búsqueda, Catálogo y Perfil Público
> **Estado Actual:** Completado  
> **Prioridad:** Alta (valor central del producto)  
> **RF Cubiertas:** RF04, RF05, RF06, RF07  
> **RNF Cubiertas:** RNF04, RNF05 (performance de búsqueda)

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Búsqueda de tutores con filtros | ✅ Completado | `PerfilTutorRepository.buscarTutores()` con JOIN a materias, filtros: materia (text), categoria, maxPrecio, minCalificacion, semestre |
| Índices PostgreSQL para optimización | ⏳ Pendiente | Pospuesto a Fase 8 (pulido final); con pocos datos no hay impacto |
| Pantalla Home con materias y recomendaciones | ✅ Completado | `materiasPorCategoria` agrupado en Controller + `top 6` tutores por calificación (`obtenerRecomendados()`) |
| Perfil público del tutor | ✅ Completado | `/tutor/{id}` con datos, materias con tarifas, disponibilidad agrupada por día, CTA para registrarse |
| Ocultar tutores no verificados o con deuda excesiva | ✅ Completado | Query `buscarTutores` y `findTop6` filtran por `verificado=true AND visible=true` |
| Vistas: `home.html`, `resultados.html`, `perfil-publico.html` | ✅ Completado | Mobile-first con Bootstrap, Thymeleaf SSR |

**Decisiones Técnicas:**
- ✅ **Búsqueda con `@Query` JPQL:** JOIN con filtros opcionales (IS NULL = ignorar filtro). Sin paginación aún (pocos registros en desarrollo).
- ✅ **Recomendaciones:** Top 6 por `calificacionPromedio DESC` con `verificado=true AND visible=true`.
- ✅ **Home dinámico:** Materias agrupadas por categoría desde BD + tarjetas de tutores recomendados con enlace a perfil público.

**Errores/Bloqueos:**
- [ ] Pendiente de registro.

---

**Nuevas funcionalidades post-Fase 3:**
- 14/06/2026 | Se agregó ruta `/tutor/mi-perfil` para que cualquier usuario autenticado vea sus datos (nombre, correo, roles) y, si es tutor, enlace a su perfil público. Se creó template `mi-perfil.html`.
- 14/06/2026 | Menú "Mi Cuenta" actualizado: "Ver Mi Perfil", "Editar Perfil", "Disponibilidad".

### 🟠 FASE 4 - Reservas, Estados y Notificaciones
> **Estado Actual:** Completado  
> **Prioridad:** Alta (core del negocio)  
> **RF Cubiertas:** RF08, RF09  
> **RNF Cubiertas:** RNF07 (procesamiento < 3s), RNF22 (fiabilidad de reservas)

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Entidad `Reserva` con máquina de estados | ✅ Completado | Estados: PENDIENTE → CONFIRMADA → FINALIZADA / CANCELADA |
| Validación de disponibilidad en tiempo real | ✅ Completado | Verifica estado LIBRE y que no exista reserva activa en el bloque |
| Creación de reserva (PENDIENTE) | ✅ Completado | Verifica disponibilidad, estado del tutor, materia ofrecida, y auto-reserva |
| Confirmación de reserva por el tutor (PENDIENTE → CONFIRMADA) | ✅ Completado | Nuevo método `confirmar()` en servicio + endpoint `POST /reservas/{id}/confirmar` + botón "Aceptar" en vista "Como Tutor" |
| Transición de estados con validaciones | ✅ Completado | Cancelación con políticas por rol (solicitante/tutor/admin) y confirmación solo por tutor |
| Entidad `Notificacion` (tabla interna) | ✅ Completado | Tipo, mensaje, leída, fecha |
| Generar notificaciones en cambios de estado | ✅ Completado | Creadas al solicitar, confirmar y cancelar reservas |
| Vistas: `mis-tutorias.html` (listado con estado) | ✅ Completado | Tabs: Como Solicitante / Como Tutor, con botones Aceptar + Cancelar |

**Decisiones Técnicas:**
- ✅ **Concurrencia:** Validación en servicio que verifica estado LIBRE + sin reserva activa no cancelada.
- ✅ **Política de cancelación (Regla 11):**
  - Solicitante puede cancelar en estado `PENDIENTE` o `CONFIRMADA`
  - Tutor puede cancelar solo en `PENDIENTE`
  - Admin puede cancelar en cualquier estado previo a `FINALIZADA`
   - Al cancelar, el bloque horario vuelve a `LIBRE` automáticamente
  - Se notifica a ambas partes en toda cancelación
- ✅ **Confirmación por tutor:** Solo el tutor asociado a la disponibilidad puede confirmar una reserva, y solo desde estado `PENDIENTE`. Al confirmar, se notifica al solicitante y la reserva pasa a `CONFIRMADA`.
- ✅ **Formulario de solicitud** integrado en perfil público (`perfil-publico.html`) con selector de materia + bloque horario.
- ✅ **Finalización por tutor:** El tutor marca la tutoría como `FINALIZADA` desde el estado `CONFIRMADA` mediante el botón "Finalizar" en Mis Tutorías.

**Errores/Bloqueos:**
- [ ] Sin errores registrados.

---

### ✅ FASE 5 - Calificaciones, Reseñas y Reputación
> **Estado Actual:** Completado  
> **Prioridad:** Media (impacta confianza pero no bloquea MVP)  
> **RF Cubiertas:** RF15  
> **RNF Cubiertas:** —

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Entidad `Resena` (calificación + comentario) | ✅ Completado | 1:1 con Reserva, calificación 1-5, fecha automática |
| Validación: solo si FINALIZADA (no requiere PAGADA, pues DIGITAL ya pagó) | ✅ Completado | Regla 9 real: solo FINALIZADA, una por reserva, solo el solicitante |
| Cálculo automático de calificación promedio del tutor | ✅ Completado | AVG() en ResenaRepository + actualización en PerfilTutor al crear/eliminar |
| Vistas: formulario de reseña, listado en perfil público | ✅ Completado | Formulario con estrellas interactivas + listado en perfil público |
| Moderación de reseñas: admin puede eliminar contenido abusivo | ✅ Completado | `POST /resenas/eliminar/{id}` valida que sea admin |

**Decisiones Técnicas:**
- ✅ **Validación:** Solo reserva FINALIZADA, solo el solicitante dueño de la reserva, una sola reseña por reserva.
- ✅ **Calificación 1-5:** Validada en servicio antes de guardar.
- ✅ **Promedio calculado:** Query `AVG(calificacion)` en repositorio + `BigDecimal.setScale(2)` para redondeo.
- ✅ **Eliminación por admin:** `ResenaController.eliminar()` recibe id y verifica que el usuario sea admin (correo `admin@uce.edu.ec`).
- ✅ **Reseñas en perfil público:** Listado completo en `perfil-publico.html` con estrellas renderizadas y nombre del solicitante.
- ✅ **Botón Calificar:** Aparece en `mis-tutorias.html` solo para FINALIZADA sin reseña existente. Ya calificadas muestran "Calificado".
- ✅ **Flash messages:** Layout ya manejaba `success`/`error` de RedirectAttributes.

**Errores/Bloqueos:**
- [ ] Sin errores registrados.

---

### ✅ FASE 6 - Panel Administrativo y Reportes (simplificado, sin módulo de pagos)
> **Estado Actual:** Completado  
> **Prioridad:** Media (gestión interna)  
> **RF Cubiertas:** CU09 (gestión admin)  
> **RNF Cubiertas:** —

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Dashboard de administración con KPIs | ✅ Completado | Métricas en tiempo real: usuarios, tutores, reservas. Vista `admin-dashboard.html` |
| Aprobar/rechazar verificación de tutores | ✅ Completado | Ampliado desde Fase 2 con `AdminController` mejorado y tabla completa |
| Gestión de materias (CRUD del catálogo) | ✅ Completado | Modal de crear/editar + eliminar en `admin-materias.html`. Catálogo expandible sin redeploy |
| Eliminación de reseñas abusivas | ✅ Completado | Vista `admin-resenas.html` con listado completo y botón eliminar |

**Decisiones Técnicas (Seguridad de Fase 7):**
- Se implementó **"Cierre Híbrido"** en `SecurityConfig.java`: Rutas `/admin/**` protegidas con `hasRole('ADMIN')`, mientras `/auth/**`, `/buscar`, `/tutor/*` y assets permanecen `permitAll()`.
- Se corrigió el conflicto de `403 Forbidden` en modo STATELESS: Se agregó `AuthenticationEntryPoint` que redirige a `/auth/login` para usuarios no autenticados.

**Errores/Bloqueos:**
- Ninguno registrado.

---

### ✅ FASE 7 - Optimización, Testing y Pulido Final
> **Estado Actual:** Completado  
> **Prioridad:** Baja (pero necesaria para salida digna)  
> **RF Cubiertas:** —  
> **RNF Cubiertas:** RNF01-RNF08, RNF17-RNF22

| Tarea | Estado | Decisiones / Notas |
|-------|--------|------------------|
| Paginación con `Pageable` en todas las listas | ✅ Completado | 8 repositorios actualizados con Page<> overloads. Page size=10 default. Navegación en admin-tutores.html |
| Manejo de errores global | ✅ Completado | `GlobalExceptionHandler` consolidado con logging + AccessDeniedException + DataIntegrityViolationException |
| Validaciones con `@Valid` + `jakarta.validation` | ✅ Completado | 8 DTOs creados con @NotBlank, @Min/@Max, @Pattern. 6 controladores actualizados con @Valid @ModelAttribute + BindingResult |
| Layout maestro Thymeleaf | ✅ Completado | 5 fragments (head, navbar, footer, flash-messages, scripts). layout.html con th:replace. 15 templates convertidos con th:with="pageTitle" |
| Testing: servicios de reservas y usuarios (unitario) | ✅ Completado | 26 tests: ReservaService (16), UsuarioService (9), context (1). Mockito + JUnit 5. **Todos pasando** |
| Testing: flujo completo de agendamiento (integración) | 🟡 Pospuesto | Pendiente de H2/Testcontainers para base de datos en memoria |
| Testing: contexto Spring | ✅ Completado | `TutomatchApplicationTests.contextLoads()` — verifica contexto Spring con PostgreSQL |
| Variables de entorno para credenciales | ✅ Completado | `application-{dev,prod}.properties` con perfiles Spring. `${DB_PASSWORD}`, `${JWT_SECRET}`, etc. en prod |
| Revisión mobile-first final | ✅ Completado | Bootstrap 5 responsive en todas las vistas |
| Revisión de accesibilidad WCAG 2.1 AA | ✅ Completado | aria-label en tablas, icon-buttons, breadcrumbs, navs. Keyboard events + role en estrella rating. for/id en labels de resultados.html |

**Decisiones Técnicas Fase 8:**
- **DTOs con @Valid + BindingResult:** Para formularios POST, BindingResult captura errores y permite redirects con flash messages
- **application-{dev,prod}.properties:** Dev con defaults inline para desarrollo local; Prod con `${VAR}` estrictos (fail fast si no hay env var)
- **JOIN FETCH en repositorios:** Eliminación de N+1 queries en buscarTutores, reservas paginadas, reseñas y home (PerfilTutorRepository, ReservaRepository, ResenaRepository)
- **Aria-label en tablas:** Se agregó a todas las `<table>` para cumplir WCAG 2.1 AA

**Errores/Bloqueos:**
- [ ] Sin errores registrados.

---

### ✅ FASE 8 — Sistema de Iconos SVG por Materia
> **Estado Actual:** Completado  
> **Prioridad:** Media (mejora visual + funcional)  
> **Commits:** `397fee2`, `a931839`, `2168388`

| Tarea | Estado | Notas |
|-------|--------|-------|
| Campo `icono` (TEXT) en entidad `Materia` | ✅ Completado | Almacena SVG inline por materia |
| Campo `descripcion` en `Materia` | ✅ Completado | Descripción textual de cada materia |
| 7 constantes SVG en `DbSeed.java` (code, calculator, database, physics, flask, chart, matrix) | ✅ Completado | Usan `currentColor` para heredar color CSS por categoría |
| Seed de 12 materias con iconos y descripciones | ✅ Completado | Mapeo por nombre de materia → constante SVG |
| CRUD admin de materias con campo icono | ✅ Completado | Modal Bootstrap en `admin-materias.html`, SVG inline renderizado con `th:utext` |
| Renderizado de iconos en templates (home, perfiles, resultados) | ✅ Completado | Patrón `th:utext` con fallback a `bi bi-book` |
| Colores por categoría vía `data-categoria` + CSS `[attr]` selectors | ✅ Completado | Informática → azul, Matemáticas → rojo, Ciencias → verde, Administración → púrpura |

**Decisiones:**
- Todos los SVG usan `currentColor` para heredar el color por categoría desde CSS
- Fallback a Bootstrap Icons (`bi bi-book`) cuando `icono == null`
- Admin puede pegar cualquier SVG manualmente en el campo icono del CRUD

---

### ✅ FASE 9 — Rediseño Estético Integral (UI/UX)
> **Estado Actual:** Completado  
> **Prioridad:** Alta (identidad visual del producto)  
> **Commits:** `3af18d3`, `a931839`, `2168388`, `d6e9dee`, `03ae84f`, `84229d5`, `a7ce472`

| Tarea | Estado | Notas |
|-------|--------|-------|
| Paleta de color vibrante basada en turquesa/teal (`#0d9488`) | ✅ Completado | Variables CSS: `--brand`, `--brand-light`, `--brand-dark` |
| Hero section con gradiente turquesa + iconos flotantes animados (CSS keyframes) | ✅ Completado | 4 iconos SVG (graduación, plus, globo, chat) con `float-icon` animation |
| Animaciones stagger-fade en secciones del home | ✅ Completado | `@keyframes stagger-fade-up` con delays escalonados |
| Animaciones card-fade-in en tarjetas de materias y tutores | ✅ Completado | `@keyframes card-fade-in` con scale + opacity |
| Glass navbar con efecto blur y bordes semitransparentes | ✅ Completado | `backdrop-filter: blur(12px)` + `border-bottom` sutil |
| Fondo con patrón circuit board + gradientes + noise textura | ✅ Completado | SVG pattern repetido, radial gradients, pseudo-elemento noise |
| Escalado y responsividad de iconos SVG en cards | ✅ Completado | `width: 3em; height: 3em` con hover scale |
| Admin compacto: iconos 24px sin animaciones | ✅ Completado | CSS específico para `.card-icon` en admin |
| Cache-buster para CSS (`?v=` + timestamp) | ✅ Completado | Forza recarga de estilos tras cambios |
| Seguridad por roles refinada + navbar admin sin enlaces usuario | ✅ Completado | Admin solo ve enlaces del panel |
| Notificaciones funcionales con campana y dropdown | ✅ Completado | Fragmento `scripts.html` con polling cada 30s, badge contador, marcar leídas |
| Navbar glass tintado + patrón circuit | ✅ Completado | Efecto glass mejorado con patrón de fondo |

**Decisiones:**
- Diseño mobile-first con Bootstrap 5, animaciones degradan graceful en mobile
- Las animaciones CSS se habilitan vía clases `.stagger-fade` y `.card-fade-in` — no afectan rendimiento en pantallas pequeñas
- Notificaciones: poll cada 30s al backend, sin WebSockets (MVP)
- Cache-buster con timestamp de compilación para evitar estilos cacheados

---

### ✅ FASE 10 — Hero con Física 2D (Colisiones Realistas)
> **Estado Actual:** Completado  
> **Prioridad:** Baja (embellecimiento)  
> **Commits:** No commiteado aún (última sesión)

| Tarea | Estado | Notas |
|-------|--------|-------|
| Motor de físicas JavaScript con `requestAnimationFrame` | ✅ Completado | `static/js/hero-physics.js` — 96 líneas |
| 10 iconos SVG totales (4 originales + 6 de materias) | ✅ Completado | Code `<>`, calculadora, base de datos, átomo, matraz, gráfica |
| Posición inicial aleatoria, velocidad, tamaño (32-56px) y rotación | ✅ Completado | Cada icono es único en trayectoria |
| Rebote contra bordes del contenedor `.hero-section` | ✅ Completado | Inversión de velocidad en colisión con pared |
| Colisiones elásticas entre íconos | ✅ Completado | Detección por distancia, corrección de superposición, intercambio de momento |
| Rotación suave variable por icono | ✅ Completado | `rotSpeed` aleatorio |
| Oculto en mobile (< 768px) | ✅ Completado | `display: none` en media query |
| Reemplazo de animación CSS anterior (`@keyframes float-icon`) | ✅ Completado | Se eliminaron nth-child rules + keyframes |

**Decisiones:**
- DOM-based (no Canvas) para mantener los SVG inline y su semántica
- Colisiones elásticas 2D con masas proporcionales al tamaño del icono
- `will-change: transform` en CSS para optimizar rendering

---


## 8. Seguimiento de RF y RNF

### Requerimientos Funcionales (RF)

| ID | Descripción | Fase | Estado |
|----|-------------|------|--------|
| RF01 | Registro e inicio de sesión (correo institucional) | 1 | ✅ Completado |
| RF02 | Configuración de perfil de tutor | 2 | ✅ Completado |
| RF03 | Gestión de disponibilidad horaria | 2 | ✅ Completado |
| RF04 | Búsqueda y filtros académicos | 3 | ✅ Completado |
| RF05 | Catálogo estructurado de materias | 3 | ✅ Completado |
| RF06 | Recomendaciones de tutores | 3 | ✅ Completado |
| RF07 | Visualizar perfil del tutor | 3 | ✅ Completado |
| RF08 | Reserva/Agendamiento de tutoría | 4 | ✅ Completado |
| RF09 | Sincronización de estados y notificaciones | 4 | ✅ Completado |
| RF15 | Sistema de calificación y reseñas | 5 | ✅ Completado |

### Requerimientos No Funcionales (RNF)

| ID | Descripción | Fase | Estado |
|----|-------------|------|--------|
| RNF01 | Mobile-first estricto | 3, 8 | ✅ Completado |
| RNF02 | Curva de aprendizaje ~0 | 3, 8 | ⏳ Pendiente |
| RNF03 | Accesibilidad WCAG 2.1 AA | 1, 3, 8 | ✅ Completado |
| RNF04 | Tiempo de carga ≤ 2 segundos | 3, 8 | ⏳ Pendiente |
| RNF05 | Búsqueda < 1,5 segundos | 3, 8 | ⏳ Pendiente |
| RNF06 | 200 usuarios concurrentes | 8 | ⏳ Pendiente |
| RNF07 | Procesamiento de reservas < 3 segundos | 4, 8 | ✅ Completado |
| RNF08 | Escalabilidad horizontal | 8 | ⏳ Pendiente |
| RNF09 | Registro institucional | 1 | ✅ Completado |
| RNF10 | Verificación de tutores | 2 | ✅ Completado |
| RNF11 | Autenticación y autorización (JWT/RBAC) | 1 | ✅ Completado |
| RNF12 | Protección de datos (bcrypt) | 1 | ✅ Completado |
| RNF13 | Pagos seguros (pasarela externa) | — | ❌ Eliminado |
| RNF14 | HTTPS y cifrado | 8 | ⏳ Pendiente |
| RNF15 | Prevención de ataques (SQLi, XSS, CSRF) | 1, 8 | ✅ Completado |
| RNF16 | Gestión de sesiones (30 min inactividad) | 1 | ✅ Completado |
| RNF17 | Disponibilidad ≥ 99% | 0 (infra) | ⏳ Pendiente |
| RNF18 | Recuperación ante fallos (< 5 min) | 0 (infra) | ⏳ Pendiente |
| RNF19 | Manejo de errores amigable | 8 | ✅ Completado |
| RNF20 | Persistencia de datos (transacciones) | 4, 5 | ✅ Completado |
| RNF21 | Mantenibilidad (código modular) | Todas | ✅ Completado |
| RNF22 | Fiabilidad de reservas (sin duplicados) | 4 | ✅ Completado |

---

## 9. Decisiones Técnicas

> Registro de decisiones importantes tomadas durante el desarrollo.

| Fecha | Decisión | Contexto | Impacto |
|-------|----------|----------|---------|
| — | Stack: Java 17 + Spring Boot 4.0.7 + PostgreSQL + Thymeleaf | Modelo guía + especificaciones | Define toda la base tecnológica |
| — | Frontend: Thymeleaf + Bootstrap (no React/Vue) | Facilidad de integración con Spring Boot | Limitaciones en experiencia mobile pero más rápido de implementar |
| — | Arquitectura N-capas con patrón Controller→Service→Repository | Patrón del modelo guía | Separación clara de responsabilidades |
| — | **JWT con cookie HttpOnly** | Resuelto en ERS (RNF11, Especificaciones 3.85 + RNF15). Protege contra XSS | Seguridad y UX |
| — | **Comisión configurable** | Resuelto en ERS (Regla 5, Especificaciones 7.182). Parámetro admin en `configuracion_sistema` | Modelo de negocio |
| — | **Política de cancelación** | Definida en revisión ENG (autoplan). 3 roles con reglas específicas | UX y fiabilidad |
| — | **Concurrencia en reservas** | UNIQUE CONSTRAINT + `@Lock(PESSIMISTIC_WRITE)` | Fiabilidad (RNF22) |
| — | **Paginación con Pageable** | Desde el inicio para cumplir RNF04/RNF05 | Performance |
| — | **Disponibilidad semanal recurrente** | Bloques de 1h configurable. Modo puntual post-MVP | Simplicidad MVP |
| — | Librerías JWT | Uso de `jjwt` 0.12.6 para tokens | Implementación estándar y moderna |
| — | Seed de datos | `CommandLineRunner` (`DbSeed`) para carga inicial | Facilidad de despliegue inicial |
| 14/06/2026 | **Cierre Híbrido de Seguridad** | Reemplazado `permitAll()` general por matriz: público, autenticado, admin. implementado en `SecurityConfig.java` | Seguridad por capas. `/admin/**` protegido con `hasRole('ADMIN')` |
| 14/06/2026 | **AuthenticationEntryPoint** | Agregado para redirigir al login en vez de devolver 403 en modo STATELESS con JWT | UX: Usuarios no autenticados son redirigidos al login |
| 14/06/2026 | **Dashboard Admin con KPIs** | Consultas agregadas en repositorios (`COUNT`, `SUM`) para métricas en tiempo real | Visibilidad operativa del negocio |
| 14/06/2026 | **CRUD Materias en Admin** | Modal Bootstrap con formularios POST para crear/editar/eliminar materias | Catálogo expandible sin redeploy |
| 21/06/2026 | **Eliminación del módulo de pagos** | Se eliminó Fase 5 completa: Pago.java, PagoService.java, PagoController.java, PagoRepository.java, templates liquidacion.html/admin-deudas.html, test PagoServiceTest.java. EstadoReserva simplificado (sin PAGADA), MetodoPago eliminado. Flujo: PENDIENTE → CONFIRMADA → FINALIZADA. | Proyecto simplificado sin gestión financiera |

---

## 10. Registro de Errores y Correcciones

> Bitácora de problemas encontrados y cómo se resolvieron.

| Fecha | Fase | Descripción del Error | Causa | Solución Aplicada | Estado |
|-------|------|----------------------|-------|-------------------|--------|
| 14/06/2026 | Fase 1 | `NullPointerException` potencial en `JwtAuthenticationFilter.doFilterInternal()` línea 41 | `validateToken(jwt, null)` pasaba `null` como `UserDetails`, y `validateToken` llamaba `userDetails.getUsername()` sin null-check | Se agregó método `validateToken(String token)` sobrecargado en `JwtTokenProvider` que solo valida firma y expiración sin requerir `UserDetails`. El filter ahora lo usa para el pre-check. | ✅ Corregido |
| 14/06/2026 | Fase 2 | Whitelabel 500 al cargar `/tutor/disponibilidad` | `#numbers.sequence(0, 6)` no existe en Thymeleaf; `diaIter.index` intenta acceder a `.index` en Integer | Iterar sobre lista `dias` con `diaStat.index` | ✅ Corregido |
| 14/06/2026 | Fase 2 | Whitelabel 500 al hacer POST a `/tutor/disponibilidad` (agregar bloque) | Stream + lambda en SpEL (`bloques.stream().filter(b -> ...).toList()`) no parseable por Thymeleaf | Agrupar bloques por día en el Controller (`bloquesPorDia: Map<Integer, List<Disponibilidad>>`) | ✅ Corregido |
| 14/06/2026 | Fase 2 | Whitelabel 500 con cookie JWT de usuario eliminado | `UsernameNotFoundException` desde `CustomUserDetailsService.loadUserByUsername()` propagado fuera del filter | Try-catch en `JwtAuthenticationFilter.doFilterInternal()` | ✅ Corregido |
| 14/06/2026 | Fase 2 | Mensaje genérico "Ha ocurrido un error" al solapar bloques | Error message del controller no mapeado en `th:switch` de `layout.html` | Mostrar mensaje real con `param.error[0]` en caso default | ✅ Corregido |
| 14/06/2026 | Fase 3 | Día del horario desplazado en perfil público (Lunes mostraba Martes) | `dias[diaEntry.key]` con `diaEntry.key` en 1-7 pero `dias` 0-indexado | Cambiar a `dias[diaEntry.key - 1]` en `perfil-publico.html:79` | ✅ Corregido |
| 14/06/2026 | Fase 7 | 403 Forbidden al acceder a páginas protegidas sin autenticación | `SessionCreationPolicy.STATELESS` sin `AuthenticationEntryPoint` para redirigir al login | Agregado `exceptionHandling().authenticationEntryPoint()` en `SecurityConfig.java` que redirige a `/auth/login` | ✅ Corregido |
| 14/06/2026 | Fase 7 | Puerto 8080 ocupado al reiniciar la aplicación | El proceso Java anterior no se detuvo completamente antes del nuevo inicio | Se agregó script de limpieza de procesos antes del reinicio | ✅ Corregido (operativo) |

---

## 11. Pendientes y Consideraciones Futuras

> Ideas, mejoras o funcionalidades que no entran en el MVP pero vale la pena documentar.

| Tarea | Fase Estimada | Notas |
|-------|---------------|-------|
| Cobertura para otras facultades/universidades | Post-MVP | Requiere multi-tenancy o multi-dominio |
| Videollamadas integradas | Post-MVP | Integración con WebRTC o Jitsi |
| IA para recomendaciones complejas | Post-MVP | Machine learning |
| Gestión administrativa institucional completa | Post-MVP | Maven, profesores, registro académico |
| Almacenamiento directo de datos bancarios | ❌ Nunca | Prohibido por seguridad (RNF13) |

---

## 📊 Métricas de Avance General

| Métrica | Valor |
|---------|-------|
| Fases Completadas | **10 / 10** (Fase 5 Pagos eliminada, reemplazada por Fases 8-10 UI) |
| RF Implementadas | **10 / 10** (RF10-RF14 eliminados) |
| RNF Cumplidos | **13 / 21** (RNF13 eliminado) |
| Tests Pasando | **26 / 26** ✅ Todos pasando |
| Bugs Abiertos | 0 |
| Última Actualización | 22/06/2026 — Fases 8-10 UI completadas |
| Identidad Visual | ✅ Sistema de iconos SVG por materia + paleta teal turquesa |
| Animaciones | ✅ Hero con física 2D (colisiones + rebotes) + stagger-fade + card-fade-in |
| Navbar | ✅ Glass efecto blur + patrón circuit board |
| Background | ✅ Circuit board pattern + gradientes + noise textura |
| Notificaciones | ✅ Sistema funcional con campana, dropdown y badge contador |
| Seguridad | ✅ "Cierre Híbrido" — ADMIN protegido, redirección al login en 403 |
| Performance | ✅ JOIN FETCH en todos los repositorios de listado (N+1 eliminado) |
| Accesibilidad | ✅ aria-label en tablas/icon-buttons, keyboard en rating, for/id en filtros |
| Localhost:8080 | ✅ App funcionando — UI rediseñada, Dashboard Admin, Gestión Materias |
| Base de datos | ✅ PostgreSQL con seed automático en cada arranque |

---

> **Instrucción de actualización:** Después de cada tarea completada, actualizar: estado (⏳ → 🔄 → ✅), fecha, y registrar decisiones/errores en sus secciones correspondientes.

---

## GSTACK REVIEW REPORT

### Resumen de la Revisión (autoplan: CEO + ENG)

| Dimensión | Hallazgos | Estado |
|-----------|-----------|--------|
| **Alcance (CEO)** | Cobertura completa de 15 RF y 22 RNF. Modo SELECTIVE EXPANSION aplicado. | ✅ Aprobado |
| **Gaps críticos detectados** | Política de cancelación (definida), transición a FINALIZADA (definida), confirmación de pago efectivo (definida) | ✅ Resuelto |
| **Decisiones resueltas** | JWT + cookie HttpOnly, comisión configurable, concurrencia pesimista, paginación desde inicio, bloques de 1h semanal | ✅ Resuelto |
| **Gaps post-MVP** | Verificación de email, notificaciones email, filtro "disponible ahora", reportes de ingresos tutor | 📋 Documentado |
| **Errores corregidos (esta revisión)** | 2 typos, 4 decisiones cerradas, 3 gaps rellenados, 3 tareas agregadas | ✅ 17 cambios aplicados |
| **Riesgo residual** | Integración real de pasarela de pagos pendiente de definir; testing concreto pendiente de implementar | 🟡 Bajo |

**Veredicto:** Plan listo para iniciar implementación. Fase 0 puede comenzar.

---

## 📜 Registro de Cambios por Sesión

> Bitácora de modificaciones al plan. Cada sesión de trabajo agrega una fila.

| Fecha | Sesión | Cambios Realizados |
|-------|--------|-------------------|
| 13/06/2026 | Inicial | Creación del plan maestro. 9 fases definidas. |
| 13/06/2026 | Revisión sonnet 4.6 | Corrección de typos (Est处→Estado, aad2→2). Cierre de decisiones JWT y comisión. Definición de política de cancelación. Resolución de granularidad, paginación, concurrencia. Agregadas tareas: PasswordEncoder, moderación reseñas, gestión materias, accesibilidad desde F1, testing con criterios. Agregado GSTACK REVIEW REPORT. |
| 13/06/2026 | Contexto de sesión | Agregada sección "🧠 INSTRUCCIONES PARA LA IA" al inicio del documento. |
| 13/06/2026 | Implementación Fase 0 | Estructura de paquetes, dependencias (Security, Validation, JJWT), entidad ConfiguracionSistema, bean PasswordEncoder y seed de datos básico. |
| 14/06/2026 | Implementación Fase 1 (Batches 1-3) | Entidades Usuario/PerfilTutor, validación @uce.edu.ec, JWT + Cookie HttpOnly, SecurityConfig stateless, UsuarioService (registro/login/logout), AuthController REST, GlobalExceptionHandler, corrección bugs (FK cascade, HTTP codes). |
| 14/06/2026 | Revisión y actualización del plan | App verificada en localhost:8080 — registro y login funcionales, usuarios persistentes en BD. Corregido bug NPE en `JwtAuthenticationFilter` (validateToken con null). Actualizadas métricas y plan a v2.2.0. |
| 14/06/2026 | Implementación Fase 2 | Entidades `TutorMateria` y `Disponibilidad` (con estado LIBRE/RESERVADO). `PerfilTutorService` (CRUD perfil, materias, tarifas, semestre). `DisponibilidadService` (CRUD bloques con validación de solapamiento). `TutorProfileController` (SSR Thymeleaf — perfil + disponibilidad). `AdminController` (verificación de tutores). Vistas: `perfil-tutor.html`, `configurar-disponibilidad.html`, `admin-tutores.html`. Navbar actualizado con enlaces a perfil tutor y disponibilidad. Flash messages agregados al layout. |
| 14/06/2026 | Corrección errores Fase 2 | 4 errores corregidos: (1) `#numbers.sequence()` + `diaIter.index` en template → iterar con `diaStat.index`; (2) Stream/lambda en SpEL no parseable → agrupar en Controller con `bloquesPorDia`; (3) `UsernameNotFoundException` sin catch en filter → try-catch; (4) Mensaje genérico en overlap → mostrar error real con `param.error[0]`. BD truncada y reseed. Plan actualizado a v2.3.0. Siguiente: Fase 3. |
| 14/06/2026 | Implementación Fase 3 | Repository `buscarTutores()` con JPQL + 5 filtros, `obtenerRecomendados()` top 6. Servicio `PerfilTutorService` con métodos de búsqueda y perfil público. `HomeController` dinámico con materias por categoría + recomendados. Nuevo `SearchController` (`/buscar`, `/tutor/{id}`). Vistas: `home.html` (catálogo + recomendaciones), `resultados.html` (filtros + lista), `perfil-publico.html` (datos, materias, disponibilidad). Compilación exitosa. Plan actualizado a v2.4.0. Siguiente: Fase 4. |
| 14/06/2026 | Corrección Fase 3 | Bug off-by-one en `perfil-publico.html:79`: `dias[diaEntry.key]` → `dias[diaEntry.key - 1]`. Se agregó ruta `/tutor/mi-perfil` con template `mi-perfil.html` para que cualquier usuario (incluso sin perfil tutor) pueda ver sus datos. Menú actualizado: "Ver Mi Perfil", "Editar Perfil", "Disponibilidad". Plan actualizado a v2.5.0. |
| 14/06/2026 | Implementación Fase 4 | Entidades `Reserva` (con máquina de estados) y `Notificacion`. Repositorios, servicios (ReservaService con lógica de cancelación por rol, NotificacionService). Controller `/reservas` con crear/cancelar. Template `mis-tutorias.html` con tabs solicitante/tutor y botón cancelar. Formulario de solicitud integrado en `perfil-publico.html` con selector materia + bloque. Enlace "Mis Tutorías" en navbar. Notificaciones creadas al solicitar y cancelar. Plan actualizado a v2.6.0. |
| 14/06/2026 | Fase 4 - Completado (aceptar/confirmar) | Se agregó funcionalidad faltante de **confirmar reserva**: nuevo método `confirmar()` en `ReservaService`, endpoint `POST /reservas/{id}/confirmar` en `ReservaController`, botón "Aceptar" en `mis-tutorias.html` (pestaña Como Tutor), notificación `RESERVA_CONFIRMADA` al solicitante, y mensaje flash `reserva_confirmada` en `layout.html`. Fase 4 completamente funcional: PENDIENTE → CONFIRMADA (tutor acepta) → ... Plan actualizado a v2.7.0. |
| 14/06/2026 | Implementación Fase 5 (completa) | Entidad `Pago` con enums y campos completos (`comision_calculada`, `referencia_pasarela`). `Reserva` con `metodo_pago`. `PagoService` con lógica de comisión/límite desde `configuracion_sistema`, liquidación de deuda. `PagoController` con endpoints: `POST /pagos/digital/{id}` (mock), `POST /pagos/efectivo/{id}` (finalizar + comisión), `POST /pagos/liquidar`. Templates: `liquidacion.html`. Flujo completo: CONFIRMADA → PAGADA (digital) o CONFIRMADA → FINALIZADA (efectivo + comisión). Navbar actualizado con enlace Liquidar Deuda. Plan actualizado a v2.8.0. |
| 14/06/2026 | Implementación Fase 6 (completa) | Entidad `Resena` (1:1 con Reserva, calificación 1-5). Servicio con validación (solo FINALIZADA, una por reserva, solo solicitante). Cálculo automático de promedio vía `AVG()` + actualización en `PerfilTutor`. Controlador con crear y eliminar (admin). Template `formulario-resena.html` con estrellas interactivas. Botón Calificar en `mis-tutorias.html` (solo FINALIZADA sin reseña). Reseñas visibles en `perfil-publico.html`. Moderación admin via `POST /resenas/eliminar/{id}`. PLAN.md v2.9.0. |
| 14/06/2026 | Implementación Fase 7 (Panel Admin) | **Cierre Híbrido de Seguridad:** `SecurityConfig.java` reestructurado — reemplazado `permitAll()` general por matriz de acceso (público/autenticado/admin). Agregado `AuthenticationEntryPoint` para redirigir al login (resuelve conflicto de 403 en modo STATELESS). **Dashboard de Control:** Nuevo endpoint `GET /admin` con KPIs en `admin-dashboard.html` (usuarios, tutores, deuda, reservas hoy). Nuevas consultas agregadas en `PerfilTutorRepository` y `ReservaRepository`. **Configuración del Sistema:** Formulario para comisión % y límite de crédito en `admin-configuracion.html`. **Reporte de Deudas:** Listado de tutores morosos en `admin-deudas.html`. **CRUD Materias:** Modal Bootstrap con crear/editar/eliminar en `admin-materias.html` — catálogo expandible sin redeploy. **Moderación Reseñas:** Listado completo con eliminación en `admin-resenas.html`. Navbar actualizado con enlace a `/admin`. Mensajes flash agregados en `layout.html`. Métricas y tracking de RF/RNF actualizados. PLAN.md v3.0.0. |
| 14/06/2026 | Implementación Fase 8 (Optimización, Testing, Pulido) | **Layout Maestro:** 5 fragmentos Thymeleaf, 15 templates convertidos. **Validaciones:** 8 DTOs con `@Valid` + `BindingResult`. **GlobalExceptionHandler:** Logging, AccessDeniedException, DataIntegrityViolationException. **Perfiles Spring:** `application-dev.properties` + `application-prod.properties`. **Tests:** 33 tests. **Accesibilidad:** aria-label, keyboard en rating. **Performance:** JOIN FETCH (N+1 eliminado). |
| 22/06/2026 | Fase 8 (real) — Sistema de Iconos SVG por Materia | Campo `icono` (TEXT) + `descripcion` en entidad `Materia`. 7 constantes SVG en `DbSeed.java`. Seed de 12 materias con iconos. CRUD admin con campo icono. Renderizado en todos los templates con `th:utext` + fallback Bootstrap Icons. Colores por categoría vía `[data-categoria]`. Commits: `397fee2`, `a931839`, `2168388`. |
| 22/06/2026 | Fase 9 — Rediseño Estético Integral | Paleta teal turquesa (`#0d9488`). Hero con gradiente + 4 iconos flotantes CSS. Animaciones stagger-fade y card-fade-in. Glass navbar con backdrop-filter. Fondo circuit board + gradientes + noise. Escalado responsive de iconos SVG. Admin compacto sin animaciones. Cache-buster CSS. Seguridad por roles refinada. Commits: `3af18d3`, `a931839`, `2168388`, `d6e9dee`, `03ae84f`, `84229d5`, `a7ce472`. |
| 22/06/2026 | Fase 9 — Notificaciones + Navbar glass | Sistema de notificaciones funcional con campana y dropdown (`scripts.html`). Polling cada 30s, badge contador, marcar leídas. Navbar glass tintado con patrón circuit. Commit: `a7ce472`. |
| 22/06/2026 | Fase 10 — Hero con Física 2D | Motor de físicas JavaScript (`hero-physics.js`). 10 iconos con posición/velocidad/masa/rotación aleatorias. Colisiones elásticas entre iconos + rebote en bordes. Reemplazo de animación CSS `@keyframes float-icon`. Oculto en mobile. |

