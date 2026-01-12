# Utilisation de l'image JDK 21 comme pour vos autres services
FROM eclipse-temurin:21-jdk

# L'argument doit correspondre au nom généré par le pom.xml
ARG JAR_FILE=target/notification-service-0.0.1-SNAPSHOT.jar

# Copie du fichier JAR dans l'image
COPY ${JAR_FILE} notification-service.jar

# NOTE : Le projet MSA précise "Aucun appel REST entrant".
# Par conséquent, EXPOSE n'est techniquement pas nécessaire ici. 
# Si vous aviez un port, ce serait par exemple : EXPOSE 8084

# Lancement du microservice
ENTRYPOINT ["java", "-jar", "/notification-service.jar"]