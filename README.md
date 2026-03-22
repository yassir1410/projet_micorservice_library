# Projet Microservices Bibliothèque

Architecture complète d'une application de gestion de bibliothèque basée sur les microservices avec Spring Cloud.

## 📋 Vue d'ensemble

Ce projet implémente une architecture de microservices pour une plateforme de gestion de bibliothèque utilisant :
- **Spring Boot** : framework pour chaque microservice
- **Spring Cloud** : orchestration, service discovery, configuration centralisée
- **Eureka** : service discovery
- **Spring Cloud Gateway** : API Gateway
- **Kafka** : système de messaging asynchrone
- **MySQL** : base de données
- **Docker Compose** : orchestration des conteneurs

## 🏗️ Architecture des microservices

### Services principaux

| Service | Port | Rôle |
|---------|------|------|
| **EurekaServer** | 8761 | Service discovery (service registry) |
| **configServer** | 8888 | Config Server centralisé (configuration externe) |
| **gateWayService** | 8080 | API Gateway (point d'entrée unique) |
| **book-microservice** | 8081 | Gestion des livres |
| **user-microservice** | 8082 | Gestion des utilisateurs |
| **emprunttService** | 8083 | Gestion des emprunts |
| **notification-service** | 8084 | Service de notifications |

### Communication inter-services

```
Client
  ↓
API Gateway (8080)
  ↓
┌─────────────────────────────────────┐
│     Eureka Service Discovery        │
│     (Service Registry - 8761)       │
└─────────────────────────────────────┘
  ↓
  ├─→ Book Microservice (8081)
  ├─→ User Microservice (8082)
  ├─→ Emprunt Service (8083)
  └─→ Notification Service (8084)
  
Config Server (8888) → Fournit les configurations à tous les services
Kafka → Communication asynchrone entre services
```

## 📁 Structure du projet

```
tp-microservice/
├── README.md
├── docker-compose.yml
├── EurekaServer/                    # Service discovery
│   ├── pom.xml
│   └── src/main/java/.../
├── configServer/                    # Config Server centralisé
│   ├── pom.xml
│   ├── src/main/java/.../
│   └── src/main/resources/
├── gateWayService/                  # API Gateway
│   ├── pom.xml
│   └── src/main/java/.../
├── book-microservice/               # Microservice livres
│   ├── pom.xml
│   └── src/main/java/.../
├── user-microservice/               # Microservice utilisateurs
│   ├── pom.xml
│   └── src/main/java/.../
├── emprunttService/                 # Microservice emprunts
│   ├── pom.xml
│   └── src/main/java/.../
└── notification-service/            # Microservice notifications
    ├── pom.xml
    └── src/main/java/.../
```

## ⚙️ Configuration

### Configuration externalisée

Les configurations sont gérées par un **Spring Cloud Config Server** qui charge les fichiers depuis un dépôt externe (`libraryConfig`).

**Fichiers de configuration :**
- `bootstrap.properties` : configuration du client Config Server (URL du serveur, nom de l'application)
- `application.properties` : configurations métier spécifiques (port, base de données, Kafka, etc.)

### Fichiers de configuration par service

Les fichiers sont stockés dans le dépôt `libraryConfig` :
- `book-microservice.properties`
- `user-microservice.properties`
- `emprunt-service.properties`
- `notification-service.properties`
- `EurekaServer.properties`
- `gateWayService.properties`

**Exemple de bootstrap.properties :**
```properties
spring.application.name=book-microservice
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.fail-fast=true
spring.cloud.config.retry.initial-interval=3000
spring.cloud.config.retry.max-attempts=10
```

## 🚀 Démarrage rapide

### Prérequis

- Java 11+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 5.7+
- Kafka 2.x+

### Étapes de lancement

#### 1. Cloner les dépôts
```bash
git clone <repo-main>          # Cloner le projet principal
git clone <libraryConfig-repo> # Cloner le dépôt de configuration
```

#### 2. Construire tous les microservices
```bash
cd tp-microservice
./mvnw clean install            # Construire tous les services
# ou pour chaque service :
cd EurekaServer && mvn clean package
cd ../configServer && mvn clean package
# etc.
```

#### 3. Lancer via Docker Compose
```bash
docker-compose up -d
```

Les conteneurs démarrés :
- `mysql-db` : base de données MySQL
- `zookeeper` : pour Kafka
- `kafka` : message broker
- `discovery-service` : EurekaServer
- `config-server` : Config Server
- `api-gateway` : Spring Cloud Gateway
- `book-service`, `user-service`, `emprunt-service`, `notification-service` : microservices

#### 4. Vérifier la santé des services
- Eureka Dashboard : http://localhost:8761
- Config Server : http://localhost:8888
- API Gateway : http://localhost:8080
- Services individuels : http://localhost:808X

## 📦 Dépendances principales

Toutes les dépendances communes (Spring Cloud, Eureka, Kafka, etc.) sont définies dans les fichiers `pom.xml` de chaque service.

**Dépendances clés dans tous les microservices :**
```xml
<!-- Spring Cloud Config Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- Spring Cloud Bootstrap -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>

<!-- Eureka Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

**Config Server spécifique :**
```xml
<!-- Eureka Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<!-- Config Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

## 🔒 Sécurité et meilleures pratiques

1. **Ne pas stocker de secrets en clair** : utiliser des variables d'environnement ou un système de gestion des secrets (Vault, etc.)
2. **Utiliser des profils** : dev, test, prod pour différencier les configurations
3. **Activer l'authentification** : sur Eureka, Config Server et API Gateway
4. **Chiffrer les données sensibles** : passwords, tokens API, etc.
5. **Monitoring** : activer les métriques Spring Boot Actuator pour le suivi des services

## 📊 Évolution et maintenance

### Ajouter un nouveau microservice
1. Créer un dossier avec le nom du service
2. Initialiser avec Spring Boot
3. Ajouter les dépendances Spring Cloud
4. Créer un fichier de configuration dans le dépôt `libraryConfig`
5. Enregistrer dans le `docker-compose.yml`

### Mettre à jour les configurations
1. Modifier le fichier `.properties` correspondant dans `libraryConfig`
2. Committer et pousser les changements
3. Les services chargeront la nouvelle configuration au prochain redémarrage (ou avec un endpoint de refresh)

## 🐛 Dépannage

### EurekaServer ne compile pas
**Erreur :** `package org.springframework.cloud.netflix.eureka.server does not exist`

**Solution :**
- Vérifier que la dépendance `spring-cloud-starter-netflix-eureka-server` est présente dans `pom.xml`
- Vérifier que `spring-cloud-starter-bootstrap` est ajouté
- Exécuter `mvn clean install` pour télécharger les dépendances

### Les fichiers de configuration ne sont pas chargés
- Vérifier que `bootstrap.properties` pointe vers le bon `spring.cloud.config.uri`
- Vérifier les logs du Config Server : `docker logs config-server`
- Vérifier que le dépôt `libraryConfig` est accessible

### Erreur d'encodage `MalformedInputException`
- S'assurer que les fichiers `.properties` sont encodés en UTF-8 sans BOM
- Dans l'éditeur, sauvegarder en UTF-8

## 📞 Support et documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)

---

**Auteur :** Projet TP - Microservices Bibliothèque  
**Date :** 20265

