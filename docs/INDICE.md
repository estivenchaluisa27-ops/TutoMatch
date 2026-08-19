# TutoMatch — Índice de documentación

## ¿Qué es TutoMatch?

Plataforma universitaria donde los estudiantes encuentran tutores por materia, reservan bloques de disponibilidad, pagan con tokens y dejan reseñas.

## Documentación

| Documento | Contenido |
|---|---|
| [README](../README.md) | Portada: qué es, inicio rápido |
| [Arquitectura](ARQUITECTURA.md) | Stack, paquetes, entidades, ciclo de vida de reservas, economía de tokens, chat, deudas técnicas |
| [Instalación](INSTALACION.md) | Requisitos, base de datos, configuración, perfiles, tests |
| [API y rutas](API.md) | Todas las rutas: páginas SSR, API JSON, WebSocket |

## Resumen rápido

- **Stack:** Spring Boot 4.0.7 · Java 17 · Thymeleaf · Spring Security + JWT · PostgreSQL · WebSocket · Apache POI
- **Flujo principal:** búsqueda → perfil tutor → reserva de bloque → confirmación → sesión → pago con tokens → reseña
- **Tokens:** 5 de bienvenida al registrarse; pago protegido contra concurrencia