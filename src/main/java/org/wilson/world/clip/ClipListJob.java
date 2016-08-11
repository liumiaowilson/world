package org.wilson.world.clip;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

import net.sf.json.JSONObject;

public class ClipListJob extends SystemWebJob {
    public static final String CLIP_LIST = "clip_list";

    public ClipListJob() {
        this.setDescription("Get a list of clips");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.cliphunter.com/categories/All");
        Elements elements = doc.select("div.mainContainer div.responsiveCont div.innerPane ul.moviethumbs li a.t");
        if(!elements.isEmpty()) {
            List<ClipInfo> infos = new ArrayList<ClipInfo>();
            
            this.getMonitor().start(elements.size());
            
            for(int i = 0; i < elements.size(); i++) {
                String page_url = "http://www.cliphunter.com" + elements.get(i).attr("href");
                
                String body = WebManager.getInstance().getContent(page_url);
                String start = "var gexoFiles = ";
                String end = "var flvQ = ";
                int start_pos = body.indexOf(start);
                int end_pos = body.indexOf(end);
                String json = body.substring(start_pos + start.length(), end_pos);
                json = json.trim().substring(0, json.length() - 2);
                
                if(WebManager.getInstance().getLogger().isTraceEnabled()) {
                    WebManager.getInstance().getLogger().trace("ClipListJob - json is " + json);
                }
                
                JSONObject obj = JSONObject.fromObject(json);
                String url = null;
                String name = null;
                for(Object key : obj.keySet()) {
                    if(key instanceof String) {
                        if(((String)key).contains("_p360.mp4")) {
                            JSONObject child = obj.getJSONObject((String) key);
                            String input = child.getString("url");
                            url = GexoDecoder.decode(input);
                            name = (String) key;
                            break;
                        }
                    }
                }
                
                if(!StringUtils.isBlank(url) && !StringUtils.isBlank(name)) {
                    ClipInfo info = new ClipInfo();
                    info.name = name;
                    info.url = url;
                    infos.add(info);
                }
                
                if(this.getMonitor().isStopRequired()) {
                    this.getMonitor().stop();
                    return;
                }
                this.getMonitor().progress(1);
            }
            
            WebManager.getInstance().put(CLIP_LIST, infos);
        }
    }

}
