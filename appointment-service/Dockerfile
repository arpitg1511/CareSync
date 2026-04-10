# --- Stage 1: Build Stage ---
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy the pom and download dependencies (cached)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Run Stage ---
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/caresync-appointment-service-*.jar app.jar

# Expose the service port (from application.properties)
EXPOSE 8084

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
