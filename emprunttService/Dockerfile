FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/emprunttService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} empruntt-service.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/empruntt-service.jar"]

