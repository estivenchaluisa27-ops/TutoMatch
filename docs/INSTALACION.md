# TutoMatch — Instalación

## Requisitos

- **Java 17+** (proyecto configurado para Java 17)
- **Maven 3.9+**
- **PostgreSQL 16+** corriendo localmente
- (Opcional) Excel con catálogo de materias para importar (Apache POI)

## 1. Crear la base de datos

```sql
CREATE DATABASE tutomatch;
CREATE EXTENSION IF NOT EXISTS unaccent;  -- requerida por búsqueda y autocompletado
```

El esquema se crea solo (`spring.jpa.hibernate.ddl-auto=update`).

> ⚠️ **Sin la extensión `unaccent`, la búsqueda de tutores y el autocompletado de materias fallan.**

## 2. Configurar credenciales

Edita `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tutomatch
spring.datasource.username=<TU_USUARIO>
spring.datasource.password=<TU_PASSWORD>
```

> ⚠️ El archivo contiene valores de desarrollo local. No subas credenciales reales a GitHub; en dev local puedes externalizarlas igual que en prod (variables de entorno `DB_USERNAME`/`DB_PASSWORD`).

## 3. Ejecutar

```bash
mvn spring-boot:run
```

La app queda en `http://localhost:8080`.

## 4. Admin inicial

En `application-dev.properties` se definen el email y password del administrador (creado automáticamente por `DbSeed` al arrancar). Cambia estos valores antes de usarlo en producción.

## 5. Perfiles

| Perfil | Archivo | Uso |
|---|---|---|
| `dev` (activo por defecto) | `application-dev.properties` | Desarrollo local, `show-sql=true`, Thymeleaf sin caché |
| `prod` | `application-prod.properties` | Producción (base remota, JWT con cookie segura) |

Activar perfil: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`

## 6. Tests

```bash
mvn test
```

> `contextLoads` falla si no hay PostgreSQL accesible — es requisito del entorno, no del código.

## Docs relacionados

- [Arquitectura](ARQUITECTURA.md)
- [API y rutas](API.md)
- [Índice](INDICE.md)