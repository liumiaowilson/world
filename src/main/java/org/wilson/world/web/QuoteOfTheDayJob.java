package org.wilson.world.web;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;

public class QuoteOfTheDayJob extends SystemWebJob {
    public static final String QUOTE_OF_THE_DAY = "quote_of_the_day";

    public QuoteOfTheDayJob() {
        this.setDescription("Get quote of the day");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.eduro.com");
        Elements elements = doc.select("dailyquote");
        String quote = null;
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            quote = element.select("div p").get(0).text();
        }
        
        WebManager.getInstance().put(QUOTE_OF_THE_DAY, quote);
    }

}
