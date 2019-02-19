package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@EnableKafka
public class ProcessedArticleConsumer {

    private static final int MILLISECONDS_PER_HOUR = 1000 * 60 * 60;

    final private String KAFKA_LISTEN_TO_TOPIC = "PullStockPriceEvent";
    final private List<Integer> HOURS_FROM_NOW_TO_PULL = Arrays.asList(1, 2, 3, 6, 12, 24);

    @Autowired
    private ThreadPoolTaskScheduler messageScheduler;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;


    @KafkaListener(topics = KAFKA_LISTEN_TO_TOPIC)
    public void listenToKafka(String data) {
        System.out.println("Received Message for Topic: " + KAFKA_LISTEN_TO_TOPIC);
        HOURS_FROM_NOW_TO_PULL.forEach((hour) -> scheduleSendMessageInHours(hour));
    }

    private void scheduleSendMessageInHours(Integer hour) {
        SendMessageTask sendMessageTask = new SendMessageTask("Some Message");


        // Hack needed to Autowire PullStockPriceProducer inside of Message
        applySpringAutowiring(sendMessageTask);

        messageScheduler.schedule(
                sendMessageTask,
                new Date(System.currentTimeMillis() + (MILLISECONDS_PER_HOUR * hour))
        );
        System.out.println("Scheduled message in " + hour + " Hours");
    }

    private void applySpringAutowiring(SendMessageTask message) {
        beanFactory.autowireBean(message);
    }

}

