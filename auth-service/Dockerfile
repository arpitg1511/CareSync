# 🏗️ Stage 1: Build Phase
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 🚀 Stage 2: Runtime Phase
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# ⚡ Use wildcard to grab ANY jar built in the target folder
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
