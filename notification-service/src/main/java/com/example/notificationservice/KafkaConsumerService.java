package com.example.notificationservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    // Écoute le topic imposé : emprunt-created
    @KafkaListener(topics = "emprunt-created", groupId = "notification-group")
    public void consume(String message) {
        System.out.println("------------------------------------------");
        System.out.println("NOTIFICATION REÇUE (Simulation) :");
        System.out.println("Détails de l'emprunt : " + message);
        System.out.println("------------------------------------------");
    }
}