package app;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import app.model.PullStockPriceRQ;
import app.model.StockPrice;
import app.repository.StockRepository;

import app.model.StockScraperRS;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Component
@EnableKafka
public class PullStockPriceConsumer {

    final private String KAFKA_LISTEN_TO_TOPIC = "PULL_STOCK_PRICE_FOR_ISIN";
    
    @Autowired
    private StockRepository stockRepository;

    @KafkaListener(topics = KAFKA_LISTEN_TO_TOPIC, groupId= "1",  containerFactory = "kafkaListenerContainerFactory")
    public void listenToKafka(PullStockPriceRQ data) {

        final String uri = "http://stockscraper:3000/?isin=" + data.getIsin();
        

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
    
        RestTemplate restTemplate = new RestTemplate();
        try {
            System.out.println("Start PullingStockPrice!");
            HttpEntity<StockScraperRS> response = restTemplate.getForEntity(uri, StockScraperRS.class);
        
            System.out.println("PulledPrice PullingStockPrice! " + response.getBody().getData());
            StockPrice myStock =  new StockPrice();
            myStock.setArticleDate(data.getArticleId());
            myStock.setAmount(new BigDecimal(response.getBody().getData()));
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

