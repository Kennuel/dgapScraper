package app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockScraperRS {
    @JsonProperty("price")
    private Double price;

    public Double getData() {
        return price;
    }

    public void setData(Double data) {
        this.price = data;
    }
}
