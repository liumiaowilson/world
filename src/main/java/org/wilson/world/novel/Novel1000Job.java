package org.wilson.world.novel;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.NovelManager;
import org.wilson.world.manager.WebManager;

public class Novel1000Job extends AbstractNovelJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://1000novel.com/","Mozilla");
        if(!this.loadListFromPage(doc)) {
            return;
        }
        
        int pages = 1;
        Elements elements = doc.select("div#content div.navigation ul li.pagination-omission");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            element = element.nextElementSibling();
            try {
                pages = Integer.parseInt(element.text());
            }
            catch(Exception e) {
            }
        }
        if(pages > 30) {
            pages = 30;
        }
        
        if(pages > 1) {
            this.getMonitor().adjust(pages - 1);
            for(int i = 2; i <= pages; i++) {
                doc = WebManager.getInstance().parse("http://1000novel.com/page/" + i,"Mozilla");
                if(!this.loadListFromPage(doc)) {
                    return;
                }
            }
        }
    }
    
    private boolean loadListFromPage(Document doc) throws Exception {
        Elements elements = doc.select("div#content h2.entry-title a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String title = element.attr("title");
            NovelInfo info = new NovelInfo();
            info.from = this.getFrom();
            info.url = url;
            info.title = title;
            NovelManager.getInstance().addNovelInfo(info);
        }
        
        if(this.getMonitor().isStopRequired()) {
            this.getMonitor().stop();
            return false;
        }
        else {
            this.getMonitor().progress(1);
        }
        
        return true;
    }

    @Override
    public void loadSingle(NovelInfo info) throws Exception {
        if(info.html != null) {
            //do not overload
            return;
        }
        
        Document doc = WebManager.getInstance().parse(info.url, "Mozilla");
        StringBuffer sb = new StringBuffer();
        boolean ret = this.loadSingleFromPage(doc, sb);
        if(!ret) {
            info.html = sb.toString();
            return;
        }
        
        Elements elements = doc.select("div.entry-content p.pages a");
        List<String> nextUrls = new ArrayList<String>();
        for(int i = 0; i < elements.size(); i++) {
            String next_url = elements.get(i).attr("href");
            nextUrls.add(next_url);
        }
        
        if(!nextUrls.isEmpty()) {
            this.getMonitor().adjust(nextUrls.size() - 1);
            for(String nextUrl : nextUrls) {
                doc = WebManager.getInstance().parse(nextUrl, "Mozilla");
                ret = this.loadSingleFromPage(doc, sb);
                if(!ret) {
                    info.html = sb.toString();
                    return;
                }
            }
        }
        
        info.html = sb.toString();
    }

    private boolean loadSingleFromPage(Document doc, StringBuffer sb) throws Exception {
        Elements elements = doc.select("div.entry-content p");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if(!element.hasClass("pages")) {
                sb.append("<p>" + element.html() + "</p>");
            }
        }
        
        if(this.getMonitor().isStopRequired()) {
            this.getMonitor().stop();
            return false;
        }
        else {
            this.getMonitor().progress(1);
        }
        
        return true;
    }
}
