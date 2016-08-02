package org.wilson.world.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class JSoupTest {

    @Test
    public void test() throws IOException {
        Document doc = Jsoup.connect("http://www.merriam-webster.com/word-of-the-day").get();
        Elements elements = doc.select("div.word-header h1");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            System.out.println(element.text());
        }
    }

}
