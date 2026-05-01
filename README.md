# spring-boot-postgresql-crud

CRUD REST de `Product` construido con **Spring Boot 3.5** y **PostgreSQL 16**, ejecutado íntegramente en Docker. Proyecto de referencia/aprendizaje basado en [este artículo de Medium](https://rameshfadatare.medium.com/spring-boot-crud-example-with-postgresql-926c87f0129a) y adaptado al subset pragmático definido en [`docs/decisions/ADR-001-pragmatic-subset.md`](./docs/decisions/ADR-001-pragmatic-subset.md).

---

## Stack

| Área | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.14 (Spring MVC, Spring Data JPA) |
| Build | Maven (`mvnw`) |
| BD | PostgreSQL 16 |
| Tests | JUnit 5, MockMvc, Testcontainers |
| Contrato API | OpenAPI 3.1.0 |
| Runtime | Docker + Docker Compose |
| Comandos | `make` |

## Pre-requisitos

- JDK 17
- Docker Desktop (Compose v2)
- `make`

## Quick start

```bash
# Levantar el stack completo (Postgres + app)
make up

# Probar
curl http://localhost:8080/api/products
```

Para más detalle, ver [`docs/operations/development.md`](./docs/operations/development.md).

## Comandos

```bash
make help          # listar todos los targets
make up            # levantar stack
make down          # parar stack (mantiene datos)
make down-v        # parar y borrar volumen de Postgres
make logs          # logs de la app
make logs-db       # logs de Postgres
make psql          # abrir psql dentro del contenedor de Postgres
make test          # tests unitarios
make test-it       # tests de integración (Testcontainers)
make verify        # build + todos los tests + openapi-lint
make openapi-lint  # validar el spec OpenAPI
make db-reset      # destruir y recrear la BD
make ci            # verify + build de la imagen Docker
```

## API

Contrato OpenAPI 3.1.0 en [`docs/api/openapi.yaml`](./docs/api/openapi.yaml).

| Método | Ruta                  | Acción          |
|--------|-----------------------|-----------------|
| GET    | `/api/products`       | Listar          |
| GET    | `/api/products/{id}`  | Obtener por ID  |
| POST   | `/api/products`       | Crear           |
| PUT    | `/api/products/{id}`  | Actualizar      |
| DELETE | `/api/products/{id}`  | Eliminar        |

Errores con formato [RFC 9457 `application/problem+json`](https://www.rfc-editor.org/rfc/rfc9457.html).

## Estructura del repositorio

```
.
├── CLAUDE.md                  # reglas para Claude Code
├── Dockerfile                 # imagen multi-stage de la app
├── Makefile                   # targets de make
├── README.md                  # este archivo
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/
├── docker/
│   ├── docker-compose.yml     # stack: postgres + app
│   └── initdb.d/              # scripts SQL de inicialización (opcional)
├── docs/                      # documentación (ver docs/README.md)
└── src/
    ├── main/java/com/example/spring_boot_postgresql_crud/
    │   ├── SpringBootPostgresqlCrudApplication.java
    │   ├── controller/        # capa HTTP, expone DTOs
    │   ├── service/           # lógica de negocio
    │   ├── repository/        # Spring Data JPA
    │   ├── model/             # entidades JPA + DTOs (records)
    │   └── exception/         # excepciones de dominio + advice global
    └── test/java/...
```

## Documentación

- [Plan de ejecución](./docs/plan-ejecucion.md) — pasos para construir el proyecto.
- [Changelog](./docs/changelog.md) — cambios en API y BD.
- [Decisiones (ADRs)](./docs/decisions/) — arquitectura y stack.
- [Operations](./docs/operations/) — setup local, runbook, troubleshooting.
- [API](./docs/api/) — contrato OpenAPI.

## Convenciones

- **OpenAPI-first:** el spec se actualiza antes (o a la vez) que el código.
- **Docker-only:** la app corre siempre en contenedor.
- **No mocks de BD:** los tests de integración usan Testcontainers contra Postgres real.
- **Constructor injection** en todos los beans.
- **Excepciones de dominio**, no `RuntimeException` crudo.

## Licencia

Proyecto educativo, sin licencia comercial.
