# 🏗️ Build Stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 🚀 Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/caresync-auth-service-0.0.1-SNAPSHOT.jar auth-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
