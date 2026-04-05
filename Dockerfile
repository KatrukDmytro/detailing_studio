# ============================================
# Stage 1: Build the application with Gradle
# ============================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper and config first (for layer caching)
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY settings.gradle gradle.properties ./

# Make gradlew executable
RUN chmod +x gradlew

# Copy build files
COPY app/build.gradle app/build.gradle

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY app/src/ app/src/

# Build the fat JAR (skip tests for faster builds)
RUN ./gradlew :app:bootJar --no-daemon -x test

# ============================================
# Stage 2: Run with minimal JRE
# ============================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR from builder stage
COPY --from=builder /app/app/build/libs/*.jar app.jar

# Create data directory for H2 (if used)
RUN mkdir -p /app/data && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/services || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
