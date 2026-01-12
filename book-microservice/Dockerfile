FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/book-microservice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} book-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/book-service.jar"]

