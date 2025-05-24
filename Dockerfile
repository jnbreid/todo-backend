# build application
FROM gradle:8.4-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .

RUN gradle build -x test --no-daemon

# run
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]