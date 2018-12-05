package com.scraper.persistence.controller;

import com.scraper.persistence.model.Stock;
import com.scraper.persistence.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/stock")
public class StockController {
    private StockRepository stockRepository;

    @Autowired
    StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @RequestMapping(value = "/findByIsin", method = RequestMethod.GET)
    public Stock findByIsin(@RequestParam("isin") String isin) {
        Stock stock = this.stockRepository.findByIsin(isin);
        if (Objects.isNull(stock)) {
            return null;
        } else {
            return stock;
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
    public void save(@RequestBody Stock stock) {
        if (Objects.isNull(stock.getIsin()) && Objects.isNull(stock.getSymbol())) {
            System.out.println("Not able to save stock - isin: " + stock.getIsin() + ", symbol: " + stock.getSymbol());
        } else {
            stockRepository.save(stock);
        }
    }
}
