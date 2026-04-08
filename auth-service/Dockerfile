# 🏗️ Stage 1: Build the JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copy the pom.xml and source code
COPY . .
# Build the JAR file
RUN mvn clean package -DskipTests

# 🚀 Stage 2: Create the final lightweight image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /app/target/caresync-auth-service-0.0.1-SNAPSHOT.jar app.jar
# Expose the service port
EXPOSE 8081
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
