package org.wilson.world.manga;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class MangaListJob extends SystemWebJob {
    public static final String MANGA_LIST = "manga_list";

    public MangaListJob() {
        this.setDescription("Get a list of mangas");
    }
    
    @Override
    public void run() throws Exception {
        List<MangaInfo> infos = new ArrayList<MangaInfo>();
        
        Document doc = WebManager.getInstance().parse("http://g.e-hentai.org/");
        Elements elements = doc.select("div.ido table.itg div.it5 a");
        
        this.getMonitor().start(elements.size());
        
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String title = element.text();
            
            Document child_doc = WebManager.getInstance().parse(url);
            Elements child_elements = child_doc.select("div.gdtm a");
            if(child_elements.size() > 30 || child_elements.size() == 0) {
                continue;
            }
            List<String> urls = new ArrayList<String>();
            for(int j = 0; j < child_elements.size(); j++) {
                String child_url = child_elements.get(j).attr("href");
                if(!StringUtils.isBlank(child_url)) {
                    urls.add(child_url);
                }
            }
            
            MangaInfo info = new MangaInfo();
            info.title = title;
            info.urls = urls.toArray(new String[0]);
            
            infos.add(info);
            
            this.getMonitor().progress(1);
        }
        
        WebManager.getInstance().put(MANGA_LIST, infos);
    }

}
