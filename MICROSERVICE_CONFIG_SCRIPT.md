# Microservice Configuration Script Template

Use this prompt template to configure any new microservice with Spring Cloud Config Server and Eureka integration.

---

## 🔧 Configuration Script for [MICROSERVICE_NAME]

**Replace `[MICROSERVICE_NAME]` with your microservice name (e.g., book-microservice, order-microservice, etc.)**

### Step 1: Update `application.yml`

Modify `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: [MICROSERVICE_NAME]
  cloud:
    config:
      uri: http://localhost:8888
  config:
    import: optional:configserver:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true

server:
  port: [PORT_NUMBER]
```

**Replace:**
- `[MICROSERVICE_NAME]` = your microservice name
- `[PORT_NUMBER]` = unique port (e.g., 8081 for user-microservice, 8082 for book-microservice, 8083 for order-microservice)

---

### Step 2: Update `pom.xml`

Add these dependencies in the `<dependencies>` section:

```xml
<!-- Spring Cloud Config Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>

<!-- Eureka Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok (optional) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

Ensure your `pom.xml` has the Spring Cloud BOM in `<dependencyManagement>`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Ensure parent version matches:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.0</version>
    <relativePath/>
</parent>
```

---

### Step 3: Add `@EnableEurekaClient` Annotation

Update your main application class (`[MICROSERVICE_NAME]Application.java`):

```java
package def.[microservice_name];

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class [MicroserviceName]Application {

    public static void main(String[] args) {
        SpringApplication.run([MicroserviceName]Application.class, args);
    }

}
```

---

### Step 4: Build and Test

Run these commands in your microservice directory:

```bash
# Clean previous builds
mvn clean

# Install dependencies
mvn install

# Run the application
mvn spring-boot:run
```

---

## 📋 Port Allocation Chart

| Microservice | Port | Status |
|---|---|---|
| Eureka Server | 8761 | Registry |
| Config Server | 8888 | Configuration |
| user-microservice | 8081 | Available ✅ |
| book-microservice | 8082 | Available |
| order-microservice | 8083 | Available |
| payment-microservice | 8084 | Available |

---

## ✅ Verification Checklist

- [ ] `application.yml` updated with correct microservice name
- [ ] `application.yml` has Config Server URI (`http://localhost:8888`)
- [ ] `pom.xml` has spring-cloud-config-client dependency
- [ ] `pom.xml` has spring-cloud-starter-netflix-eureka-client dependency
- [ ] Main application class has `@EnableDiscoveryClient` annotation
- [ ] Port number is unique and configured in `application.yml`
- [ ] Spring Cloud BOM version is `2023.0.3` (matches user-microservice)
- [ ] Spring Boot parent version is `3.3.0`
- [ ] Config Server is running on port 8888
- [ ] Eureka Server is running on port 8761

---

## 🚀 Quick Command to Apply This Configuration

**For book-microservice example:**

```bash
# Navigate to your microservice directory
cd C:\Users\DELL 7410\Downloads\tp-microservice\book-microservice

# Update application.yml with the template above (replace [MICROSERVICE_NAME] with book-microservice, port 8082)
# Update pom.xml with the dependencies above

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

---

## 📝 Configuration Server Repository Structure

Create these files in your Config Server Git repository:

**user-microservice.yml**
```yaml
spring:
  jpa:
    show-sql: true
server:
  port: 8081
```

**book-microservice.yml**
```yaml
spring:
  jpa:
    show-sql: true
server:
  port: 8082
```

**application.yml** (shared across all microservices)
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## 🆘 Troubleshooting

**Issue: Cannot connect to Config Server**
- Ensure Config Server is running on port 8888
- Check firewall settings
- Verify `spring.cloud.config.uri` in application.yml

**Issue: Cannot register with Eureka**
- Ensure Eureka Server is running on port 8761
- Verify `eureka.client.service-url.defaultZone` in application.yml
- Check that `@EnableDiscoveryClient` is added to main class

**Issue: Version conflicts**
- Run: `mvn dependency:tree`
- Ensure all microservices use same Spring Boot (3.3.0) and Cloud (2023.0.3) versions


