package app;

import org.springframework.beans.factory.annotation.Autowired;

public class SendMessageTask implements Runnable {

    @Autowired
    private PullStockPriceProducer pullStockPriceProducer;

    private String message;

    public SendMessageTask(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        pullStockPriceProducer.send(this.message);
    }
}
