# syntax=docker/dockerfile:1.7

# Note on base image variant: we use the non-Alpine images.
# eclipse-temurin:17-*-alpine has no linux/arm64 manifest, so on Apple Silicon
# `docker compose build` fails with "no match for platform in manifest". The
# Ubuntu-based images publish multi-arch (amd64 + arm64) and work everywhere.
# Trade-off: larger image (~440 MB vs ~210 MB).

# ---------- Build stage ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

# Resolve dependencies in a cacheable layer.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Copy sources and the OpenAPI spec (input to the generator plugin).
COPY src/ src/
COPY docs/api/ docs/api/

RUN ./mvnw -B -q -DskipTests package


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Run as a non-root user (Ubuntu uses adduser/addgroup with different flags than Alpine).
RUN groupadd --system app && useradd --system --gid app --shell /usr/sbin/nologin app
USER app

COPY --from=build --chown=app:app /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
