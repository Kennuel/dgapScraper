package app;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import app.model.Article;
import app.repository.ArticleRepository;

@Component
@EnableKafka
public class NewArticleConsumer {

    private static final int MILLISECONDS_PER_HOUR = 1000 * 60 * 60;

    final private String KAFKA_LISTEN_TO_TOPIC = "NEW_ARTICLE_AT_SOURCE";
    final private List<Integer> HOURS_FROM_NOW_TO_PULL = Arrays.asList(0, 1, 2, 3, 6, 12, 24);

    @Autowired
    private ThreadPoolTaskScheduler messageScheduler;

    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @KafkaListener(topics = KAFKA_LISTEN_TO_TOPIC, groupId = "2", containerFactory = "kafkaListenerContainerFactory")
    public void listenToKafka(Article data) {
        System.out.println("********************************************************");
        System.out.println("Received Message for Topic: " + KAFKA_LISTEN_TO_TOPIC);
        HOURS_FROM_NOW_TO_PULL.forEach((hour) -> scheduleSendMessageInHours(hour, data.getIsin(), data.getDate()));
        this.articleRepo.save(data);
        System.out.println("********************************************************");
    }

    private void scheduleSendMessageInHours(Integer hour, String isin, Date articleId) {
        SendMessageTask sendMessageTask = new SendMessageTask(isin, articleId);

        // Hack needed to Autowire PullStockPriceProducer inside of Message
        beanFactory.autowireBean(sendMessageTask);
        
        Date scheduledAt = new Date(System.currentTimeMillis() + (MILLISECONDS_PER_HOUR * hour));
        messageScheduler.schedule(
                sendMessageTask,
                scheduledAt
        );

        System.out.println("Scheduled message in " + hour + " hours: " + scheduledAt.toString());
    }
}

