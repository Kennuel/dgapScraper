package app;

import app.model.OpenFigiResponse;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class StockProcessorMicroservice implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(StockProcessorMicroservice.class, args);
    }

    //Todo: This can be removed only test purposes
    // I already found out how to map a symbol to a free API; it seems that DGAP.de only gives WKNs or ISIN
    // Either we need to find from isin/wkn to symbol or find another possibilty; Maybe also scraping this data from a website

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
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
        HttpEntity<String> response = restTemplate.postForEntity(uri, request, String.class);

        System.out.println(response.getBody());

//        final IEXTradingClient iexTradingClient = IEXTradingClient.create();
//        final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
//                .withSymbol(response.getBody().getTicker())
//                .build());
//        System.out.println(quote);
    }
}
