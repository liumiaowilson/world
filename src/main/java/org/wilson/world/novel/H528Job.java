package org.wilson.world.novel;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.NovelManager;
import org.wilson.world.manager.WebManager;

public class H528Job extends AbstractNovelJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.h528.com/", "Mozilla");
        if(!this.loadListFromPage(doc)) {
            return;
        }
        
        int pages = 1;
        Elements elements = doc.select("div#main div#content div.pagination div.wp-pagenavi span.pages");
        String pageStr = elements.text();
        int pos = pageStr.lastIndexOf("of");
        try {
            pages = Integer.parseInt(pageStr.substring(pos + 2, pageStr.length()).trim());
        }
        catch(Exception e) {
        }
        if(pages > 30) {
            pages = 30;
        }
        
        if(pages > 1) {
            this.getMonitor().adjust(pages - 1);
            for(int i = 2; i <= pages; i++) {
                doc = WebManager.getInstance().parse("http://www.h528.com/page/" + i);
                if(!this.loadListFromPage(doc)) {
                    return;
                }
            }
        }
    }
    
    private boolean loadListFromPage(Document doc) throws Exception {
        Elements elements = doc.select("div#main div#content div.post table td h3 a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String title = element.text();
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
        Elements elements = doc.select("div#main div#content div.post div.entry");
        info.html = elements.html();
    }
}
