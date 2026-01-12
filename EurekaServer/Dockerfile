FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/EurekaServer-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} eureka-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "/eureka-server.jar"]

