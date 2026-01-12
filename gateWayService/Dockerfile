FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/gateWayService-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} gateway-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/gateway-service.jar"]

