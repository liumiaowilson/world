package org.wilson.world.etiquette;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.EtiquetteManager;
import org.wilson.world.manager.WebManager;

public class EtiquetteJob extends AbstractEtiquetteJob {

    @Override
    public void loadList() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.etiquettescholar.com/index.html", "Mozilla");
        Elements elements = doc.select("nav#mymenuright li");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            
            Elements children = element.children();
            if(children.size() == 1) {
                Element child = children.get(0);
                
                String url = "http://www.etiquettescholar.com/" + child.attr("href");
                String title = child.text();
                
                EtiquetteInfo info = new EtiquetteInfo();
                info.from = this.getFrom();
                info.title = title;
                info.url = url;
                EtiquetteManager.getInstance().addEtiquetteInfo(info);
            }
        }
    }

    @Override
    public void loadSingle(EtiquetteInfo info) throws Exception {
        if(info.html != null) {
            return;
        }
        
        Document doc = WebManager.getInstance().parse(info.url, "Mozilla");
        Elements elements = doc.select("article");
        if(!elements.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            
            Elements children = elements.get(0).children();
            for(int i = 0; i < children.size(); i++) {
                Element child = children.get(i);
                if("div".equals(child.tagName())) {
                    continue;
                }
                sb.append(child.outerHtml());
            }
            
            info.html = sb.toString();
        }
    }

}
