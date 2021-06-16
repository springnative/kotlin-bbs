FROM java:8-alpine

COPY build/libs/kotlin-bbs-0.0.1.jar /app.jar

EXPOSE 8080

ENTRYPOINT java -jar /app.jar