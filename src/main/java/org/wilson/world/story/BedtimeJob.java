package org.wilson.world.story;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.StoryManager;
import org.wilson.world.manager.WebManager;

public class BedtimeJob extends AbstractStoryJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.bedtime.com/category/stories/");
        Elements elements = doc.select("article h2 a");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String title = element.attr("title");
            
            StoryInfo info = new StoryInfo();
            info.from = this.getFrom();
            info.title = title;
            info.url = url;
            
            StoryManager.getInstance().addStoryInfo(info);
        }
    }

    @Override
    public void loadSingle(StoryInfo info) throws Exception {
        Document doc = WebManager.getInstance().parse(info.url);
        Elements elements = doc.select("article div.entry-content");
        info.html = elements.html();
        String separator = "<!--www.crestaproject.com Social Button in Content Start-->";
        int pos = info.html.lastIndexOf(separator);
        if(pos >= 0) {
            info.html = info.html.substring(0, pos);
        }
    }

}
