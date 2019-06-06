package app.repository;

import org.springframework.data.repository.CrudRepository;

import app.model.StockPrice;

public interface StockRepository extends CrudRepository<StockPrice, String> {
    
}
