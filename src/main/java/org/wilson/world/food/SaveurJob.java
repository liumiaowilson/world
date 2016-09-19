package org.wilson.world.food;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.FoodManager;
import org.wilson.world.manager.WebManager;

public class SaveurJob extends AbstractFoodJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.saveur.com/photos");
        Elements elements = doc.select("div.content-main div.pane-node-title h3 a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String title = element.text();
            String url = "http://www.saveur.com" + element.attr("href");
            
            FoodInfo info = new FoodInfo();
            info.from = this.getFrom();
            info.title = title;
            info.url = url;
            FoodManager.getInstance().addFoodInfo(info);
        }
    }

    @Override
    public void loadSingle(FoodInfo info) throws Exception {
        //Do not load single
    }

}
