package org.wilson.world.parn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.ParnManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class JapanParnList2Job extends SystemWebJob {
    public static final String FROM = "love100girl";

    public JapanParnList2Job() {
        this.setDescription("Get a list of Japan parn infos");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://love100girl.com/", "Mozilla");
        Elements elements = doc.select("div#index-featured1 a.entry-thumbnails-link");
        if(!elements.isEmpty()) {
            ParnManager.getInstance().clearParnInfos(FROM);

            this.getMonitor().start(elements.size());
            
            for(int i = 0; i < elements.size(); i++) {
                String page_url = elements.get(i).attr("href");
                
                List<String> nextUrls = new ArrayList<String>();
                this.loadPage(page_url, nextUrls, true);
                
                if(!nextUrls.isEmpty()) {
                    this.getMonitor().adjust(nextUrls.size());
                    
                    for(String nextUrl : nextUrls) {
                        this.loadPage(nextUrl, null, false);
                        
                        if(this.getMonitor().isStopRequired()) {
                            this.getMonitor().stop();
                            return;
                        }
                        this.getMonitor().progress(1);
                    }
                }
                
                if(this.getMonitor().isStopRequired()) {
                    this.getMonitor().stop();
                    return;
                }
                this.getMonitor().progress(1);
            }
        }
    }

    private void loadPage(String url, List<String> nextUrls, boolean loadNextUrls) throws IOException {
        Document doc = WebManager.getInstance().parse(url, "Mozilla");
        Elements elements = null;
        if(loadNextUrls) {
            elements = doc.select("div#main div.wp-pagenavi a");
            for(int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                if(!element.select("span").isEmpty()) {
                    String nextUrl = element.attr("href");
                    nextUrls.add(nextUrl);
                }
            }
        }
        
        elements = doc.select("div.entry-content img");
        for(int i = 0; i < elements.size(); i++) {
            String image_url = elements.get(i).attr("src");
            
            ParnInfo info = new ParnInfo();
            info.from = FROM;
            info.url = image_url;
            ParnManager.getInstance().addParnInfo(info);
        }
    }
}
