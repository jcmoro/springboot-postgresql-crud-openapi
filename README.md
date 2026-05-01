# spring-boot-postgresql-crud

REST CRUD for `Product`, built with **Spring Boot 3.5** and **PostgreSQL 16**, executed entirely in Docker.

---

## Stack

| Area | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 (Spring MVC, Spring Data JPA) |
| Build | Maven (`mvnw`) |
| Database | PostgreSQL 16 |
| Tests | JUnit 5, MockMvc, Testcontainers |
| API contract | OpenAPI 3.0.3 |
| Runtime | Docker + Docker Compose |
| Commands | `make` |

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

```bash
make help          # list all targets
make up            # bring up the stack
make down          # stop the stack (keeps data)
make down-v        # stop the stack and delete the Postgres volume
make logs          # tail app logs
make logs-db       # tail Postgres logs
make psql          # open psql inside the Postgres container
make test          # unit tests
make test-it       # integration tests (Testcontainers)
make verify        # build + all tests + openapi-lint
make openapi-lint  # validate the OpenAPI spec
make db-reset      # destroy and recreate the database
make ci            # verify + build the Docker image
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

DTOs and the controller interface (`ProductsApi`) are **generated from the OpenAPI spec** at every build (see [`docs/decisions/ADR-002-openapi-codegen.md`](./docs/decisions/ADR-002-openapi-codegen.md)).

## Repository layout

```
.
├── CLAUDE.md                  # operating rules for Claude Code
├── Dockerfile                 # multi-stage app image
├── Makefile                   # make targets
├── README.md                  # this file
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/
├── docker/
│   ├── docker-compose.yml     # stack: postgres + app
│   └── initdb.d/              # SQL bootstrap scripts (optional)
├── docs/                      # documentation (see docs/README.md)
└── src/
    └── main/java/com/example/spring_boot_postgresql_crud/
        ├── SpringBootPostgresqlCrudApplication.java
        ├── controller/        # HTTP layer, implements ProductsApi
        ├── service/           # business logic
        ├── repository/        # Spring Data JPA
        ├── model/             # JPA entity (Product)
        └── exception/         # domain exceptions + global advice
```

DTOs and the `ProductsApi` interface generated from `docs/api/openapi.yaml` live under `target/generated-sources/openapi/` and are not committed.

## Documentation

- [Execution plan](./docs/plan-ejecucion.md) — step-by-step plan for building the project.
- [Changelog](./docs/changelog.md) — API and DB schema changes.
- [Decisions (ADRs)](./docs/decisions/) — architecture and stack rationale.
- [Operations](./docs/operations/) — local setup, runbook, troubleshooting.
- [API](./docs/api/) — OpenAPI contract.

## Conventions

- **OpenAPI-first:** the spec is updated before (or alongside) the code.
- **Docker-only:** the app always runs inside a container.
- **No DB mocks:** integration tests use Testcontainers against real Postgres.
- **Constructor injection** everywhere.
- **Domain exceptions**, not raw `RuntimeException`.

## License

Educational project, no commercial license.
