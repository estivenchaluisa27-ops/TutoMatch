# Deuda Técnica Pendiente - TutoMatch

**Última actualización:** 28 de junio de 2026
**Proyecto:** TutoMatch - Sistema de Gestión de Tutorías

---

## 1. Dependencia Circular Usuario ↔ PerfilTutor (Prioridad: ALTA)

### Descripción

Existe una relación bidireccional entre las entidades `Usuario` y `PerfilTutor`:

```java
// Usuario.java
@OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
private PerfilTutor perfilTutor;

// PerfilTutor.java
@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "usuario_id", referencedColumnName = "id", insertable = false, updatable = false)
private Usuario usuario;
```

### Impacto

- **Riesgo de `LazyInitializationException`**: Al acceder a `Usuario.perfilTutor` fuera de una sesión Hibernate
- **Problemas de serialización JSON**: Referencias circulares pueden causar StackOverflow en Jackson
- **Ambigüedad del "owner" de la relación**: Ambos lados tienen `mappedBy` o `referencedColumnName`
- **Complejidad en consultas**: Queries deben usar `JOIN FETCH` explícitamente

### Intento de Corrección Fallido

Se intentó romper la circularidad eliminando la navegación inversa (`Usuario.perfilTutor`), pero múltiples queries JPQL en `PerfilTutorRepository` y `ResenaRepository` dependen de esta relación:

- `JOIN FETCH pt.usuario` en 8+ métodos de repositorio
- Métodos derivados de Spring Data que navegan la relación

### Solución Propuesta

Para resolver correctamente sin romper funcionalidades:

1. **Conservar solo la navegación de `PerfilTutor` a `Usuario`** (el ID foráneo está en `PerfilTutor.usuarioId`)
2. **Actualizar todos los repositorios** para usar `JOIN` explícitos o subconsultas
3. **Ejemplo de cambio en PerfilTutorRepository**:

```java
// ANTES (depende de la relación bidireccional)
@Query("SELECT pt FROM PerfilTutor pt JOIN FETCH pt.usuario WHERE pt.usuario.id = :usuarioId")
Optional<PerfilTutor> findByUsuarioId(@Param("usuarioId") Long usuarioId);

// DESPUÉS (navegación unidireccional con subconsulta)
@Query("SELECT pt FROM PerfilTutor pt WHERE pt.usuarioId = :usuarioId")
Optional<PerfilTutor> findByUsuarioId(@Param("usuarioId") Long usuarioId);
```

4. **Si se necesita datos del usuario**, usar:

```java
@Query("SELECT u FROM Usuario u WHERE u.id = :usuarioId")
Optional<Usuario> findUsuarioById(@Param("usuarioId") Long usuarioId);
```

### Archivos Afectados

- `src/main/java/com/uce/Tutomatch/model/Usuario.java`
- `src/main/java/com/uce/Tutomatch/model/PerfilTutor.java`
- `src/main/java/com/uce/Tutomatch/repository/PerfilTutorRepository.java`
- `src/main/java/com/uce/Tutomatch/repository/ResenaRepository.java`
- `src/main/java/com/uce/Tutomatch/repository/UsuarioRepository.java`
- Cualquier servicio que use `JOIN FETCH pt.usuario`

---

## 2. Eager Loading en PerfilTutor.usuario (Prioridad: MEDIA)

### Descripción

```java
@OneToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "usuario_id", referencedColumnName = "id", insertable = false, updatable = false)
private Usuario usuario;
```

### Impacto

- **N+1 Queries**: Al cargar una lista de `PerfilTutor`, se carga LAZY un `Usuario` por cada uno
- **Performance**: Queries innecesarias cuando no se necesita el usuario
- **Memoria**: Carga de objetos relacionados que quizás no se usen

### Solución Propuesta

```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id", referencedColumnName = "id", insertable = false, updatable = false)
private Usuario usuario;
```

Y en los queries que SÍ necesitan el usuario:

```java
@Query("SELECT pt FROM PerfilTutor pt LEFT JOIN FETCH pt.usuario WHERE pt.id = :id")
Optional<PerfilTutor> findByIdWithUsuario(@Param("id") Long id);
```

---

## 3. GlobalExceptionHandler No Maneja RedirectAttributes (Prioridad: MEDIA)

### Descripción

`GlobalExceptionHandler` retorna `ResponseEntity<Map<String, String>>` para APIs REST, pero los controllers MVC usan `RedirectAttributes` para pasar mensajes de error.

### Impacto

- Controllers como `ResenaController`, `ReservaController` usan `try-catch` local para manejar excepciones porque `GlobalExceptionHandler` no puede retornar redirects
- Violación del principio DRY - lógica de manejo de errores duplicada

### Solución Propuesta

Crear un handler separado para excepciones MVC:

```java
@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes attrs) {
        attrs.addFlashAttribute("error", ex.getMessage());
        return "redirect:/error-page";
    }
}
```

O migrar los controllers a usar el patrón REST con `ResponseEntity` y manejar redirects en el cliente.

---

## 4. PerfilTutorService Tiene Solo 1 Responsabilidad Restante (Prioridad: BAJA)

### Descripción

`PerfilTutorService` ahora solo tiene el método `obtenerOCrearPerfilTutor`. Podría integrarse en `PerfilTutorGestionService`.

### Análisis

Sin embargo, `PerfilTutorService` tiene una dependencia con `AuthenticationHelper` para obtener el usuario del contexto de seguridad, lo cual `PerfilTutorGestionService` no tiene. Fusionarlos requeriría agregar esa dependencia.

### Recomendación

Mantener separados por ahora. Si en el futuro `PerfilTutorGestionService` también necesita acceso al usuario autenticado, considerar fusión.

---

## Resumen de Métricas

| Métrica | Valor |
|---------|-------|
| Problemas de deuda técnica restantes | 4 |
| Prioridad ALTA | 1 |
| Prioridad MEDIA | 2 |
| Prioridad BAJA | 1 |
| Estimación de corrección | 4-6 horas |

---

## Historial de Correcciones

| Fecha | Problema | Estado |
|-------|----------|--------|
| 28 jun 2026 | `catch(Exception)` genéricos en `ResenaController` | ✅ CORREGIDO |
| 28 jun 2026 | `catch(Exception)` genérico en `PerfilPublicoService` | ✅ CORREGIDO |
| 28 jun 2026 | `GlobalControllerAdvice` violaba SRP | ✅ CORREGIDO |
| 28 jun 2026 | Lógica de autorización duplicada en `ResenaController` | ✅ CORREGIDO |
| 28 jun 2026 | `PerfilTutorService` con responsabilidades mezcladas | ✅ CORREGIDO |
| 28 jun 2026 | `MateriaService` creado desde `PerfilTutorService` | ✅ CORREGIDO |
| 28 jun 2026 | Dependencia circular Usuario ↔ PerfilTutor | ⚠️ PENDIENTE |

---

*Este documento se actualiza automáticamente cuando se corrige deuda técnica.*