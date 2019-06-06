package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import app.model.PullStockPriceRQ;
import app.model.StockPrice;
import app.repository.StockRepository;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

import app.model.OpenFigiResponse;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Component
@EnableKafka
public class PullStockPriceConsumer {

    final private String KAFKA_LISTEN_TO_TOPIC = "PULL_STOCK_PRICE_FOR_ISIN";
    

    private StockRepository stockRepository;

    @Autowired
    PullStockPriceConsumer(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
    

    @KafkaListener(topics = KAFKA_LISTEN_TO_TOPIC, groupId= "1",  containerFactory = "kafkaListenerContainerFactory")
    public void listenToKafka(PullStockPriceRQ data) {

        final String uri = "https://api.openfigi.com/v2/mapping";
        

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    

        String requestBody = "[{\n" +
                "\t\"idType\": \"ID_ISIN\",\n" +
                "\t\"idValue\": \""+ data.getIsin()+"\"\n"+
                "}]";

        HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            System.out.println("Start PullingStockPrice!");
            HttpEntity<OpenFigiResponse[]> responseList = restTemplate.postForEntity(uri, request,
                    OpenFigiResponse[].class);
            String ticker = responseList.getBody()[0].getData()[0].getTicker();
            System.out.println("Symbol: " + ticker);
            final IEXTradingClient iexTradingClient = IEXTradingClient.create();
            final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
                    .withSymbol(ticker)
                    .build());

            System.out.println(quote);
            StockPrice myStock =  new StockPrice();
            myStock.setId(data.getArticleId());
            myStock.setAmount(quote.getLatestPrice());
            myStock.setDate(new Date());
            stockRepository.save(myStock);

        } catch (Exception e) {
            System.out.println("********************************************************");
            System.out.println("ISIN: " + data.getIsin());
            System.out.println("Could not find an item for the requested ISIN");
            System.out.println("Exception: " + e);
            System.out.println("********************************************************");
        }
    }



}

