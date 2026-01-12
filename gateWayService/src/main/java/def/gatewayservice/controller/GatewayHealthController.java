package def.gatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayHealthController {

    @GetMapping("/")
    public Mono<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Gateway is running");
        response.put("message", "Use /api/books or /api/users endpoints");
        return Mono.just(response);
    }

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "API Gateway");
        return Mono.just(response);
    }
}

