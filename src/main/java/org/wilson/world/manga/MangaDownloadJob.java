package org.wilson.world.manga;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.DownloadManager;
import org.wilson.world.manager.MangaManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class MangaDownloadJob extends SystemWebJob {
    
    @SuppressWarnings("unchecked")
    @Override
    public void run() throws Exception {
        List<MangaInfo> infos = (List<MangaInfo>) WebManager.getInstance().get(MangaListJob.MANGA_LIST);
        if(infos != null && !infos.isEmpty()) {
            int n = DiceManager.getInstance().random(infos.size());
            
            MangaInfo info = infos.get(n);
            
            if(info.urls != null) {
                this.getMonitor().start(info.urls.length);
                
                String dir = MangaManager.getInstance().getMangaDir();
                MangaManager.getInstance().ensureMangaDir();
                
                MangaManager.getInstance().clean();
                
                for(int i = 0; i < info.urls.length; i++) {
                    try {
                        String url = info.urls[i];
                        
                        Document doc = WebManager.getInstance().parse(url);
                        Elements elements = doc.select("div.sni>a img");
                        String src = elements.attr("src");
                        
                        DownloadManager.getInstance().download(src, dir + (i + 1) + ".jpg");
                    }
                    catch(Exception e) {
                        WebManager.getInstance().getLogger().warn(e.getMessage());
                    }
                    
                    if(this.getMonitor().isStopRequired()) {
                        this.getMonitor().stop();
                        return;
                    }
                    this.getMonitor().progress(1);
                }
            }
        }
    }

}
