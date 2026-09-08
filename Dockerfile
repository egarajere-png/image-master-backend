# ============================================================
# Stage 1 — build the jar with Maven
# ============================================================
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /build

# Copy the wrapper + pom first so dependency resolution is
# cached in its own Docker layer and isn't re-run on every
# source change.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Now copy the actual source and build.
COPY src src
RUN ./mvnw -B clean package -DskipTests

# ============================================================
# Stage 2 — slim runtime image
# ============================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Non-root user — no reason to run the JVM as root.
RUN useradd --create-home --shell /usr/sbin/nologin appuser
USER appuser

COPY --from=build --chown=appuser:appuser /build/target/workflow-0.0.1-SNAPSHOT.jar app.jar

# Uploaded images are written here by default (file.storage.location).
# Mount a volume at this path to persist them across container restarts.
VOLUME /app/uploads

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]