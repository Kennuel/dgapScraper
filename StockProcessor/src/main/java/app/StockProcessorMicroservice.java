package app;

import app.model.OpenFigiResponse;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

@SpringBootApplication
public class StockProcessorMicroservice implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(StockProcessorMicroservice.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) {
        final String uri = "https://api.openfigi.com/v1/mapping";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String requestBody = "[{\n" +
                "\t\"idType\": \"ID_ISIN\",\n" +
                "\t\"idValue\": \"US0378331005\",\n" +
                "\t\"exchCode\":\"US\"\n" +
                "}]";

        HttpEntity<String> request = new HttpEntity<>(requestBody, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpEntity<OpenFigiResponse[]> responseList = restTemplate.postForEntity(uri, request,
                    OpenFigiResponse[].class);
            String ticker = responseList.getBody()[0].getData()[0].getTicker();

            final IEXTradingClient iexTradingClient = IEXTradingClient.create();
            final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
                    .withSymbol(ticker)
                    .build());
            System.out.println(quote);
        } catch (Exception e) {
            System.out.println("Could not find an item for the requested ISIN");
        }

    }
}
