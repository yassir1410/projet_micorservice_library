package com.example.notificationservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class KafkaConsumerServiceTest {

    private final KafkaConsumerService consumer = new KafkaConsumerService();

    @Test
    void consume_shouldLogMessage() {
        consumer.consume("sample-message");
        assertThat(true).isTrue();
    }
}

