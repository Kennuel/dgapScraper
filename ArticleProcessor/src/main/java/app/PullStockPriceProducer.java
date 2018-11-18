package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class PullStockPriceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    final private String TOPIC = "PullStockPriceEvent";

    PullStockPriceProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message) {
        this.kafkaTemplate.send(TOPIC, message);
        System.out.print("Message send\n");
    }
}
