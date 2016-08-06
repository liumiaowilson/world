package org.wilson.world.util;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import net.sf.json.JSONObject;

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

    @Test
    public void testJSON() throws Exception {
        Connection con = HttpConnection.connect("https://www.randomlists.com/data/words.json");
        con.method(Method.GET).ignoreContentType(true);
        Response resp = con.execute();
        String body = resp.body();
        JSONObject obj = JSONObject.fromObject(body);
        System.out.println(obj);
    }
    
    @Test
    public void testImage() throws IOException {
        Document doc = Jsoup.connect("http://www.freeimages.com/new").get();
        Elements elements = doc.select("div.listing-main ul li img");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            System.out.println(element.attr("rel"));
            System.out.println(element.parent().parent().attr("title"));
        }
    }
    
    @Test
    public void testRSS() throws IOException {
        Document doc = Jsoup.connect("http://feeds.abcnews.com/abcnews/topstories").get();
    }
}
