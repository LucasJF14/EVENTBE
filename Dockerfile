FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/event-docker.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]