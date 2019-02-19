package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class PullStockPriceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    final private String KAFKA_SEND_TO_TOPIC = "PullStockPriceEvent";

    PullStockPriceProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message) {
        this.kafkaTemplate.send(KAFKA_SEND_TO_TOPIC, message);
        System.out.println("Send message to Topic: " + KAFKA_SEND_TO_TOPIC);
    }
}
