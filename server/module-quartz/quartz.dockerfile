FROM gradle:jdk11 as build

WORKDIR /app

COPY --chown=gradle:gradle ./build.gradle settings.gradle gradlew ./
COPY --chown=gradle:gradle gradle/ ./gradle/
COPY --chown=gradle:gradle scripts/ ./scripts/


RUN chmod +x ./scripts/profile_check.sh \
    && ./scripts/profile_check.sh | tee ./profile_env.txt \
    && ls -al \
    && cat ./profile_env.txt \
    && ./gradlew --no-daemon dependencies \
    && apt-get clean && apt-get update && apt-get install -y --no-install-recommends net-tools


COPY --chown=gradle:gradle . .

RUN ./gradlew clean :module-quartz:build


FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/module-quartz/build/libs/*.jar app.jar
COPY --from=build /app/profile_env.txt /app/profile_env.txt

RUN apt-get update && \
    apt-get install -y --no-install-recommends busybox lsof procps && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN ls -al && cat /app/profile_env.txt  

ENTRYPOINT sh -c 'export PROFILE=$(cat /app/profile_env.txt) && java -jar -Dspring.profiles.active=$PROFILE app.jar'


