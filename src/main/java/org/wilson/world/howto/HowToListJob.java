package org.wilson.world.howto;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class HowToListJob extends SystemWebJob {
    public static final String HOW_TO_LIST = "how_to_list";
    public static final String HOW_TO_INFO = "how_to_info";
    
    @Override
    public void run() throws Exception {
        HowToInfo info = (HowToInfo) WebManager.getInstance().get(HOW_TO_INFO);
        if(info == null) {
            this.listHowTo();
        }
        else {
            this.loadHowTo(info);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void listHowTo() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.wikihow.com/Main-Page", "Mozilla");
        Elements elements = doc.select("div#fa_container div.thumbnail a");
        List<HowToInfo> infos = WebManager.getInstance().getList(HOW_TO_LIST);
        infos.clear();
        
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String url = element.attr("href");
            String text = element.text();
            
            HowToInfo info = new HowToInfo();
            info.name = text;
            info.url = "http:" + url;
            infos.add(info);
        }
    }

    private void loadHowTo(HowToInfo info) throws Exception {
        String url = info.url;
        
        Document doc = WebManager.getInstance().parse(url, "Mozilla");
        Elements elements = doc.select("div#bodycontents div.section");
        StringBuffer sb = new StringBuffer();
        
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String title = element.select("h3").text();
            sb.append("<h4>" + title + "</h4>");
            
            Elements child_elements = element.select("div.section_text ol li");
            for(int j = 0; j < child_elements.size(); j++) {
                Element child_element = child_elements.get(j);
                String step_num = child_element.select("div.step_num").text();
                String step = child_element.select("div.step b.whb").text();
                
                if(StringUtils.isBlank(step_num)) {
                    continue;
                }
                
                sb.append("<p><b>" + step_num + "</b> " + step + "</p>");
            }
        }
        
        info.answer = sb.toString();
    }
}
