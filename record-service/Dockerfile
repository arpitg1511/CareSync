FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/caresync-record-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]
