# spring-boot-postgresql-crud

REST CRUD for `Product`, built with **Spring Boot 3.5** and **PostgreSQL 16**, executed entirely in Docker.

---

## Stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 (Spring MVC, Spring Data JPA) |
| Architecture | DDD + Hexagonal (Ports & Adapters) — see `docs/decisions/ADR-003-pivot-to-ddd-hexagonal.md` |
| Build | Maven (`mvnw`) |
| Database | PostgreSQL 16 |
| Tests | JUnit 5 + AssertJ, MockMvc, Testcontainers |
| Static analysis | SpotBugs 4.8 + FindSecBugs, Checkstyle 10.21 |
| API contract | OpenAPI 3.0.3 (codegen via `openapi-generator-maven-plugin`) |
| Runtime | Docker + Docker Compose |
| Commands | `make` (grouped help via `make help`) |

## Prerequisites

- JDK 17
- Docker Desktop (Compose v2)
- `make`

## Quick start

```bash
# Bring up the full stack (Postgres + app)
make up

# Try it
curl http://localhost:8080/api/products
```

For more detail, see [`docs/operations/development.md`](./docs/operations/development.md).

## Commands

`make help` lists all targets grouped by section. Quick reference:

```bash
# General
make help              # grouped help

# Docker stack
make up                # bring up the stack (postgres + app)
make down              # stop the stack (keeps the data volume)
make down-v            # stop and DELETE the data volume
make ps                # list stack containers
make logs              # tail app logs
make logs-db           # tail Postgres logs
make psql              # open psql inside the Postgres container

# Build & run
make build             # compile and package the app jar
make run               # run the app from the host (Mode A; requires postgres up)

# OpenAPI
make openapi-generate  # regenerate ProductsApi from openapi.yaml
make openapi-lint      # validate the OpenAPI spec (alias of openapi-generate)

# Static analysis
make spotbugs          # SpotBugs + FindSecBugs
make spotbugs-report   # generate target/spotbugs.html
make checkstyle        # Checkstyle
make lint              # spotbugs + checkstyle

# Tests
make test              # unit tests only (surefire, no Docker)
make test-it           # integration tests only (failsafe; requires Docker)
make verify            # compile + unit + integration + lint — full pre-CI gate (Docker required)
make verify-no-it      # compile + unit + lint, skipping integration tests (no Docker)

# Database
make db-reset          # destroy and recreate the postgres volume
make fixtures          # reset products table and load 5 sample rows (requires `make up`)

# CI
make ci                # verify + build the Docker image
```

## API

OpenAPI 3.0.3 contract in [`docs/api/openapi.yaml`](./docs/api/openapi.yaml).

| Method | Path                  | Action     |
|--------|-----------------------|------------|
| GET    | `/api/products`       | List       |
| GET    | `/api/products/{id}`  | Get by ID  |
| POST   | `/api/products`       | Create     |
| PUT    | `/api/products/{id}`  | Update     |
| DELETE | `/api/products/{id}`  | Delete     |

Errors follow [RFC 9457 `application/problem+json`](https://www.rfc-editor.org/rfc/rfc9457.html).

The controller interface (`ProductsApi`) is **generated from the OpenAPI spec** at every build. DTOs are hand-written Java `record`s under `src/main/java/.../infrastructure/web/dto/`; the compiler enforces alignment because `ProductController` implements the generated interface and the interface references the records by name. See [`docs/decisions/ADR-002-openapi-codegen.md`](./docs/decisions/ADR-002-openapi-codegen.md).

## Repository layout

```
.
├── CLAUDE.md                       # operating rules for Claude Code
├── Dockerfile                      # multi-stage app image
├── Makefile                        # make targets (grouped via ##@)
├── README.md                       # this file
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/
├── checkstyle.xml                  # Checkstyle ruleset
├── checkstyle-suppressions.xml     # Checkstyle suppressions
├── spotbugs-exclude.xml            # SpotBugs filter file
├── docker/
│   ├── docker-compose.yml          # stack: postgres + app
│   └── initdb.d/                   # SQL bootstrap scripts (optional)
├── docs/                           # documentation (see docs/README.md)
└── src/
    └── main/java/com/example/spring_boot_postgresql_crud/
        ├── SpringBootPostgresqlCrudApplication.java
        ├── domain/                              # pure Java; zero infra deps
        │   ├── model/Product                    # final POJO with invariants
        │   ├── exception/ResourceNotFoundException
        │   └── port/ProductRepository           # outbound port (interface)
        ├── application/
        │   └── service/                         # use case (interface + impl)
        └── infrastructure/                      # adapters
            ├── persistence/                     # JPA adapter (entity, mapper, port impl)
            └── web/                             # web adapter
                ├── ProductController            # implements generated ProductsApi
                ├── ProductWebMapper             # Domain ↔ DTO
                ├── GlobalExceptionHandler       # RFC 9457 error mapping
                └── dto/                         # OpenAPI DTOs (records)
```

DDD + Hexagonal layout — see `docs/decisions/ADR-003-pivot-to-ddd-hexagonal.md`. The `ProductsApi` interface generated from `docs/api/openapi.yaml` lives under `target/generated-sources/openapi/` (in package `infrastructure.web`) and is not committed.

## Documentation

- [Execution plan](./docs/plan-ejecucion.md) — step-by-step plan for building the project.
- [Changelog](./docs/changelog.md) — API and DB schema changes.
- [Decisions (ADRs)](./docs/decisions/README.md) — architecture and stack rationale.
- [Operations](./docs/operations/README.md) — local setup, runbook, troubleshooting.
- [API](./docs/api/README.md) — OpenAPI contract.

## Conventions

- **OpenAPI-first:** the spec is updated before (or alongside) the code.
- **DDD + Hexagonal:** `domain/` is pure Java with zero infrastructure imports; ports live in `domain/port/`, adapters in `infrastructure/`.
- **Docker-only:** the app always runs inside a container.
- **No DB mocks:** integration tests use Testcontainers against real Postgres.
- **Constructor injection** everywhere.
- **Domain exceptions**, not raw `RuntimeException`.
- **Static analysis** (`make lint`) is part of `make verify`.

## License

Educational project, no commercial license.
