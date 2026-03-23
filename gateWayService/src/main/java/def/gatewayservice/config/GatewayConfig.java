package def.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("book-service", r -> r
                        .path("/api/books/**")
                        .uri("lb://book-microservice"))
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://user-microservice"))

                .route("emprunt-service", r -> r
                        .path("/api/emprunts/**")
                        .uri("lb://emprunt-service"))
                .build();
    }
}

