package org.wilson.world.joke;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.JokeManager;
import org.wilson.world.manager.WebManager;

public class XiaohuaJob extends AbstractJokeJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.xiaohuayoumo.com/");
        Elements elements = doc.select("div#main-menu li.expanded ul li.leaf a");
        this.getMonitor().adjust(elements.size());
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String categoryUrl = "http://www.xiaohuayoumo.com" + element.attr("href");
            
            boolean ret = this.loadFromCategory(categoryUrl);
            if(!ret) {
                return;
            }
            
            if(this.getMonitor().isStopRequired()) {
                this.getMonitor().stop();
                return;
            }
            else {
                this.getMonitor().progress(1);
            }
        }
    }
    
    private boolean loadFromCategory(String categoryUrl) throws Exception {
        Document doc = WebManager.getInstance().parse(categoryUrl);
        boolean ret = this.loadFromCategoryPage(doc);
        if(!ret) {
            return false;
        }
        
        Elements elements = doc.select("div#block-system-main ul.pager li.last a");
        Element lastPage = elements.get(0);
        String lastPageUrl = lastPage.attr("href");
        int pos = lastPageUrl.lastIndexOf("=");
        int pages = 1;
        try {
            pages = Integer.parseInt(lastPageUrl.substring(pos + 1).trim());
        }
        catch(Exception e) {
        }
        if(pages > 5) {
            pages = 5;
        }
        
        this.getMonitor().adjust(pages);
        for(int i = 1; i <= pages; i++) {
            doc = WebManager.getInstance().parse("http://www.xiaohuayoumo.com/jingdianxiaohua?page=" + i);
            ret = this.loadFromCategoryPage(doc);
            if(!ret) {
                return false;
            }
            
            if(this.getMonitor().isStopRequired()) {
                this.getMonitor().stop();
                return false;
            }
            else {
                this.getMonitor().progress(1);
            }
        }
        
        return true;
    }
    
    private boolean loadFromCategoryPage(Document doc) throws Exception {
        Elements elements = doc.select("div#main div.content ul li.views-row div.content a");
        
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String title = element.text();
            String url = "http://www.xiaohuayoumo.com" + element.attr("href");
            
            JokeInfo info = new JokeInfo();
            info.from = this.getFrom();
            info.title = title;
            info.url = url;
            JokeManager.getInstance().addJokeInfo(info);
        }
        
        return true;
    }

    @Override
    public void loadSingle(JokeInfo info) throws Exception {
        if(info.html != null) {
            return;
        }
        
        Document doc = WebManager.getInstance().parse(info.url);
        Elements elements = doc.select("div#block-system-main article div.field-item p");
        info.html = elements.outerHtml();
    }

}
