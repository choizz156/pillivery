FROM gradle:jdk11 AS build

WORKDIR /app

COPY --chown=gradle:gradle build.gradle settings.gradle gradlew ./
COPY --chown=gradle:gradle gradle/ ./gradle/
COPY --chown=gradle:gradle deploy_script/ ./deploy_script/
COPY --chown=gradle:gradle . .

RUN ./gradlew clean :module-api:build --parallel


#FROM openjdk:11.0.16-jre-slim-buster
FROM openjdk:11.0.16-jre-slim-buster

WORKDIR /app

COPY --from=build /app/module-api/build/libs/module-api-boot.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]