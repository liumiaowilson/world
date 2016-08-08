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
import org.wilson.world.manager.ConfigManager;

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
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://feeds.abcnews.com/abcnews/topstories").get();
        Elements elements = doc.select("item");
        System.out.println(elements.size());
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            System.out.println(element.getElementsByTag("title").text());
            System.out.println(element.getElementsByTag("description").text());
            System.out.println(element.getElementsByTag("link").text());
        }
    }
    
    @Test
    public void testQuote() throws IOException {
        Document doc = Jsoup.connect("http://www.eduro.com/").get();
        Elements elements = doc.select("dailyquote");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            System.out.println(element.select("div p").get(0).text());
        }
    }
    
    @Test
    public void testLookupWord() throws IOException {
        Document doc = Jsoup.connect("http://www.merriam-webster.com/dictionary/word").get();
        Elements elements = doc.select("div.def-header-box");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            
            String part = element.select("span.main-attr").text();
            String pronunciation = element.select("span.pr").text();
            String definition = element.select("div.def-text p.definition-inner-item").text();
            
            System.out.println(part);
            System.out.println(pronunciation);
            System.out.println(definition);
        }
    }
    
    @Test
    public void testSocks() throws Exception {
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "8001");
        
        Connection con = HttpConnection.connect("http://10.8.128.222:5000/minios/execute");
        con.method(Method.GET).ignoreContentType(true);
        Response resp = con.execute();
        String body = resp.body();
        System.out.println(body);
    }
}
