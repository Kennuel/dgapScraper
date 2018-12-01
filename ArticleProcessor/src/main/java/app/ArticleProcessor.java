package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class ArticleProcessor {

    // TODO revert to old value just changed for easy testing
    final private String TOPIC = "PullStockPriceEvent1";

    @KafkaListener(topics = TOPIC)
    public void processMessage(String data) {
        System.out.print(data);
    }

}

