# TutoMatch

Plataforma universitaria (UCE) que conecta estudiantes con tutores por materia: búsqueda con autocompletado, reserva de bloques de disponibilidad, **economía de tokens** para el pago de tutorías y reseñas post-sesión. Incluye chat en tiempo real estudiante–tutor.

## Stack

Spring Boot 4.0.7 · Java 17 · Thymeleaf · Spring Security + JWT · PostgreSQL · WebSocket · Apache POI

## Inicio rápido

```bash
# 1. Requisitos: Java 17+, Maven 3.9+, PostgreSQL local
CREATE DATABASE tutomatch;

# 2. Configura credenciales en src/main/resources/application-dev.properties

# 3. Ejecutar
mvn spring-boot:run   # → http://localhost:8080
```

## Funcionalidades

- Búsqueda de tutores por materia con autocompletado (unaccent/ILIKE)
- Perfiles de tutor con disponibilidad semanal
- Reservas con ciclo de vida: `PENDIENTE → CONFIRMADA → PENDIENTE_PAGO → FINALIZADA`
- Billetera de tokens: 5 de bienvenida, pago de tutorías protegido contra concurrencia
- Reseñas, notificaciones y chat en tiempo real
- Panel de administración (tutores, materias, reseñas, configuración)

## Documentación

- [Índice de documentación](docs/INDICE.md)
- [Arquitectura](docs/ARQUITECTURA.md)
- [Instalación](docs/INSTALACION.md)
- [API y rutas](docs/API.md)

## Estado

Proyecto académico. Token economy en Fase 2.1; pendiente Fase 3 (masterclass). Ver deudas técnicas en [Arquitectura](docs/ARQUITECTURA.md).
