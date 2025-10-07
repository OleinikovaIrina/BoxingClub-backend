# syntax = docker/dockerfile:1.5

# ---------- build stage ----------
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace/app

# 1) Кэшируем зависимости
COPY gradlew settings.gradle build.gradle ./
COPY gradle/wrapper gradle/wrapper
RUN chmod +x gradlew

# Pre-fetch dependencies (cached)
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/workspace/app/.gradle \
    ./gradlew --no-daemon dependencies

# 2) Копируем исходники и собираем fat-jar
COPY src ./src
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/workspace/app/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

# ---------- run stage ----------
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Копируем собранный jar
COPY --from=builder /workspace/app/build/libs/*.jar /app/app.jar




EXPOSE 8081

# активируем профиль dev —  на prod при необходимости !!!
ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java","-jar","/app/app.jar"]
