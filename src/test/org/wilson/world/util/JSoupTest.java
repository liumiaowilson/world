package org.wilson.world.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.wilson.world.clip.GexoDecoder;
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
        Connection con = HttpConnection.connect("http://www.easydefine.com/modules/test.inc.php");
        con.method(Method.POST).ignoreContentType(true);
        con.data("par1", "look-up");
        Response resp = con.execute();
        String body = resp.body();
        System.out.println(body);
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
    
    @Test
    public void testLiterotica() throws Exception {
        Document doc = Jsoup.connect("https://www.literotica.com/c/non-consent-stories").get();
        Elements elements = doc.select("div.b-story-list-box");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            
            Elements articles = element.select("div.b-story-list-box-body h3");
            for(int i = 0; i < articles.size(); i++) {
                Element article = articles.get(i);
                String title = article.text();
                String url = article.select("a").attr("href");
                
                System.out.println(title);
                System.out.println(url);
            }
        }
    }
    
    @Test
    public void testLiteroticaGet() throws Exception {
        Document doc = Jsoup.connect("https://www.literotica.com/s/possessions-ch-08").get();
        Elements pages = doc.select("div.b-pager-pages span.b-pager-caption-t");
        String pagesStr = pages.text();
        String [] pageItems = pagesStr.split(" ");
        if(pageItems.length > 0) {
            System.out.println(pageItems[0]);
        }
        
        StringBuffer sb = new StringBuffer();
        String content = doc.select("div.b-story-body-x").html();
        sb.append(content);
        
        int num = Integer.parseInt(pageItems[0]);
        if(num > 1) {
            for(int i = 2; i <= num; i++) {
                doc = Jsoup.connect("https://www.literotica.com/s/possessions-ch-08?page=" + i).get();
                content = doc.select("div.b-story-body-x").html();
                sb.append(content);
            }
        }
        
        System.out.println(sb.toString());
    }
    
    @Test
    public void testPornPic() throws Exception {
        Document doc = Jsoup.connect("http://www.pornpics.com/recent/hardcore/").get();
        Elements elements = doc.select("div#main li.thumbwook");
        for(int i = 0; i < elements.size(); i++) {
            Element li = elements.get(i);
            Elements item = li.select("a.rel-link");
            String title = item.attr("title");
            String url = item.attr("href");
            
            System.out.println(title);
            System.out.println(url);
        }
    }
    
    @Test
    public void testPornPicFetch() throws Exception {
        Document doc = Jsoup.connect("http://www.pornpics.com/galleries/european-teen-sheri-vi-taking-hardcore-penetration-of-shaved-cunt-and-anus/").get();
        Elements elements = doc.select("div#main li.thumbwook");
        for(int i = 0; i < elements.size(); i++) {
            Element li = elements.get(i);
            Elements item = li.select("a.rel-link");
            String url = item.attr("href");
            
            System.out.println(url);
        }
    }
    
    @Test
    public void testBeauty() throws Exception {
        Document doc = Jsoup.connect("http://www.mm131.com/xinggan/").get();
        Elements elements = doc.select("div.main dl.list-left dd");
        for(int i = 0; i < elements.size(); i++) {
            Elements item = elements.get(i).select("a");
            if(item.size() == 1) {
                String url = item.attr("href");
                
                System.out.println(url);
            }
        }
    }
    
    @Test
    public void testBeautyEach() throws Exception {
        Document doc = Jsoup.connect("http://www.mm131.com/xinggan/2606.html").get();
        Elements elements = doc.select("div.content div.content-page span.page-ch");
        String text = elements.get(0).text();
        
        Pattern p = Pattern.compile("([0-9]+)");
        Matcher m = p.matcher(text);
        if(m.find()) {
            int pages = Integer.parseInt(m.group(1));
            for(int i = 1; i <= pages; i++) {
                System.out.println("http://img1.mm131.com/pic/2606/" + i + ".jpg");
            }
        }
    }
    
    @Test
    public void testHentai() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://g.e-hentai.org/").get();
        Elements elements = doc.select("div.ido table.itg div.it5 a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String title = element.text();
            
            System.out.println(title);
            System.out.println(url);
        }
    }
    
    @Test
    public void testHentaiEach() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://g.e-hentai.org/g/963653/75852cb933/").get();
        Elements elements = doc.select("div.gtb p");
        String text = elements.text();
        
        Pattern p = Pattern.compile("of ([0-9]+) image");
        Matcher m = p.matcher(text);
        if(m.find()) {
            int images = Integer.parseInt(m.group(1));
            System.out.println(images);
        }
        
        elements = doc.select("div.gdtm a");
        for(int i = 0; i < elements.size(); i++) {
            String url = elements.get(i).attr("href");
            
            System.out.println(url);
        }
    }
    
    @Test
    public void testHentaiGet() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://g.e-hentai.org/s/29ff204432/963653-1").get();
        Elements elements = doc.select("div.sni>a img");
        String url = elements.attr("src");
        System.out.println(url);
    }
    
    @Test
    public void testClip() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.cliphunter.com/categories/All").get();
        Elements elements = doc.select("div.mainContainer div.responsiveCont div.innerPane ul.moviethumbs li a.t");
        for(int i = 0; i < elements.size(); i++) {
            String url = "http://www.cliphunter.com" + elements.get(i).attr("href");
            System.out.println(url);
        }
    }
    
    @Test
    public void testClipEach() throws Exception {
        ConfigManager.getInstance();
        Connection con = HttpConnection.connect("http://www.cliphunter.com/w/2668818/Bursty_minx_is_nailed_hard");
        con.method(Method.GET).ignoreContentType(true);
        Response resp = con.execute();
        String body = resp.body();
        String start = "var gexoFiles = ";
        String end = "var flvQ = ";
        int start_pos = body.indexOf(start);
        int end_pos = body.indexOf(end);
        String json = body.substring(start_pos + start.length(), end_pos);
        json = json.trim().substring(0, json.length() - 2);
        
        System.out.println(json);
        JSONObject obj = JSONObject.fromObject(json);
        String url = null;
        for(Object key : obj.keySet()) {
            if(key instanceof String) {
                if(((String)key).contains("_p360.mp4")) {
                    JSONObject child = obj.getJSONObject((String) key);
                    String input = child.getString("url");
                    url = GexoDecoder.decode(input);
                    break;
                }
            }
        }
        
        System.out.println(url);
    }
    
    @Test
    public void testJapanPorn() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.unmosaic.net/").get();
        Elements elements = doc.select("ul.indexnaviul li a");
        for(int i = 0; i < elements.size(); i++) {
            String url = "http://www.unmosaic.net/" + elements.get(i).attr("href");
            System.out.println(url);
        }
    }
    
    @Test
    public void testJapanPornIndex() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.unmosaic.net/Dticn/H02300/index.html").get();
        Elements elements = doc.select("ol.galleryol li a");
        for(int i = 0; i < elements.size(); i++) {
            String url = "http://www.unmosaic.net/Dticn/H02300/" + elements.get(i).attr("href");
            System.out.println(url);
        }
    }
    
    @Test
    public void testJapanPornEach() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.unmosaic.net/Dticn/Ippondo2/7893/index.html").get();
        Elements elements = doc.select("div#picturesbox img");
        for(int i = 0; i < elements.size(); i++) {
            String url = "http://www.unmosaic.net/Dticn/Ippondo2/7893/" + elements.get(i).attr("src");
            System.out.println(url);
        }
    }
    
    @Test
    public void testRunway() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.livingly.com/runway").get();
        Elements elements = doc.select("div.boxRight div.designerScroller a.designerLink");
        for(int i = 0; i < elements.size(); i++) {
            String url = "http://www.livingly.com/" + elements.get(i).attr("href");
            System.out.println(url);
        }
    }
    
    @Test
    public void testRunwayEach() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.livingly.com//runway/Milan+Fashion+Week+Fall+2016/Uma+Wang/Details" + "/browse").get();
        Elements elements = doc.select("div#Content ul li a img");
        for(int i = 0; i < elements.size(); i++) {
            String src = elements.get(i).attr("src");
            String url = src.replace("s.jpg", "l.jpg");
            System.out.println(url);
        }
    }
    
    @Test
    public void testLove100() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://love100girl.com/").userAgent("Mozilla").get();
        Elements elements = doc.select("div#index-featured1 a.entry-thumbnails-link");
        for(int i = 0; i < elements.size(); i++) {
            String url = elements.get(i).attr("href");
            System.out.println(url);
        }
    }
    
    @Test
    public void testLove100Each() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://love100girl.com/archives/4724").userAgent("Mozilla").get();
        Elements elements = doc.select("div#main div.wp-pagenavi a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if(!element.select("span").isEmpty()) {
                String nextUrl = element.attr("href");
                System.out.println(nextUrl);
            }
        }
        
        elements = doc.select("div.entry-content img");
        for(int i = 0; i < elements.size(); i++) {
            String url = elements.get(i).attr("src");
            System.out.println(url);
        }
    }
    
    @Test
    public void test1000Novel() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://1000novel.com/").userAgent("Mozilla").get();
        Elements elements = doc.select("div#content h2.entry-title a");
        for(int i = 0; i < elements.size(); i++) {
            String url = elements.get(i).attr("href");
            System.out.println(url);
        }
        
        elements = doc.select("div#content div.navigation ul li.pagination-omission");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            element = element.nextElementSibling();
            System.out.println(element.text());
        }
    }
    
    @Test
    public void test1000NovelEach() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://1000novel.com/2016/08/15/%e5%ac%8c%e8%89%b7%e5%a5%b3%e5%8f%8b%e7%9a%84ktv%e8%bc%aa%e5%a7%a6/").userAgent("Mozilla").get();
        Elements elements = doc.select("div.entry-content p.pages a");
        List<String> nextUrls = new ArrayList<String>();
        for(int i = 0; i < elements.size(); i++) {
            String next_url = elements.get(i).attr("href");
            nextUrls.add(next_url);
        }
        
        StringBuffer sb = new StringBuffer();
        elements = doc.select("div.entry-content p");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if(!element.hasClass("pages")) {
                sb.append(element.html());
            }
        }
        
        System.out.println(sb);
    }
    
    @Test
    public void testWikiHow() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.wikihow.com/Main-Page").userAgent("Mozilla").get();
        Elements elements = doc.select("div#fa_container div.thumbnail a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String text = element.text();
            
            System.out.println(text);
            System.out.println(url);
        }
    }
    
    @Test
    public void testWikiHowEach() throws Exception {
        ConfigManager.getInstance();
        Document doc = Jsoup.connect("http://www.wikihow.com/Eat-When-Pregnant-with-Twins").userAgent("Mozilla").get();
        Elements elements = doc.select("div#bodycontents div.section");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String title = element.select("h3").text();
            System.out.println(title);
            
            Elements child_elements = element.select("div.section_text ol li");
            for(int j = 0; j < child_elements.size(); j++) {
                Element child_element = child_elements.get(j);
                String step_num = child_element.select("div.step_num").text();
                String step = child_element.select("div.step b.whb").text();
                
                if(StringUtils.isBlank(step_num)) {
                    continue;
                }
                
                System.out.println(step_num);
                System.out.println(step);
            }
        }
    }
    
    @Test
    public void testProxy() throws Exception {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved("den-entbc-001", 80));
        final URL website = new URL("http://g.e-hentai.org/"); 
        HttpURLConnection httpUrlConnetion = (HttpURLConnection) website.openConnection(proxy);
        httpUrlConnetion.connect();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnetion.getInputStream()));
        StringBuilder buffer = new StringBuilder();
        String str;

        while( (str = br.readLine()) != null )
        {
            buffer.append(str);
        }
        
        Document doc = Jsoup.parse(buffer.toString());
        System.out.println(doc);
    }
    
    @Test
    public void testStory() throws Exception {
        Document doc = Jsoup.connect("http://www.bedtime.com/category/stories/").get();
        Elements elements = doc.select("article h2 a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String title = element.attr("title");
            
            System.out.println(title);
            System.out.println(url);
        }
    }
    
    @Test
    public void testStoryEach() throws Exception {
        Document doc = Jsoup.connect("http://www.bedtime.com/lily-rose-and-the-magic-of-helping/").get();
        Elements elements = doc.select("article div.entry-content");
        System.out.println(elements.html());
    }
    
    @Test
    public void test69Story() throws Exception {
        Document doc = Jsoup.connect("http://69story.com/page/1").get();
        Elements elements = doc.select("main#main div.wp-pagenavi span");
        String pageStr = elements.text();
        int pos = pageStr.lastIndexOf("/");
        try {
            int pages = Integer.parseInt(pageStr.substring(pos + 1, pageStr.length()).trim());
            System.out.println(pages);
        }
        catch(Exception e) {
        }
        
        elements = doc.select("main#main table tr td.entry-content a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String title = element.text();
            String url = element.attr("href");
            
            System.out.println(title);
            System.out.println(url);
        }
    }
    
    @Test
    public void test69StoryEach() throws Exception {
        Document doc = Jsoup.connect("http://69story.com/article/376-%e5%a5%87%e6%80%9d%e5%a6%99%e6%83%b3%e8%a1%a8%e6%bc%94%e6%9c%83.html").get();
        Elements elements = doc.select("main#main article div.entry-content");
        String html = elements.html();
        System.out.println(html);
    }
}
