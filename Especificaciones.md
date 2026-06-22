# TutoMatch — Documento Maestro de Especificación (Consolidado)
### Plataforma de Tutorías Académicas Estudiantiles — Universidad Central del Ecuador
**Stack objetivo:** Java (Spring Boot) + PostgreSQL | Arquitectura N-capas | Diseño Mobile-First

---

## 0. Propósito de este documento

Este documento consolida y reconcilia **tres fuentes**:

1. **Levantamiento inicial (Fase 1-5, "Gestión de Proyecto TutoMatch")**: hipótesis, encuesta, RF01-RF05/RNF01-RNF05, prototipos de pantallas.
2. **Resultados originales de la encuesta de Google Forms (24 respuestas)**: datos cuantitativos y cualitativos que validan o ajustan los requerimientos.
3. **Informe de Requerimientos para "Marcos de Desarrollo" (ERS ampliado)**: alcance ampliado con perfiles duales, módulo financiero híbrido (pagos digitales + efectivo con comisión/deuda), casos de uso y diagrama de secuencia.

El objetivo es entregar **una única especificación coherente** que sirva como *prompt maestro* para que un asistente de IA (o un equipo de desarrollo) entienda completamente el sistema TutoMatch y pueda generar código en **Java + PostgreSQL** de forma consistente, sin contradicciones entre documentos.

> **Instrucción para la IA de desarrollo:** A partir de este documento, actúa como arquitecto/desarrollador backend en Java (Spring Boot) con base de datos PostgreSQL. Toda nueva funcionalidad, entidad, endpoint o tabla que generes debe respetar los módulos, prioridades y reglas de negocio aquí descritos. Si una solicitud del usuario entra en conflicto con este documento, señala el conflicto antes de implementar.

---

## 1. Resumen Ejecutivo del Proyecto

**Nombre:** TutoMatch
**Tipo:** Plataforma web (responsive, mobile-first) de economía colaborativa académica.
**Problema:** Los estudiantes de la Facultad de Ingeniería y Ciencias Aplicadas (FICA - UCE) pierden tiempo y desconfían al buscar ayuda académica por canales informales (grupos de WhatsApp, amigos, internet).
**Solución:** Centralizar la oferta y demanda de tutorías entre estudiantes (perfiles duales: solicitante / tutor), con búsqueda filtrada, agendamiento, sistema de reputación y un módulo de pagos híbrido (digital y efectivo con gestión de comisiones).

### 1.1 Datos clave de la encuesta (24 respuestas — fuente oficial Google Forms)

| Indicador | Resultado |
|---|---|
| Se ha sentido "atascado" en una materia y necesitó ayuda externa (sí, frecuentemente / alguna vez) | 54,2% frecuentemente + 33,3% alguna vez = **87,5%** |
| Dificultad/frustración para encontrar un compañero capacitado (escala 1-5) | 50% nivel 4 + 37,5% nivel 5 + 12,5% nivel 3 → **87,5% reporta dificultad alta (4-5)** |
| Recurso al que acuden primero | 62,5% grupos de WhatsApp / 29,2% amigo cercano / 8,3% tutoriales en internet |
| ¿Usarían una plataforma oficial de tutorías? | 75% Sí + 25% Probablemente = **100% confirma la hipótesis** |
| Perfil de participación | 54,2% solo como solicitante / 37,5% ambos perfiles (dual) |
| Filtros indispensables (selección múltiple) | Horarios disponibles 70,8% / Calificación 66,7% / Precio por hora 50% / Semestre del tutor 25% |
| Método de pago preferido | Efectivo el día de clase 70,8% / Mezcla de ambos 12,5% / Pago digital integrado 16,7% |
| Dispositivo preferido en campus | Móvil 83,3% / PC o laptop 16,7% |

### 1.2 Conclusión de consenso (resuelve discrepancias entre documentos)

- El levantamiento inicial (18 respuestas, documento "Gestión de Proyecto") y la encuesta oficial (24 respuestas) **coinciden en la tendencia**: alta frustración (~88%), preferencia móvil abrumadora (>80%) y aceptación total de la idea (100%).
- Sobre **pagos**: el levantamiento inicial proponía un MVP **sin pasarela de pago** (solo acordar el método). Sin embargo, la encuesta muestra que aunque el 70,8% prefiere efectivo, existe un 16,7%-12,5% que sí desea pago digital, y el Informe ERS ampliado (Marcos de Desarrollo) define un **módulo financiero híbrido completo** (digital + efectivo con comisión/deuda) como requerimiento del proyecto.
- **Decisión de consenso:** Se construirá el sistema completo según el **ERS ampliado** (módulo híbrido con RF08-RF12), pero el **pago digital se implementa mediante integración con pasarela externa en modo sandbox/pruebas**, priorizando primero el flujo de **efectivo + comisión/deuda**, que es el escenario mayoritario real (70,8%). Esto permite cumplir el alcance académico ampliado sin bloquear el MVP por dependencias externas de pago.

---

## 2. Alcance del Proyecto

### 2.1 Incluido en el MVP
- Registro/login con **correo institucional** (@uce.edu.ec o dominio equivalente).
- Perfiles duales (solicitante / tutor) para un mismo usuario.
- Gestión de perfil de tutor: materias, descripción, tarifa, semestre verificado.
- Gestión de disponibilidad horaria (calendario de bloques).
- Búsqueda y filtros: materia, horario, calificación, precio, semestre.
- Catálogo estructurado de materias (categorías / buscador).
- Recomendaciones básicas de tutores en pantalla principal.
- Agendamiento de tutorías con verificación de disponibilidad.
- Sistema de calificaciones y reseñas (estrellas + comentario) tras tutoría finalizada.
- Notificaciones internas (panel de avisos: reserva confirmada, pago recibido, etc.).
- Panel administrativo básico (gestión de usuarios, reportes, control de deudas).

### 2.2 Fuera del alcance (versiones futuras)
- Cobertura para otras facultades/universidades.
- Videollamadas integradas.
- IA para recomendaciones complejas.
- Gestión administrativa institucional completa.
- Módulo de pagos (eliminado del alcance).

---

## 3. Stack Tecnológico y Arquitectura

| Capa | Tecnología sugerida |
|---|---|
| Backend | Java 17+, Spring Boot (Spring Web, Spring Security, Spring Data JPA, Validation) |
| Base de datos | PostgreSQL (relacional) |
| Autenticación | JWT (tokens), contraseñas con bcrypt |
| Frontend | Aplicación web responsiva (mobile-first); puede consumir el backend vía API REST (React, Vue o Thymeleaf según preferencia del equipo) |
| Comunicación | API REST con JSON, HTTPS obligatorio |
| Pasarela de pagos | No aplica (eliminado del alcance) |
| Notificaciones | Tabla de notificaciones internas (no se requiere servicio externo en MVP) |
| Arquitectura | N-capas: Presentación (API/Controllers) → Lógica de Negocio (Services) → Persistencia (Repositories/JPA) → PostgreSQL |

### 3.1 Principios de diseño backend
- Separación estricta por capas (Controller → Service → Repository → Entity).
- Control de acceso basado en roles (RBAC): `SOLICITANTE`, `TUTOR`, `ADMIN` (un usuario puede tener rol dual solicitante+tutor simultáneamente).
- Los endpoints de búsqueda y agendamiento deben optimizarse con índices en PostgreSQL (materia, horario, estado de reserva).

---

## 4. Actores del Sistema

| Actor | Descripción |
|---|---|
| **Estudiante Solicitante** | Busca tutores, revisa perfiles, agenda tutorías, paga (digital o efectivo) y califica el servicio. |
| **Estudiante Tutor** | Configura perfil académico (materias, tarifa, semestre), define disponibilidad, recibe reservas, gestiona su deuda por comisiones y recibe calificaciones. |
| **Perfil Dual** | Un mismo usuario puede operar como solicitante y como tutor según la materia/contexto (37,5% de encuestados lo prefiere así). |
| **Administrador** | Supervisa usuarios, valida/verifica tutores, gestiona reportes, controla límites de crédito y deudas. |

---

## 5. Requerimientos Funcionales (RF) — Consolidados

> Numeración unificada para el desarrollo. Se indica la fuente (Levantamiento inicial = "L", ERS ampliado = "E") y la validación con encuesta.

| ID | Requerimiento | Descripción | Prioridad | Validación (encuesta) |
|---|---|---|---|---|
| RF01 | Registro e inicio de sesión | El sistema permite crear cuenta y autenticarse usando **correo institucional**. Define rol(es): solicitante, tutor o ambos. | Alta (Crítico) | Restricción de registro institucional (L) |
| RF02 | Configuración de perfil de tutor | El tutor registra materias que domina, descripción de experiencia, tarifa por hora y **semestre** (validado/verificado). | Alta (Crítico) | 25% pide filtro por semestre; feedback cualitativo pide "semestres superiores o graduados" |
| RF03 | Gestión de disponibilidad | El tutor define y actualiza bloques horarios disponibles en un calendario interactivo. | Alta (Crítico) | 70,8% considera "horarios disponibles" filtro indispensable |
| RF04 | Búsqueda y filtros académicos | El solicitante busca tutores filtrando por materia, horario disponible, calificación y precio por hora. | Alta (Crítico) | Horario 70,8%, Calificación 66,7%, Precio 50% |
| RF05 | Catálogo estructurado de materias | Pantalla de inicio con barra de búsqueda principal y accesos rápidos por materia/categoría. | Alta (Crítico) | Feedback cualitativo: "dividido por materias o tenga un buscador" |
| RF06 | Recomendaciones de tutores | La pantalla principal muestra tutores recomendados según afinidad de materia y reputación. | Media (Importante) | Complementa RF04 |
| RF07 | Visualizar perfil del tutor | Mostrar semestre verificado, descripción, calificación promedio, reseñas y tarifa. | Media (Importante) | Feedback: "validación de conocimiento", "veracidad del contenido, respaldo, reseñas, horarios" |
| RF08 | Reserva/Agendamiento de tutoría | El solicitante selecciona un bloque horario disponible y genera una solicitud de reserva formal; el sistema valida disponibilidad en tiempo real. | Alta (Crítico) | Flujo ideal descrito por el 100% de respuestas abiertas |
| RF09 | Sincronización de estados y notificaciones | El sistema actualiza automáticamente el estado de la sesión (Pendiente, Confirmada, Pagada, Finalizada) y envía notificaciones internas a ambos usuarios. | Alta (Crítico) | — |
| RF15 | Sistema de calificación y reseñas | El solicitante otorga estrellas y comentario al tutor una vez que la tutoría está marcada como finalizada. | Media (Importante) | 66,7% considera calificación filtro indispensable; feedback pide "comentarios de otros estudiantes calificando a los tutores" |

---

## 6. Requerimientos No Funcionales (RNF) — Consolidados

### 6.1 Usabilidad y diseño
| ID | Requerimiento | Descripción |
|---|---|---|
| RNF01 | Mobile-first estricto | Toda la interfaz se diseña primero para móvil; el diseño de escritorio es secundario. (83,3% usa móvil en campus) |
| RNF02 | Navegación intuitiva, curva de aprendizaje ~0 | Flujo simple sin tutoriales: ingreso → registro → materia → horario → tutor → agendar. Tiempo de aprendizaje objetivo < 10 minutos. |
| RNF03 | Accesibilidad | Cumplir WCAG 2.1 nivel AA (contraste, navegación por teclado). |

### 6.2 Desempeño
| ID | Requerimiento | Descripción |
|---|---|---|
| RNF04 | Tiempo de carga de páginas | ≤ 2 segundos en conexiones 4G o superiores. |
| RNF05 | Tiempo de respuesta de búsqueda | Resultados de búsqueda/filtrado de tutores en < 1,5 segundos. |
| RNF06 | Usuarios concurrentes | Soportar al menos 200 usuarios concurrentes en el MVP sin degradación significativa. |
| RNF07 | Procesamiento de reservas | Verificación de disponibilidad y creación de reserva en < 3 segundos. |
| RNF08 | Escalabilidad | Arquitectura que permita escalar horizontalmente al expandirse a otras facultades. |

### 6.3 Seguridad
| ID | Requerimiento | Descripción |
|---|---|---|
| RNF09 | Registro institucional | Solo se permiten cuentas con correo institucional de la UCE. |
| RNF10 | Verificación de tutores | Filtro obligatorio: nadie ofrece tutorías sin validar previamente su semestre/conocimientos. |
| RNF11 | Autenticación y autorización | JWT o sesiones seguras; control de acceso basado en roles (solicitante, tutor, administrador). |
| RNF12 | Protección de datos personales | Cumplimiento de la Ley Orgánica de Protección de Datos del Ecuador; contraseñas encriptadas (bcrypt). |
| RNF13 | Pagos seguros | No aplica (eliminado del alcance). |
| RNF14 | HTTPS y cifrado | Toda la comunicación cliente-servidor sobre HTTPS. |
| RNF15 | Prevención de ataques comunes | Protección contra SQL Injection, XSS, CSRF y rate limiting en login/endpoints críticos. |
| RNF16 | Gestión de sesiones | Expiración de sesión tras 30 minutos de inactividad. |

### 6.4 Estabilidad y mantenibilidad
| ID | Requerimiento | Descripción |
|---|---|---|
| RNF17 | Disponibilidad | ≥ 99% durante horario académico (7:00 - 22:00). |
| RNF18 | Recuperación ante fallos | Recuperación en < 5 minutos mediante backups automáticos. |
| RNF19 | Manejo de errores | Mensajes claros y amigables al usuario, sin exponer detalles técnicos. |
| RNF20 | Persistencia de datos | Las reservas y perfiles no deben perderse ante caídas; uso de transacciones en PostgreSQL. |
| RNF21 | Mantenibilidad | Código modular (capas separadas), documentado, siguiendo convenciones Java/Spring. |
| RNF22 | Fiabilidad de reservas | Las reservas no deben duplicarse ni perderse (validación de unicidad en bloque horario + tutor). |

---

## 7. Reglas de Negocio Clave

1. **Registro institucional obligatorio**: el correo debe pertenecer al dominio institucional de la UCE; se valida en el registro (RF01).
2. **Perfiles duales**: un mismo usuario (`usuario_id`) puede tener un registro asociado en `perfil_tutor`; los roles `SOLICITANTE` y `TUTOR` no son mutuamente excluyentes.
3. **Verificación de tutor**: un tutor no aparece en resultados de búsqueda hasta que su perfil esté marcado como `verificado = true` (semestre validado).
4. **Reserva**: un bloque horario solo puede tener una reserva activa (estado distinto de `CANCELADA`); se debe validar a nivel de base de datos (constraint único) y de servicio.
5. **Calificación**: solo se permite calificar (RF15) si `estado_reserva = FINALIZADA`.
6. **Estados de reserva** (máquina de estados): `PENDIENTE` → `CONFIRMADA` → `FINALIZADA` (o `CANCELADA` desde cualquier estado previo a `FINALIZADA`).

---

## 8. Modelo de Datos Sugerido (PostgreSQL)

> Entidades principales y campos clave. Los nombres de tabla están en `snake_case` para PostgreSQL; las clases Java equivalentes irían en `PascalCase` (ej. `Usuario`, `PerfilTutor`).

### `usuarios`
- `id` (PK, UUID/serial)
- `correo_institucional` (unique, not null)
- `password_hash`
- `nombre_completo`
- `rol_solicitante` (boolean)
- `rol_tutor` (boolean)
- `rol_admin` (boolean)
- `fecha_creacion`

### `perfiles_tutor`
- `id` (PK)
- `usuario_id` (FK → usuarios)
- `semestre`
- `descripcion`
- `verificado` (boolean)
- `visible` (boolean) — controlado por límite de crédito
- `deuda_pendiente` (numeric)
- `calificacion_promedio` (numeric)

### `materias`
- `id` (PK)
- `nombre`
- `categoria` / `semestre_referencial`

### `tutor_materias`
- `id` (PK)
- `perfil_tutor_id` (FK)
- `materia_id` (FK)
- `tarifa_hora` (numeric)

### `disponibilidad`
- `id` (PK)
- `perfil_tutor_id` (FK)
- `dia_semana` / `fecha`
- `hora_inicio`, `hora_fin`
- `estado` (`LIBRE`, `RESERVADO`)

### `reservas`
- `id` (PK)
- `solicitante_id` (FK → usuarios)
- `disponibilidad_id` (FK)
- `materia_id` (FK)
- `estado` (`PENDIENTE`, `CONFIRMADA`, `FINALIZADA`, `CANCELADA`)
- `fecha_creacion`

### `resenas`
- `id` (PK)
- `reserva_id` (FK)
- `calificacion` (1-5)
- `comentario`
- `fecha`

### `notificaciones`
- `id` (PK)
- `usuario_id` (FK)
- `tipo` (`RESERVA_CONFIRMADA`, `RESERVA_FINALIZADA`, etc.)
- `mensaje`
- `leida` (boolean)
- `fecha`

### `configuracion_sistema`
- `clave` (PK) — ej. `porcentaje_comision`, `limite_credito_tutor`
- `valor`

---

## 9. Casos de Uso Consolidados

| Código | Caso de Uso | Actor principal | RF relacionado |
|---|---|---|---|
| CU01 | Registrar cuenta e iniciar sesión | Solicitante / Tutor | RF01 |
| CU02 | Configurar perfil como tutor | Tutor | RF02 |
| CU03 | Gestionar disponibilidad horaria | Tutor | RF03 |
| CU04 | Buscar tutores con filtros | Solicitante | RF04, RF05 |
| CU05 | Ver recomendaciones de tutores | Solicitante | RF06 |
| CU06 | Ver perfil y reseñas de tutor | Solicitante | RF07 |
| CU07 | Agendar/reservar tutoría | Solicitante | RF08, RF09 |
| CU08 | Calificar tutoría | Solicitante | RF15 |
| CU09 | Administrar plataforma (usuarios, reportes) | Administrador | — |

---

## 10. Flujo Principal (Agendamiento + Pago Híbrido)

1. El **solicitante** selecciona materia → tutor → bloque horario.
2. El **backend** valida disponibilidad del bloque.
3. Si está disponible → se crea la reserva en estado `PENDIENTE`.
4. El **tutor** acepta la reserva → pasa a `CONFIRMADA`.
5. Tras la sesión, el **tutor** finaliza la reserva → pasa a `FINALIZADA`.
6. Se envían notificaciones internas a ambos usuarios en cada cambio de estado (RF09).
7. El solicitante puede calificar la tutoría una vez `FINALIZADA` (RF15).

---

## 11. Pantallas Mobile-First (basadas en prototipos validados)

| Pantalla | Contenido principal | RF que materializa |
|---|---|---|
| **Home / Búsqueda** | Saludo, barra de búsqueda principal, accesos rápidos por materia (Programación, Cálculo, Bases de Datos, Física, Química, Estadística, Álgebra, Contabilidad, etc.) | RF05, RF06 |
| **Resultados y Filtros** | Filtros superiores (Horario, Precio, Calificación, Semestre); listado de tarjetas de tutores con nombre, semestre, calificación y tarifa/hora | RF04, RF07 |
| **Perfil del Tutor + Agendamiento** | Foto/nombre, semestre verificado, descripción, reseñas de estudiantes, total por hora, botón "Agendar Tutoría" | RF07, RF08 |
| **Mis Tutorías** | Listado de reservas con estado (Pendiente, Confirmada, Pagada, Finalizada) | RF09 |
| **Notificaciones** | Panel de avisos internos | RF09 |
| **Perfil propio / Configuración de tutor** | Materias dominadas, tarifa, descripción, calendario de disponibilidad | RF02, RF03 |
| **Panel administrador** | Gestión de usuarios, verificación de tutores, gestión de materias | CU09 |

---

## 12. Glosario

| Término | Definición |
|---|---|
| Estudiante Solicitante | Usuario que busca y reserva tutorías. |
| Estudiante Tutor | Usuario que ofrece tutorías en materias específicas. |
| Perfil Dual | Capacidad de un mismo usuario de actuar como solicitante o tutor. |
| MVP | Versión inicial del sistema con funcionalidades esenciales. |
| Reputación | Puntuación promedio (estrellas) y comentarios recibidos por un tutor. |
| Bloque Horario | Intervalo de tiempo (ej. 1 hora) en que un tutor está disponible. |

---

## 13. Notas finales para el desarrollo con IA

- Priorizar la implementación en este orden: **RF01 → RF02/RF03 → RF04/RF05 → RF08/RF09 → RF15 → RF06/RF07**.
- Cada entidad de la sección 8 debe mapearse a una clase Java anotada con JPA (`@Entity`, `@Table`, relaciones `@ManyToOne`/`@OneToMany`) y su correspondiente `Repository`, `Service` y `Controller` REST.
- Toda fecha/hora debe almacenarse en UTC y convertirse en el frontend según la zona horaria de Ecuador (UTC-5).
