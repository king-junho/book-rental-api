FROM eclipse-temurin:21-jdk AS builder

WORKDIR /workspace

COPY . .

RUN sh ./gradlew --no-daemon test bootJar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /workspace/build/libs/app.jar /app/app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]