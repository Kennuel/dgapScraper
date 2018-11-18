package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class ArticleProcessor {

    final private String TOPIC = "PullStockPriceEvent";

    @KafkaListener(topics = TOPIC)
    public void processMessage(String data) {
        System.out.print(data);
    }

}

