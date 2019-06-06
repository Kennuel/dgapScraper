package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import app.model.PullStockPriceRQ;

@Component
@EnableKafka
public class PullStockPriceProducer {

    private final KafkaTemplate<String, PullStockPriceRQ> kafkaTemplate;

    final private String KAFKA_SEND_TO_TOPIC = "PULL_STOCK_PRICE_FOR_ISIN";

    PullStockPriceProducer(KafkaTemplate<String, PullStockPriceRQ> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PullStockPriceRQ pullStockPriceRQ) {
        this.kafkaTemplate.send(KAFKA_SEND_TO_TOPIC, pullStockPriceRQ);
        System.out.println("Send message to Topic: " + KAFKA_SEND_TO_TOPIC);
    }
}
