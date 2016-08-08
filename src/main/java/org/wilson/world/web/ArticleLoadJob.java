package org.wilson.world.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;

public class ArticleLoadJob extends SystemWebJob {
    public static final String ARTICLE_INFO = "article_info";

    public ArticleLoadJob() {
        this.setDescription("Load the full article");
    }
    
    @Override
    public void run() throws Exception {
        ArticleInfo info = (ArticleInfo) WebManager.getInstance().get(ARTICLE_INFO);
        if(info != null && !info.loaded) {
            Document doc = WebManager.getInstance().parse(info.url);
            Elements pages = doc.select("div.b-pager-pages span.b-pager-caption-t");
            String pagesStr = pages.text();
            String [] pageItems = pagesStr.split(" ");
            int num = 0;
            if(pageItems.length > 0) {
                num = Integer.parseInt(pageItems[0]);
            }
            
            StringBuffer htmlSb = new StringBuffer();
            StringBuffer textSb = new StringBuffer();
            Elements body = doc.select("div.b-story-body-x");
            htmlSb.append(body.html());
            textSb.append(body.text());
            
            if(num > 1) {
                for(int i = 2; i <= num; i++) {
                    doc = Jsoup.connect(info.url + "?page=" + i).get();
                    body = doc.select("div.b-story-body-x");
                    htmlSb.append(body.html());
                    textSb.append(body.text());
                }
            }
            
            info.html = htmlSb.toString();
            info.text = textSb.toString();
            info.loaded = true;
        }
    }

}
