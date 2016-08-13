package org.wilson.world.porn;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class JapanPornListJob extends SystemWebJob {
    public static final String JAPAN_PORN_LIST = "japan_porn_list";

    public JapanPornListJob() {
        this.setDescription("Get a list of Japan porn infos");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.unmosaic.net/");
        Elements elements = doc.select("ul.indexnaviul li a");
        if(!elements.isEmpty()) {
            //for limited memory, we do not load too much
            int n = DiceManager.getInstance().random(elements.size());
            String galleryUrl = "http://www.unmosaic.net/" + elements.get(n).attr("href");
            
            Document gallery_doc = WebManager.getInstance().parse(galleryUrl);
            Elements gallery_elements = gallery_doc.select("ol.galleryol li a");
            if(!gallery_elements.isEmpty()) {
                List<PornInfo> infos = new ArrayList<PornInfo>();
                
                this.getMonitor().start(gallery_elements.size());
                
                int pos = galleryUrl.lastIndexOf("/");
                String baseUrl = galleryUrl.substring(0, pos + 1);
                for(int i = 0; i < gallery_elements.size(); i++) {
                    String groupUrl = baseUrl + gallery_elements.get(i).attr("href");
                    
                    Document image_doc = WebManager.getInstance().parse(groupUrl);
                    Elements image_elements = image_doc.select("div#picturesbox img");
                    pos = groupUrl.lastIndexOf("/");
                    String imageBaseUrl = groupUrl.substring(0, pos + 1);
                    for(int j = 0; j < image_elements.size(); j++) {
                        String url = imageBaseUrl + image_elements.get(j).attr("src");
                        
                        PornInfo info = new PornInfo();
                        info.url = url;
                        infos.add(info);
                    }
                    
                    if(this.getMonitor().isStopRequired()) {
                        this.getMonitor().stop();
                        return;
                    }
                    this.getMonitor().progress(1);
                }
                
                WebManager.getInstance().put(JAPAN_PORN_LIST, infos);
            }
        }
    }

}
