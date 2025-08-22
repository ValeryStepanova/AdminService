FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/AdminService-0.0.1-SNAPSHOT.jar admin-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "admin-service.jar"]
