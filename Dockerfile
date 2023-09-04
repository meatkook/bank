FROM openjdk:17-alpine
USER root
WORKDIR /app
COPY build/libs/*.jar .
COPY src/main/resources/application.yml /app/application.yml
CMD ["java", "-jar", "/app/clevertec-1.0-SNAPSHOT.jar"]