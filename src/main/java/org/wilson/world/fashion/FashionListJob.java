package org.wilson.world.fashion;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class FashionListJob extends SystemWebJob {
    public static final String FASHION_LIST = "fashion_list";

    public FashionListJob() {
        this.setDescription("Get a list of fashion models");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.livingly.com/runway");
        Elements elements = doc.select("div.boxRight div.designerScroller a.designerLink");
        if(!elements.isEmpty()) {
            List<FashionInfo> infos = new ArrayList<FashionInfo>();
            this.getMonitor().start(elements.size());
            
            for(int i = 0; i < elements.size(); i++) {
                String designer_url = "http://www.livingly.com" + elements.get(i).attr("href");
                
                Document designer_doc = WebManager.getInstance().parse(designer_url + "/browse");
                Elements designer_elements = designer_doc.select("div#Content ul li a img");
                for(int j = 0; j < designer_elements.size(); j++) {
                    String src = designer_elements.get(j).attr("src");
                    String url = src.replace("s.jpg", "l.jpg");
                    
                    FashionInfo info = new FashionInfo();
                    info.url = url;
                    infos.add(info);
                }

                if(this.getMonitor().isStopRequired()) {
                    this.getMonitor().stop();
                    return;
                }
                this.getMonitor().progress(1);
            }
            
            WebManager.getInstance().put(FASHION_LIST, infos);
        }
    }

}
