# ---------- BUILD STAGE ----------
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better layer caching)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy source code and build
COPY src src
RUN ./mvnw clean package -DskipTests -B

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]