package org.wilson.world.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;

public class ArticleListJob extends SystemWebJob {
    public static final String ARTICLE_LIST = "article_list";

    public ArticleListJob() {
        this.setDescription("Get a list of articles");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("https://www.literotica.com/c/non-consent-stories");
        Elements elements = doc.select("div.b-story-list-box");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            
            Elements articles = element.select("div.b-story-list-box-body h3");
            List<ArticleInfo> infos = new ArrayList<ArticleInfo>();
            for(int i = 0; i < articles.size(); i++) {
                Element article = articles.get(i);
                String title = article.text();
                String url = article.select("a").attr("href");
                
                ArticleInfo info = new ArticleInfo();
                info.title = title;
                info.url = url;
                infos.add(info);
            }
            
            WebManager.getInstance().put(ARTICLE_LIST, infos);
        }
    }

}
