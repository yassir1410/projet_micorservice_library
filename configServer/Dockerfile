FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/configServer-0.0.1-SNAPSHOT.jar /app/config-server.jar
RUN mkdir -p /config-repo
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "/app/config-server.jar"]
