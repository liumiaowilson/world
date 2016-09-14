package org.wilson.world.scam;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.ScamManager;
import org.wilson.world.manager.WebManager;

public class PianJuJob extends AbstractScamJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://pianju.huashi123.cn/?order=rand", "Mozilla");
        Elements elements = doc.select("div#primary div.primary-site article h2 a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String title = element.text();
            String url = element.attr("href");
            
            ScamInfo info = new ScamInfo();
            info.from = this.getFrom();
            info.title = title;
            info.url = url;
            ScamManager.getInstance().addScamInfo(info);
        }
    }

    @Override
    public void loadSingle(ScamInfo info) throws Exception {
        if(info.html != null) {
            return;
        }
        
        Document doc = WebManager.getInstance().parse(info.url, "Mozilla");
        Elements elements = doc.select("div#primary div.content-main");
        info.html = elements.html();
    }

}
