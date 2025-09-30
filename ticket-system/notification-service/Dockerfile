# syntax=docker/dockerfile:1.7

FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :notification-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/notification-service/build/libs/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
