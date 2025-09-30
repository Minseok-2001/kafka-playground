# syntax=docker/dockerfile:1.7

FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN ./gradlew :waiting-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/waiting-service/build/libs/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
