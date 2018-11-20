package app;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@SpringBootApplication
public class StockProcessorMicroservice {


    public static void main(String[] args) {
        SpringApplication.run(StockProcessorMicroservice.class, args);
    }



    //Todo: This can be removed only test purposes
    // I already found out how to map a symbol to a free API; it seems that DGAP.de only gives WKNs or ISIN
    // Either we need to find from isin/wkn to symbol or find another possibilty; Maybe also scraping this data from a website

    @Bean
    public ApplicationRunner runner() {

        final IEXTradingClient iexTradingClient = IEXTradingClient.create();
        final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
                .withSymbol("AAPL")
                .build());
        System.out.println(quote);
        return null;
    }
}
