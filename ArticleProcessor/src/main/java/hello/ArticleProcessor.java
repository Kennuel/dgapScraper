package hello;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class ArticleProcessor {

    private final KafkaTemplate<String, String> kafkaTemplate;


    private String topic = "PullStockPriceEvent";

    ArticleProcessor(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message) {
        this.kafkaTemplate.send(topic, message);
        System.out.println("Sent sample message [" + message + "] to " + topic);
    }


    @KafkaListener(topics = "ProcessedArticleEvent")
    public void processMessage(String payload) {
        System.out.printf("Message Received: " +  payload);
    }



}

