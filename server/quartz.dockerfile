FROM gradle:jdk11 as build

WORKDIR /app

COPY --chown=gradle:gradle build.gradle settings.gradle gradlew ./
COPY --chown=gradle:gradle gradle/ ./gradle/

RUN ./gradlew --no-daemon dependencies
RUN apt-get update && apt-get install -y net-tools

COPY --chown=gradle:gradle . .

RUN ./gradlew clean :module-quartz:build


FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/module-quartz/build/libs/*.jar app.jar

#ENTRYPOINT ["java", "-Dspring.profiles.active=real", "-jar", "app.jar", "1>logs.txt","2>&1"]
