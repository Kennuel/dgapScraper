package app.scraper;

import java.io.FileNotFoundException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class SingleScraper {

    // TODO align with change notifier
    final private String TOPIC = "PullStockPriceEvent";

    @KafkaListener(topics = TOPIC)
    public void processMessage() {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("JavaScript");
      try {
        // TODO pathing is not correct, wanted to debug to clarify root :P
        engine.eval(new java.io.FileReader("../../js/singleScraper.js"));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (ScriptException e) {
        e.printStackTrace();
      }
    }

}
