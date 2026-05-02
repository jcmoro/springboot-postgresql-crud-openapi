SHELL := /bin/bash
.DEFAULT_GOAL := help

# Validate JAVA_HOME; if the path doesn't have bin/java, fall back to
# /usr/libexec/java_home on macOS. Override per-invocation with `make JAVA_HOME=...`.
ifeq ($(wildcard $(JAVA_HOME)/bin/java),)
  ifeq ($(shell uname),Darwin)
    JAVA_HOME := $(shell /usr/libexec/java_home 2>/dev/null)
  endif
endif
export JAVA_HOME

COMPOSE := docker compose -f docker/docker-compose.yml
MVN     := ./mvnw

.PHONY: help \
        up down down-v logs logs-db ps psql \
        build run \
        openapi-generate openapi-lint \
        spotbugs spotbugs-report checkstyle lint \
        test test-it verify verify-no-it \
        db-reset fixtures \
        ci

##@ General

help: ## Show this help (targets grouped by section)
	@awk ' \
		BEGIN { FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n" } \
		/^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2 } \
		/^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) }' \
		$(MAKEFILE_LIST)

##@ Docker stack

up: ## Start the docker stack (postgres + app)
	$(COMPOSE) up -d

down: ## Stop the stack, keep volumes
	$(COMPOSE) down

down-v: ## Stop the stack and DELETE the data volume
	$(COMPOSE) down -v

logs: ## Tail logs of the app
	$(COMPOSE) logs -f app

logs-db: ## Tail logs of postgres
	$(COMPOSE) logs -f postgres

ps: ## List the stack containers
	$(COMPOSE) ps

psql: ## Open psql inside the postgres container
	$(COMPOSE) exec postgres psql -U postgres -d productdb

##@ Build & run

build: ## Compile and package the app jar (mvnw clean package)
	$(MVN) clean package

run: ## Run the app from the host (Mode A; requires postgres up)
	$(MVN) spring-boot:run

##@ OpenAPI

openapi-generate: ## Regenerate ProductsApi from docs/api/openapi.yaml
	$(MVN) generate-sources

# The openapi-generator plugin validates the spec as part of generation.
# There is no standalone `validate` goal, so lint = generate. We use
# `generate-sources` (lifecycle phase) so all plugin executions run.
openapi-lint: openapi-generate ## Validate the OpenAPI spec (alias of openapi-generate)

##@ Static analysis

spotbugs: ## Run SpotBugs + FindSecBugs (fails the build on findings)
	$(MVN) -DskipTests compile spotbugs:check

spotbugs-report: ## Generate SpotBugs HTML report (target/spotbugs.html)
	$(MVN) -DskipTests compile spotbugs:spotbugs
	@echo "Open target/spotbugs.html in a browser."

checkstyle: ## Run Checkstyle (fails the build on findings)
	$(MVN) checkstyle:check

lint: spotbugs checkstyle ## Run all static-analysis tools

##@ Tests

test: ## Run unit tests (surefire, no Docker required)
	$(MVN) test

# Invokes failsafe goals directly so the lifecycle's `test` phase (surefire,
# unit tests) is skipped. `test-compile` ensures the *IT.java sources are
# compiled before failsafe looks for them.
test-it: ## Run integration tests only (failsafe; requires Docker)
	$(MVN) test-compile failsafe:integration-test failsafe:verify

verify: ## Compile + unit + integration (Docker) + lint — full pre-CI gate
	$(MVN) clean verify

verify-no-it: ## Compile + unit + lint, skipping integration tests (no Docker)
	$(MVN) clean verify -DskipITs

##@ Database

db-reset: ## Destroy and recreate the postgres volume (clean slate)
	$(COMPOSE) down -v
	$(COMPOSE) up -d postgres

# Loads docker/fixtures.sql via psql. The product table is created by Hibernate
# at app boot, so `make up` must have run at least once before this works.
fixtures: ## Reset products table and load 5 sample rows (requires `make up`)
	@$(COMPOSE) exec -T postgres psql -U postgres -d productdb -v ON_ERROR_STOP=1 < docker/fixtures.sql
	@echo "Loaded 5 fixtures. Try: curl -s http://localhost:8080/api/products"

##@ CI

ci: verify ## verify + build the docker image
	$(COMPOSE) build app
