package com.scraper.persistence.repository;

import com.scraper.persistence.model.Stock;
import org.springframework.data.repository.CrudRepository;

public interface StockRepository extends CrudRepository<Stock, String> {
    Stock findByIsin(String isin);
}
