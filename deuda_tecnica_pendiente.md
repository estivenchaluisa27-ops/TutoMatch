# Deuda Técnica Pendiente - TutoMatch

**Última actualización:** 6 de julio de 2026
**Proyecto:** TutoMatch - Sistema de Gestión de Tutorías

---

## 1. PerfilTutorService con 1 sola responsabilidad (Prioridad: BAJA)

### Descripción

`PerfilTutorService` ahora solo tiene el método `obtenerOCrearPerfilTutor`. Podría integrarse en `PerfilTutorGestionService`.

### Análisis

Sin embargo, `PerfilTutorService` tiene una dependencia con `AuthenticationHelper` para obtener el usuario del contexto de seguridad, lo cual `PerfilTutorGestionService` no tiene. Fusionarlos requeriría agregar esa dependencia.

### Recomendación

Mantener separados por ahora. Si en el futuro `PerfilTutorGestionService` también necesita acceso al usuario autenticado, considerar fusión.

---

## 2. GlobalExceptionHandler sin soporte para redirects (Prioridad: BAJA)

### Descripción

`GlobalExceptionHandler` retorna `ResponseEntity<Map<String, String>>` para APIs REST. Los controllers MVC (`ReservaController`, `ResenaController`) delegaban excepciones a `MvcExceptionHandler`.

### Estado

✅ **CORREGIDO** — Se creó `MvcExceptionHandler` y se eliminaron los `try-catch` duplicados.

---

## Resumen de Métricas

| Métrica | Valor |
|---------|-------|
| Problemas de deuda técnica restantes | 1 |
| Prioridad ALTA | 0 |
| Prioridad MEDIA | 0 |
| Prioridad BAJA | 1 |
| Estimación de corrección | 1-2 horas |

---

## Historial de Correcciones

| Fecha | Problema | Estado |
|-------|----------|--------|
| 06 jul 2026 | Método `ReservaService.finalizar()` deprecado | ✅ ELIMINADO |
| 06 jul 2026 | Dependencia circular Usuario ↔ PerfilTutor | ✅ CORREGIDO |
| 06 jul 2026 | `catch(Exception)` duplicados en ReservaController y ResenaController | ✅ CORREGIDO |
| 06 jul 2026 | PerfilTutor.usuario ya tenía `FetchType.LAZY` | ✅ VERIFICADO |
| 28 jun 2026 | `catch(Exception)` genéricos en `ResenaController` | ✅ CORREGIDO |
| 28 jun 2026 | `catch(Exception)` genérico en `PerfilPublicoService` | ✅ CORREGIDO |
| 28 jun 2026 | `GlobalControllerAdvice` violaba SRP | ✅ CORREGIDO |
| 28 jun 2026 | Lógica de autorización duplicada en `ResenaController` | ✅ CORREGIDO |
| 28 jun 2026 | `PerfilTutorService` con responsabilidades mezcladas | ✅ CORREGIDO |
| 28 jun 2026 | `MateriaService` creado desde `PerfilTutorService` | ✅ CORREGIDO |
| 28 jun 2026 | Dependencia circular Usuario ↔ PerfilTutor | ✅ CORREGIDO |

---

*Este documento se actualiza automáticamente cuando se corrige deuda técnica.*
