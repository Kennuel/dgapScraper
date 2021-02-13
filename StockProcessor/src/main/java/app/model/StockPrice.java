package app.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table
public class StockPrice {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    Date articleDate;

    Date date;

    BigDecimal amount;

    public StockPrice() {}
    
    public Date getArticleDate() {
        return articleDate;
    }

    public void setArticleDate(Date articleDate) {
        this.articleDate = articleDate;
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