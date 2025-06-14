FROM gradle:jdk11 AS build

WORKDIR /app

COPY --chown=gradle:gradle build.gradle settings.gradle gradlew ./
COPY --chown=gradle:gradle gradle/ ./gradle/
COPY --chown=gradle:gradle deploy_script/ ./deploy_script/
COPY --chown=gradle:gradle . .

RUN ./gradlew clean :module-batch:build --parallel


FROM openjdk:11.0.16-jre-slim-buster

WORKDIR /app

COPY --from=build /app/module-batch/build/libs/module-batch-boot.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=batch", "app.jar"]