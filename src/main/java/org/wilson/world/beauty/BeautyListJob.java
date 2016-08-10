package org.wilson.world.beauty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class BeautyListJob extends SystemWebJob {
    public static final String BEAUTY_LIST = "beauty_list";

    public BeautyListJob() {
        this.setDescription("Get a list of beauties");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.mm131.com/xinggan/");
        Elements elements = doc.select("div.main dl.list-left dd");
        if(!elements.isEmpty()) {
            List<BeautyInfo> infos = new ArrayList<BeautyInfo>();
            
            for(int i = 0; i < elements.size(); i++) {
                Elements item = elements.get(i).select("a");
                if(item.size() == 1) {
                    String url = item.attr("href");
                    
                    doc = WebManager.getInstance().parse(url);
                    elements = doc.select("div.content div.content-page span.page-ch");
                    String text = elements.get(0).text();
                    
                    Pattern p = Pattern.compile("([0-9]+)");
                    Matcher m = p.matcher(text);
                    if(m.find()) {
                        try {
                            int pages = Integer.parseInt(m.group(1));
                            for(int j = 1; j <= pages; j++) {
                                BeautyInfo info = new BeautyInfo();
                                info.url = "http://img1.mm131.com/pic/2606/" + j + ".jpg";
                                infos.add(info);
                            }
                        }
                        catch(Exception e) {
                        }
                    }
                }
            }
            
            WebManager.getInstance().put(BEAUTY_LIST, infos);
        }
    }

}
