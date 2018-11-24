package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@Component
//@EnableKafka
public class PullStockPriceConsumer {

//    final private String KAFKA_LISTEN_TO_TOPIC = "PullStockPriceEvent";

//    @KafkaListener(topics = KAFKA_LISTEN_TO_TOPIC)
    public void listenToKafka(String data) {
        final IEXTradingClient iexTradingClient = IEXTradingClient.create();
        final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
                .withSymbol("AAPL")
                .build());
        System.out.println(quote);
    }



}

