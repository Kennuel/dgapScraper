package app;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import app.model.PullStockPriceRQ;

public class SendMessageTask implements Runnable {

    @Autowired
    private PullStockPriceProducer pullStockPriceProducer;

    private String isin;
    private Date articleId;

    public SendMessageTask(String isin, Date articleId) {
        this.isin = isin;
        this.articleId = articleId;
    }

    @Override
    public void run() {
        pullStockPriceProducer.send(new PullStockPriceRQ(this.isin, this.articleId));
    }
}
