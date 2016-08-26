package org.wilson.world.article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.ArticleManager;
import org.wilson.world.manager.WebManager;

public class LiteroticaJob extends AbstractArticleJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("https://www.literotica.com/c/non-consent-stories");
        Elements elements = doc.select("div.b-story-list-box");
        if(!elements.isEmpty()) {
            Element element = elements.get(0);
            
            Elements articles = element.select("div.b-story-list-box-body h3");
            
            for(int i = 0; i < articles.size(); i++) {
                Element article = articles.get(i);
                String title = article.text();
                String url = article.select("a").attr("href");
                
                ArticleInfo info = new ArticleInfo();
                info.title = title;
                info.url = url;
                info.from = this.getFrom();
                ArticleManager.getInstance().addArticleInfo(info);
            }
        }
    }

    @Override
    public void loadSingle(ArticleInfo info) throws Exception {
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
    }

}
