FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/caresync-payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
