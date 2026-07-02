# Plan: Layout Dividido tipo LinkedIn

**Proyecto:** TutoMatch — Página de Inicio (`home.html`)
**Última actualización:** 2 de julio de 2026
**Estado:** Pendiente de implementación

---

## ✅ Objetivo

Convertir el `home.html` actual (secciones verticales) en un layout de **2 columnas**: catálogo de materias (~75%) + panel lateral derecho con tutores recomendados (~25%), con navegación híbrida tipo LinkedIn (scroll independiente en cada panel).

---

## 📁 Archivos a modificar

| Archivo | Ruta |
|---------|------|
| HTML | `src/main/resources/templates/home.html` |
| CSS | `src/main/resources/static/css/style.css` |

---

## 📄 `style.css` — Nuevas clases

Insertar **después del bloque `.avatar-circle-lg`** (línea ~527), **antes de `/* ---------- Badges ---------- */`**:

```css
/* ---------- Linkedin-Like Split Layout ---------- */

.main-layout {
    height: 70vh;
}

.panel-scrollable {
    overflow-y: auto;
    overflow-x: hidden;
    height: 100%;
    scrollbar-width: thin;
}

.panel-scrollable::-webkit-scrollbar {
    width: 6px;
}

.panel-scrollable::-webkit-scrollbar-track {
    background: transparent;
}

.panel-scrollable::-webkit-scrollbar-thumb {
    background: var(--border-card);
    border-radius: 3px;
}

.tutor-sidebar {
    background: var(--bg-light-utility);
    border-radius: var(--radius-lg);
    padding: 1.25rem;
    border: 1px solid var(--border-card);
}

.tutor-sidebar .card {
    border: none;
    box-shadow: var(--shadow-sm);
    transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.tutor-sidebar .card:hover {
    transform: translateY(-3px);
    box-shadow: var(--shadow-md);
}

.avatar-circle-sm {
    width: 36px;
    height: 36px;
    font-size: 0.9rem;
}
```

---

## 📄 `home.html` — Estructura

### 🔴 Eliminar

Desde la línea 53 hasta la línea 113 (ambas secciones: `<!-- Categorías / Materias -->` y `<!-- Tutores Recomendados -->`)

### 🟢 Insertar

```html
    <!-- DESKTOP: Layout Dividido (> lg) -->
    <section class="container-fluid pb-4 pt-0 d-none d-lg-block">
        <div class="row main-layout g-4">
            <!-- Izquierda: Materias (75%) -->
            <div class="col-lg-9 panel-scrollable pt-4">
                <h2 class="mb-4 fw-bold">Materias por Categor&iacute;a</h2>
                <div th:each="entry : ${materiasPorCategoria}" class="mb-4">
                    <h4 class="fw-semibold mb-3 section-header" th:text="${entry.key}">Categor&iacute;a</h4>
                    <div class="row row-cols-1 row-cols-md-3 row-cols-lg-4 g-3">
                        <div th:each="m : ${entry.value}" class="col card-fade-in" th:style="'animation-delay: ' + (${m.id} * 0.03) + 's'">
                            <a th:href="@{/buscar(materia=${m.nombre})}" class="text-decoration-none">
                                <div class="card h-100 text-center border-0">
                                    <div class="card-body">
                                        <div class="card-icon mx-auto mb-3" th:attr="data-categoria=${m.categoria}">
                                            <th:block th:if="${m.icono != null}">
                                                <span th:utext="${m.icono}"></span>
                                            </th:block>
                                            <i th:if="${m.icono == null}" class="bi bi-book fs-3" th:style="'color: var(--categoria-' + ${#strings.toLowerCase(m.categoria)} + ')'"></i>
                                        </div>
                                        <h6 class="card-title fw-semibold mb-1" th:text="${m.nombre}">Materia</h6>
                                        <small class="text-muted text-truncate d-block" th:text="${m.descripcion}">Descripci&oacute;n</small>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Derecha: Tutores (25%) -->
            <div class="col-lg-3 panel-scrollable tutor-sidebar pt-4">
                <h5 class="fw-bold mb-3" th:if="${not #lists.isEmpty(recomendados)}">Tutores Recomendados</h5>
                <div th:each="t : ${recomendados}" class="mb-3 card-fade-in" th:style="'animation-delay: ' + (${t.id} * 0.05) + 's'">
                    <div class="card h-100">
                        <div class="card-body p-3">
                            <div class="d-flex align-items-center mb-2">
                                <div class="avatar-circle avatar-circle-sm me-2">
                                    <span th:text="${#strings.substring(t.usuario.nombreCompleto, 0, 1)}">I</span>
                                </div>
                                <div>
                                    <h6 class="fw-bold mb-0 small" th:text="${t.usuario.nombreCompleto}">Nombre</h6>
                                    <small class="text-muted" th:text="'Semestre ' + ${t.semestre}">Semestre</small>
                                </div>
                            </div>
                            <p class="card-text small text-muted mb-2" th:text="${#strings.abbreviate(t.descripcion, 80)}">Descripci&oacute;n</p>
                            <div class="d-flex justify-content-between align-items-center star-rating">
                                <span>
                                    <i class="bi bi-star-fill text-warning"></i>
                                    <span class="fw-semibold ms-1 small" th:text="${t.calificacionPromedio}">5.0</span>
                                </span>
                            </div>
                            <a th:href="@{/tutor/{id}(id=${t.id})}" class="btn btn-outline-primary btn-sm w-100 mt-2">
                                Ver perfil
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- MÓVIL: Materias primero (< lg) -->
    <section class="container mb-5 d-lg-none stagger-fade stagger-fade-5">
        <h2 class="text-center mb-4 fw-bold">Materias por Categor&iacute;a</h2>
        <div th:each="entry : ${materiasPorCategoria}" class="mb-4">
            <h4 class="fw-semibold mb-3 section-header" th:text="${entry.key}">Categor&iacute;a</h4>
            <div class="row row-cols-1 row-cols-md-3 g-3">
                <div th:each="m : ${entry.value}" class="col card-fade-in" th:style="'animation-delay: ' + (${m.id} * 0.03) + 's'">
                    <a th:href="@{/buscar(materia=${m.nombre})}" class="text-decoration-none">
                        <div class="card h-100 text-center border-0">
                            <div class="card-body">
                                <div class="card-icon mx-auto mb-3" th:attr="data-categoria=${m.categoria}">
                                    <th:block th:if="${m.icono != null}"><span th:utext="${m.icono}"></span></th:block>
                                    <i th:if="${m.icono == null}" class="bi bi-book fs-3" th:style="'color: var(--categoria-' + ${#strings.toLowerCase(m.categoria)} + ')'"></i>
                                </div>
                                <h6 class="card-title fw-semibold mb-1" th:text="${m.nombre}">Materia</h6>
                                <small class="text-muted text-truncate d-block" th:text="${m.descripcion}">Descripci&oacute;n</small>
                            </div>
                        </div>
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- MÓVIL: Tutores después (< lg) -->
    <section th:if="${not #lists.isEmpty(recomendados)}" class="py-5 d-lg-none" style="background: var(--bg-light-utility);">
        <div class="container">
            <h2 class="text-center fw-bold mb-4 stagger-fade stagger-fade-6">Tutores Recomendados</h2>
            <div class="row row-cols-1 row-cols-md-2 g-4">
                <div th:each="t : ${recomendados}" class="col card-fade-in" th:style="'animation-delay: ' + (${t.id} * 0.05) + 's'">
                    <div class="card h-100">
                        <div class="card-body">
                            <div class="d-flex align-items-center mb-3">
                                <div class="avatar-circle me-3">
                                    <span th:text="${#strings.substring(t.usuario.nombreCompleto, 0, 1)}">I</span>
                                </div>
                                <div>
                                    <h6 class="fw-bold mb-0" th:text="${t.usuario.nombreCompleto}">Nombre</h6>
                                    <small class="text-muted" th:text="'Semestre ' + ${t.semestre}">Semestre</small>
                                </div>
                            </div>
                            <p class="card-text small text-muted mb-2" th:text="${#strings.abbreviate(t.descripcion, 100)}">Descripci&oacute;n</p>
                            <div class="d-flex justify-content-between align-items-center star-rating">
                                <span>
                                    <i class="bi bi-star-fill text-warning"></i>
                                    <span class="fw-semibold ms-1" th:text="${t.calificacionPromedio}">5.0</span>
                                </span>
                            </div>
                            <a th:href="@{/tutor/{id}(id=${t.id})}" class="btn btn-outline-primary btn-sm w-100 mt-3">Ver perfil</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
```

---

## ⚡ Scroll Híbrido — Cómo funciona

| Elemento | CSS |
|----------|-----|
| Contenedor padre `.row.main-layout` | `height: 70vh` |
| Cada panel `.panel-scrollable` | `overflow-y: auto; height: 100%` |
| Sidebar `.tutor-sidebar` | mismo scroll + fondo y borde distintivos |

- Scroll independiente: al hacer scroll en materias, los tutores no se mueven y viceversa.
- No usa `position: sticky`, es un layout de dos columnas con scroll propio.
- En móvil/tablet (< lg) se mantiene el layout vertical original.

---

## 📱 Responsive

| Breakpoint | Layout |
|------------|--------|
| `>= lg` (escritorio) | Dividido 75/25 con scroll independiente |
| `< lg` (móvil/tablet) | Vertical: materias primero, tutores después |

Controlado con clases Bootstrap: `d-none d-lg-block` / `d-lg-none`.

---

## ✅ Checklist de Verificación

| # | Verificación | Hecho |
|---|-------------|-------|
| 1 | Hero section intacto (no se modifica) | ☐ |
| 2 | Desktop: layout 75/25 visible solo en `>= lg` | ☐ |
| 3 | Mobile: layout vertical visible solo en `< lg` | ☐ |
| 4 | Panel izquierdo materias: `col-lg-9`, scroll propio | ☐ |
| 5 | Panel derecho tutores: `col-lg-3`, scroll propio, fondo `--bg-light-utility` | ☐ |
| 6 | Scroll independiente: un panel no mueve al otro | ☐ |
| 7 | Dark mode: colores heredan correctamente | ☐ |
| 8 | Animaciones `card-fade-in` y `stagger-fade` funcionan | ☐ |
| 9 | Sin errores de Thymeleaf en consola | ☐ |
| 10 | Mobile se ve igual que antes (misma experiencia) | ☐ |
| 11 | Avatar `.avatar-circle-sm` se renderiza bien (36px) | ☐ |

---

## 🎯 Pendientes Futuros

| Item | Descripción |
|------|-------------|
| Algoritmo de recomendación | Formulario en registro → guardar intereses del estudiante → filtrar tutores por materias de interés |
| Datos en tarjeta de tutor | Mostrar materias que enseña cada tutor en la sidebar |
| Botón "Ver más tutores" | Paginación o enlace a página completa de búsqueda de tutores |
