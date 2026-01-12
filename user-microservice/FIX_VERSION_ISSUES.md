# Microservice Version Fix Prompt Script

Use this prompt with GitHub Copilot or any AI assistant to fix version-related issues in Spring Boot microservices projects:

---

## PROMPT TO USE:

```
I have a Spring Boot microservice project with the following issues:

1. Spring Cloud and Spring Boot version compatibility problems
2. Missing or incorrect dependency versions in pom.xml
3. Eureka client integration not working due to version conflicts
4. Need to convert application.properties to application.yml with Eureka configuration

Please fix the following:

### Required Changes:

1. **Fix pom.xml dependencies:**
   - Spring Boot version: 3.3.0 (compatible with Spring Cloud)
   - Spring Cloud version: 2023.0.3 (compatible with Spring Boot 3.3.0)
   - Remove any dependencies with missing version specifications
   - Add spring-cloud-starter-netflix-eureka-client dependency
   - Add proper dependencyManagement for Spring Cloud

2. **Create/Update application.yml with:**
   - Application name: [YOUR_MICROSERVICE_NAME]
   - Eureka server URL: http://localhost:8761/eureka/
   - Database configuration (H2/MySQL/PostgreSQL as needed)
   - Server port configuration (e.g., 8081, 8082, etc.)
   - JPA/Hibernate settings

3. **Add @EnableDiscoveryClient annotation to the main application class**

4. **Remove invalid dependencies like spring-boot-h2console that have missing versions**

5. **Ensure compatibility matrix:**
   - Spring Boot: 3.3.0
   - Spring Cloud: 2023.0.3
   - Java version: 21
   - JPA/Hibernate for database access
   - Netflix Eureka for service discovery

### Current Project Structure:
- Project Type: Spring Boot Microservice
- Java Version: 21
- Framework: Spring Cloud with Eureka
- Database: H2 (in-memory)

Please apply these fixes and ensure the project compiles without errors.
```

---

## Alternative - Detailed Version Compatibility Matrix:

If you need to support different versions, use this compatibility reference:

```
COMPATIBLE VERSION COMBINATIONS:

✅ Recommended (Stable):
- Spring Boot: 3.3.0
- Spring Cloud: 2023.0.3
- Java: 21
- Status: Stable & Production-Ready

✅ Alternative (Latest):
- Spring Boot: 3.2.x
- Spring Cloud: 2022.0.x
- Java: 21
- Status: Stable

❌ Avoid:
- Spring Boot: 4.0.0 + Spring Cloud: 2024.0.0 (Compatibility issues)
- Spring Boot: 4.0.0 + Spring Cloud: 2023.x (Bean generation errors)
```

---

## Dependencies to Include:

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

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## application.yml Template:

```yaml
spring:
  application:
    name: your-microservice-name
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
  port: 8081
```

---

## Main Application Class Template:

```java
package your.package.name;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class YourMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YourMicroserviceApplication.class, args);
    }
}
```

---

## Common Issues Fixed:

| Issue | Solution |
|-------|----------|
| Spring Boot 4.0.0 incompatibility | Downgrade to Spring Boot 3.3.0 |
| Spring Cloud version mismatch | Use Spring Cloud 2023.0.3 |
| Missing bean name generation | Update Spring Boot/Cloud versions |
| Eureka client not importing | Add spring-cloud-starter-netflix-eureka-client |
| Invalid dependency versions | Remove spring-boot-h2console, use h2 directly |
| Properties not loading | Convert to application.yml format |

---

## Quick Fix Checklist:

- [ ] pom.xml has Spring Boot 3.3.0
- [ ] pom.xml has Spring Cloud 2023.0.3
- [ ] dependencyManagement section present
- [ ] spring-cloud-starter-netflix-eureka-client added
- [ ] Invalid dependencies removed
- [ ] application.yml created with Eureka config
- [ ] @EnableDiscoveryClient annotation present
- [ ] No compilation errors
- [ ] Project compiles with `mvn clean install`

---

## Build Command:

```powershell
cd "C:\path\to\project"
.\mvnw.cmd clean install -DskipTests
```

or with Maven installed:

```powershell
mvn clean install -DskipTests
```

