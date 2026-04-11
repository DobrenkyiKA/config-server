# ── Build stage ──────────────────────────────────────
FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /workspace

# Copy Gradle wrapper and build files first (layer caching)
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies (cached unless build files change)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build
RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /workspace/build/libs/*.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8888

HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD wget -qO- http://localhost:8888/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]