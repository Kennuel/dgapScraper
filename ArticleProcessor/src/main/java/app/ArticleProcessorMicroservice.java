package app;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArticleProcessorMicroservice {

    public static void main(String[] args) {
        SpringApplication.run(ArticleProcessorMicroservice.class, args);
    }

    //Todo: This can be removed only test purposes
    @Bean
    public ApplicationRunner runner(PullStockPriceProducer producer) {
        return (args) -> {
            for(int i = 0; i < 2; i++) {
                System.out.println("Sended a testmessage!");
                producer.send("A simple test message\n");
                Thread.sleep(2000);
            }
        };
    }
}
