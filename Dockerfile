FROM openjdk:17-alpine
USER root
WORKDIR /app
COPY build/libs/*.jar .
COPY src/main/resources/for_docker/application.yml /app/src/main/resources/application.yml
COPY src/main/resources/assets/fonts/couriercyrps.ttf /app/src/main/resources/assets/fonts/couriercyrps.ttf
CMD ["java", "-jar", "/app/clevertec-1.0-SNAPSHOT.jar"]