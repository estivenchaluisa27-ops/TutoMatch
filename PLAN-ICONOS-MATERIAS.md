# PLAN MAESTRO: Sistema de Iconos por Materia

> Plano completo para replicar el sistema de iconos SVG por materia en cualquier versión de Tutomatch.

---

## 1. Dependencias necesarias (`pom.xml`)

```xml
<!-- Spring Boot Starter Data JPA -->
<!-- spring-boot-starter-thymeleaf -->
<!-- thymeleaf-layout-dialect -->
<!-- spring-boot-starter-webmvc -->
<!-- spring-boot-starter-security -->
<!-- spring-boot-starter-validation -->
<!-- postgresql -->
```

Ninguna dependencia extra. Los iconos son SVG inline — no se necesita Font Awesome, Iconify, etc. Solo Bootstrap Icons CDN para el fallback.

---

## 2. Base de datos

### Tabla `materias` (generada por Hibernate con `ddl-auto=update`)

| Columna       | Tipo              | Rol                              |
|---------------|-------------------|----------------------------------|
| `id`          | BIGINT (PK, auto) | Identificador                    |
| `nombre`      | VARCHAR(255) NOT NULL | Nombre de la materia         |
| `categoria`   | VARCHAR(255)      | Categoría (Informática, etc.)    |
| `descripcion` | TEXT              | Descripción                      |
| `icono`       | TEXT (nullable)   | **SVG inline del icono** ← CLAVE |

### Tabla `tutor_materias` (tabla puente)

| Columna            | Tipo    | Rol                |
|--------------------|---------|--------------------|
| `id`               | BIGINT  | PK                 |
| `perfil_tutor_id`  | BIGINT  | FK → perfil_tutores |
| `materia_id`       | BIGINT  | FK → materias      |

---

## 3. Backend — Archivos

### 3a. `model/Materia.java`

```java
@Entity
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "icono", columnDefinition = "TEXT")  // ← CAMPO CLAVE
    private String icono;

    @OneToMany(mappedBy = "materia")
    private List<TutorMateria> tutorMaterias = new ArrayList<>();

    public Materia() {}

    public Materia(String nombre, String categoria, String descripcion) {
        this.nombre = nombre; this.categoria = categoria; this.descripcion = descripcion;
    }

    // Constructor CON icono ← CLAVE
    public Materia(String nombre, String categoria, String descripcion, String icono) {
        this.nombre = nombre; this.categoria = categoria; this.descripcion = descripcion; this.icono = icono;
    }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }
}
```

### 3b. `dto/MateriaDTO.java`

```java
public class MateriaDTO {
    @NotBlank(message = "El nombre de la materia es obligatorio")
    private String nombre;
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    private String descripcion;
    private String icono;  // ← CAMPO CLAVE
}
```

### 3c. `repository/MateriaRepository.java`

```java
@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    List<Materia> findAllByOrderByCategoriaAscNombreAsc();
    Page<Materia> findAllByOrderByCategoriaAscNombreAsc(Pageable pageable);
    List<Materia> findByCategoriaOrderByNombreAsc(String categoria);
    Page<Materia> findByCategoriaOrderByNombreAsc(String categoria, Pageable pageable);
}
```

### 3d. `model/TutorMateria.java`

```java
@Entity
@Table(name = "tutor_materias")
public class TutorMateria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_tutor_id", nullable = false)
    private PerfilTutor perfilTutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;  // ← tm.materia.icono

    public Materia getMateria() { return materia; }
}
```

### 3e. `config/DbSeed.java` — Seed de materias + iconos

#### 7 constantes SVG (usar `currentColor` siempre)

```java
private static final String ICONO_CODE =
  "<svg viewBox=\"0 0 24 24\" width=\"1.5em\" height=\"1.5em\">" +
  "<path fill=\"currentColor\" d=\"m8 18-6-6 6-6 1.425 1.425-4.6 4.6L9.4 16.6Zm8 0-1.425-1.425 4.6-4.6L14.6 7.4 16 6l6 6Z\"/></svg>";

private static final String ICONO_CALCULATOR =
  "<svg viewBox=\"0 0 24 24\" width=\"1.5em\" height=\"1.5em\">" +
  "<path fill=\"currentColor\" d=\"M7 2h10a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2m0 2v4h10V4Zm0 6v2h2v-2Zm4 0v2h2v-2Zm4 0v2h2v-2Zm-8 4v2h2v-2Zm4 0v2h2v-2Zm4 0v2h2v-2Zm-8 4v2h2v-2Zm4 0v2h2v-2Zm4 0v2h2v-2Z\"/></svg>";

private static final String ICONO_DATABASE =
  "<svg viewBox=\"0 0 24 24\" width=\"1.5em\" height=\"1.5em\">" +
  "<path fill=\"currentColor\" d=\"M18.375 9.825Q21 8.65 21 7t-2.625-2.825T12 3T5.625 4.175T3 7t2.625 2.825T12 11t6.375-1.175m-3.812 3.463q1.537-.213 2.962-.688t2.45-1.237T21 9.5V12q0 1.1-1.025 1.863t-2.45 1.237t-2.962.688T12 16t-2.562-.213t-2.963-.687t-2.45-1.237T3 12V9.5q0 1.1 1.025 1.863t2.45 1.237t2.963.688T12 13.5t2.563-.213m0 5q1.537-.212 2.962-.687t2.45-1.237T21 14.5V17q0 1.1-1.025 1.863t-2.45 1.237t-2.962.688T12 21t-2.562-.213t-2.963-.687t-2.45-1.237T3 17v-2.5q0 1.1 1.025 1.863t2.45 1.237t2.963.688T12 18.5t2.563-.213\"/></svg>";

private static final String ICONO_PHYSICS =
  "<svg viewBox=\"0 0 24 24\" width=\"1.5em\" height=\"1.5em\">" +
  "<g fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.5\">" +
  "<path stroke-linecap=\"round\" d=\"M12 5.793a28 28 0 0 1 3.342 2.865A28 28 0 0 1 18.207 12m0 0c2.584-3.57 3.554-6.947 2.147-8.354S15.57 3.209 12 5.793a28 28 0 0 0-3.342 2.865A28 28 0 0 0 5.793 12m12.414 0c2.584 3.57 3.554 6.947 2.147 8.354c-1.043 1.043-3.17.78-5.654-.48M18.207 12a28 28 0 0 1-2.865 3.342A28 28 0 0 1 12 18.207m0 0a28 28 0 0 1-3.342-2.865A28 28 0 0 1 5.793 12M12 18.207c-3.57 2.584-6.947 3.554-8.354 2.147S3.209 15.57 5.793 12m0 0C3.21 8.43 2.24 5.053 3.646 3.646c1.043-1.043 3.17-.78 5.654.48\"/>" +
  "<circle cx=\"12\" cy=\"12\" r=\"2\"/></g></svg>";

private static final String ICONO_FLASK =
  "<svg viewBox=\"0 0 14 14\" width=\"1.5em\" height=\"1.5em\">" +
  "<path fill=\"none\" stroke=\"currentColor\" stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M9 .5v6l3.59 4.57a1.5 1.5 0 0 1-1.18 2.43H2.59a1.5 1.5 0 0 1-1.18-2.43L5 6.5v-6M3.5.5h7\"/></svg>";

private static final String ICONO_CHART =
  "<svg viewBox=\"0 0 24 24\" width=\"1.5em\" height=\"1.5em\">" +
  "<path fill=\"currentColor\" d=\"m16 11.78 4.24-7.33 1.73 1-5.23 9.05-6.51-3.75L5.46 19H22v2H2V3h2v14.54L9.5 8Z\"/></svg>";

private static final String ICONO_MATRIX =
  "<svg viewBox=\"0 0 24 24\" width=\"1.5em\" height=\"1.5em\">" +
  "<path fill=\"currentColor\" d=\"M2 2h4v2H4v16h2v2H2Zm18 2h-2V2h4v20h-4v-2h2ZM9 5h1v5h1v1H8v-1h1V6l-1 .5v-1Zm6 8h1v5h1v1h-3v-1h1v-4l-1 .5v-1Zm-6 0c1.1 0 2 1.34 2 3s-.9 3-2 3-2-1.34-2-3 .9-3 2-3m0 1c-.55 0-1 .9-1 2s.45 2 1 2 1-.9 1-2-.45-2-1-2m6-9c1.1 0 2 1.34 2 3s-.9 3-2 3-2-1.34-2-3 .9-3 2-3m0 1c-.55 0-1 .9-1 2s.45 2 1 2 1-.9 1-2-.45-2-1-2\"/></svg>";
```

#### Mapeo: nombre de materia → constante

| Materia | Constante |
|---------|-----------|
| Programación I, Programación II | `ICONO_CODE` |
| Cálculo I, Cálculo II | `ICONO_CALCULATOR` |
| Bases de Datos I, Bases de Datos II | `ICONO_DATABASE` |
| Física I, Física II | `ICONO_PHYSICS` |
| Química General | `ICONO_FLASK` |
| Estadística | `ICONO_CHART` |
| Álgebra Lineal | `ICONO_MATRIX` |
| Contabilidad | `ICONO_CALCULATOR` |

#### Lógica del seed (método `seedMaterias()`)

```
1. Buscar todas las materias existentes
2. Para cada materia SIN icono (null o blank):
   - switch(nombre) → asignar constante SVG
   - si hay cambios, guardar todo
3. Si la tabla está vacía:
   - Insertar las 12 materias con su icono
```

```java
@Component
public class DbSeed implements CommandLineRunner {
    // Constantes SVG...

    @Transactional
    public void run(String... args) {
        seedMaterias();
        // otros seeds...
    }

    private void seedMaterias() {
        List<Materia> todas = materiaRepository.findAll();
        boolean huboCambios = false;
        for (Materia m : todas) {
            if (m.getIcono() == null || m.getIcono().isBlank()) {
                String icono = switch (m.getNombre()) {
                    case "Programación I", "Programación II" -> ICONO_CODE;
                    case "Cálculo I", "Cálculo II" -> ICONO_CALCULATOR;
                    case "Bases de Datos I", "Bases de Datos II" -> ICONO_DATABASE;
                    case "Física I", "Física II" -> ICONO_PHYSICS;
                    case "Química General" -> ICONO_FLASK;
                    case "Estadística" -> ICONO_CHART;
                    case "Álgebra Lineal" -> ICONO_MATRIX;
                    case "Contabilidad" -> ICONO_CALCULATOR;
                    default -> null;
                };
                if (icono != null) { m.setIcono(icono); huboCambios = true; }
            }
        }
        if (huboCambios) materiaRepository.saveAll(todas);

        if (materiaRepository.count() == 0) {
            materiaRepository.saveAll(List.of(
                new Materia("Programación I", "Informática", "Fundamentos de algoritmos y lógica de programación.", ICONO_CODE),
                new Materia("Programación II", "Informática", "Estructuras de datos y programación orientada a objetos.", ICONO_CODE),
                new Materia("Cálculo I", "Matemáticas", "Límites, derivadas y aplicaciones.", ICONO_CALCULATOR),
                new Materia("Cálculo II", "Matemáticas", "Integrales, series y cálculo multivariable.", ICONO_CALCULATOR),
                new Materia("Bases de Datos I", "Informática", "Modelado entidad-relación y SQL.", ICONO_DATABASE),
                new Materia("Bases de Datos II", "Informática", "Administración, optimización y bases NoSQL.", ICONO_DATABASE),
                new Materia("Física I", "Ciencias", "Mecánica clásica y termodinámica.", ICONO_PHYSICS),
                new Materia("Física II", "Ciencias", "Electromagnetismo y óptica.", ICONO_PHYSICS),
                new Materia("Química General", "Ciencias", "Estructura atómica, enlaces y reacciones.", ICONO_FLASK),
                new Materia("Estadística", "Matemáticas", "Probabilidad, inferencia y análisis de datos.", ICONO_CHART),
                new Materia("Álgebra Lineal", "Matemáticas", "Vectores, matrices y transformaciones lineales.", ICONO_MATRIX),
                new Materia("Contabilidad", "Administración", "Registro de operaciones y estados financieros.", ICONO_CALCULATOR)
            ));
        }
    }
}
```

---

## 4. Controladores

### HomeController (`GET /`)

```java
List<Materia> todasMaterias = materiaRepository.findAllByOrderByCategoriaAscNombreAsc();
Map<String, List<Materia>> materiasPorCategoria = todasMaterias.stream()
    .collect(Collectors.groupingBy(Materia::getCategoria, LinkedHashMap::new, Collectors.toList()));
model.addAttribute("materiasPorCategoria", materiasPorCategoria);
```

### TutorProfileController (`GET /tutor/perfil`)

```java
PerfilTutor perfil = obtenerOCrearPerfilTutor(usuarioId);
List<TutorMateria> materias = perfil.getMaterias();
List<Materia> todasMaterias = materiaRepository.findAllByOrderByCategoriaAscNombreAsc();
Map<String, List<Materia>> materiasPorCategoria = todasMaterias.stream()
    .collect(Collectors.groupingBy(Materia::getCategoria, LinkedHashMap::new, Collectors.toList()));
model.addAttribute("perfil", perfil);
model.addAttribute("materias", materias);
model.addAttribute("materiasPorCategoria", materiasPorCategoria);
```

### AdminController (`GET /admin/materias`)

```java
List<Materia> todas = materiaRepository.findAllByOrderByCategoriaAscNombreAsc();
Map<String, List<Materia>> materiasPorCategoria = todas.stream()
    .collect(Collectors.groupingBy(Materia::getCategoria, LinkedHashMap::new, Collectors.toList()));
model.addAttribute("materiasPorCategoria", materiasPorCategoria);
model.addAttribute("categorias", materiasPorCategoria.keySet());
// BUG: template usa ${materias} pero acá se envía materiasPorCategoria (Map)
```

### AdminController — POST /agregar y /editar (guardan icono)

```java
// Agregar
Materia materia = new Materia(dto.getNombre(), dto.getCategoria(), dto.getDescripcion(), dto.getIcono());
materiaRepository.save(materia);

// Editar
Materia materia = materiaRepository.findById(id).orElseThrow(...);
materia.setIcono(dto.getIcono());
materiaRepository.save(materia);
```

### SearchController (`GET /tutor/{id}` — perfil público)

```java
PerfilTutor tutor = perfilTutorService.obtenerPorId(id);
model.addAttribute("tutor", tutor);
// tutor.getMaterias() → List<TutorMateria>, cada una con .materia.icono
```

---

## 5. Frontend — Templates

### 5a. `fragments/head.html` — Bootstrap Icons CDN

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
```

### 5b. `templates/home.html` — Renderizado exitoso (referencia)

```html
<div class="card-icon mx-auto mb-3" th:attr="data-categoria=${m.categoria}">
    <th:block th:if="${m.icono != null}">
        <span th:utext="${m.icono}"></span>
    </th:block>
    <i th:if="${m.icono == null}" class="bi bi-book fs-3"></i>
</div>
```

### 5c. Patrón para `TutorMateria` (iterando `tm`)

```html
<div class="d-flex align-items-center">
    <span class="me-2" th:attr="data-categoria=${tm.materia.categoria}">
        <th:block th:if="${tm.materia.icono != null}">
            <span th:utext="${tm.materia.icono}"></span>
        </th:block>
        <i th:if="${tm.materia.icono == null}" class="bi bi-book fs-5"></i>
    </span>
    <span class="fw-semibold" th:text="${tm.materia.nombre}">Materia</span>
</div>
```

### 5d. Cambios por archivo

| Archivo | Línea | Código actual | Código nuevo |
|---------|-------|---------------|--------------|
| `perfil-publico.html` | 48-51 | `<i class="bi bi-book me-2 text-primary"></i>` + `<span th:text="${m.nombre}">` | Patrón 5c con `m.materia.icono` y `m.materia.categoria` |
| `perfil-tutor.html` | 101-102 | `<span class="fw-semibold" th:text="${tm.materia.nombre}">` | Patrón 5c |
| `mi-perfil.html` | 101-102 | `<span class="fw-semibold" th:text="${tm.materia.nombre}">` | Patrón 5c |
| `admin-materias.html` | 64 | `<tr th:each="m : ${materias}">` | **BUG** — ver sección 7 |

---

## 6. CSS — `static/css/style.css`

```css
.card-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.card-icon svg {
    width: 3em;
    height: 3em;
    transition: transform 0.3s ease;
}

.card:hover .card-icon svg {
    transform: scale(1.15);
}

/* Colores por categoría */
[data-categoria="Informática"]    { color: #2563eb; }
[data-categoria="Matemáticas"]    { color: #dc2626; }
[data-categoria="Ciencias"]       { color: #059669; }
[data-categoria="Administración"] { color: #7c3aed; }
[data-categoria="Idiomas"]        { color: #d97706; }
[data-categoria="Ingeniería"]     { color: #ea580c; }
[data-categoria="General"]        { color: #0891b2; }
```

---

## 7. BUG conocido en `admin-materias.html`

**Problema:** El template itera `${materias}` pero `AdminController` envía un `Map` como `"materiasPorCategoria"`.

**Solución A — corregir el controller (más simple):**
```java
model.addAttribute("materias", todas); // List<Materia> plana
```

**Solución B — corregir el template:**
```html
<th:block th:each="entry : ${materiasPorCategoria}">
    <tr th:each="m : ${entry.value}">
        <td class="px-4 fw-semibold" th:text="${m.nombre}">Materia</td>
        <td><span class="badge bg-secondary" th:text="${m.categoria}">Categoría</span></td>
        <td class="text-end px-4"><!-- acciones --></td>
    </tr>
</th:block>
```

---

## 8. Checklist de implementación

- [ ] `Materia.java` — campo `icono` (TEXT) + getter/setter + constructor de 4 params
- [ ] `MateriaDTO.java` — campo `icono`
- [ ] `MateriaRepository.java` — `findAllByOrderByCategoriaAscNombreAsc()`
- [ ] `DbSeed.java` — 7 constantes SVG + seed 12 materias con iconos
- [ ] `fragments/head.html` — CDN Bootstrap Icons
- [ ] `style.css` — `.card-icon` + colores `[data-categoria]`
- [ ] `home.html` — renderiza iconos con `th:utext`
- [ ] `perfil-publico.html` — renderiza `tm.materia.icono`
- [ ] `perfil-tutor.html` — renderiza `tm.materia.icono`
- [ ] `mi-perfil.html` — renderiza `tm.materia.icono`
- [ ] `admin-materias.html` — corregido BUG + renderiza `m.icono`

---

## 9. Reglas técnicas clave

1. **Todos los SVG deben usar `currentColor`** como fill/stroke para heredar el color CSS de `data-categoria`
2. Atributos estándar del SVG: `viewBox="0 0 24 24" width="1.5em" height="1.5em"`
3. **`th:utext`** renderiza el SVG sin escapar — seguro porque el contenido es controlado por seed/admin
4. **Fallback**: si `icono == null`, mostrar `<i class="bi bi-book">` de Bootstrap Icons
5. **Admin CRUD**: el campo `icono` permite pegar cualquier SVG manualmente
