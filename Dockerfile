# ---------------------------------------------
# Stage 1: Build the Application
# ---------------------------------------------
FROM maven:3.9.6-amazoncorretto-21 AS builder
WORKDIR /build

# Cache Maven dependencies before copying source
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---------------------------------------------
# Stage 2: Minimal Production Runtime
# ---------------------------------------------
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

# Run as non-root user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]