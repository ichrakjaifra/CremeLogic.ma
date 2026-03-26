# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl bash tzdata \
    && cp /usr/share/zoneinfo/Africa/Casablanca /etc/localtime \
    && echo "Africa/Casablanca" > /etc/timezone

# Create directories for uploads and logs
RUN mkdir -p /app/uploads /app/logs \
    && chmod 755 /app/uploads /app/logs

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
    && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]