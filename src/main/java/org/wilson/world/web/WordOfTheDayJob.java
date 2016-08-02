package org.wilson.world.web;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;

public class WordOfTheDayJob extends SystemWebJob {
    public static final String WORD_OF_THE_DAY = "word_of_the_day";

    public WordOfTheDayJob() {
        this.setDescription("Get word of the day from merriam webster");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.merriam-webster.com/word-of-the-day");
        Elements elements = doc.select("div.word-header h1");
        String word = null;
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            word = element.text();
        }
        
        WebManager.getInstance().put(WORD_OF_THE_DAY, word);
    }

}
