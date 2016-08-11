package org.wilson.world.porn;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class PornListJob extends SystemWebJob {
    public static final String PORN_LIST = "porn_list";

    public PornListJob() {
        this.setDescription("Get a list of porn infos");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.pornpics.com/recent/hardcore/");
        Elements elements = doc.select("div#main li.thumbwook");
        if(!elements.isEmpty()) {
            List<PornInfo> infos = new ArrayList<PornInfo>();
            
            this.getMonitor().start(elements.size());
            
            for(int i = 0; i < elements.size(); i++) {
                Element li = elements.get(i);
                Elements item = li.select("a.rel-link");
                String url = item.attr("href");
                
                if(!StringUtils.isBlank(url)) {
                    Document child_doc = WebManager.getInstance().parse(url);
                    Elements child_elements = child_doc.select("div#main li.thumbwook");
                    for(int j = 0; j < child_elements.size(); j++) {
                        Element j_li = child_elements.get(j);
                        Elements j_item = j_li.select("a.rel-link");
                        String item_url = j_item.attr("href");
                        
                        PornInfo info = new PornInfo();
                        info.url = item_url;
                        infos.add(info);
                    }
                }
                
                if(this.getMonitor().isStopRequired()) {
                    this.getMonitor().stop();
                    return;
                }
                this.getMonitor().progress(1);
            }
            
            WebManager.getInstance().put(PORN_LIST, infos);
        }
    }

}
