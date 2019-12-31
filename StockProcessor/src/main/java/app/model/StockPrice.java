package app.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class StockPrice {
    
    @Id
    Date id;
    
    @Id
    Date date;

    BigDecimal amount;

    public StockPrice() {}
    
    public Date getId() {
        return id;
    }

    public void setId(Date id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}