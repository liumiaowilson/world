package org.wilson.world.image;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wilson.world.manager.WebManager;
import org.wilson.world.web.SystemWebJob;

public class ImageListJob extends SystemWebJob {
    public static final String IMAGE_LIST = "image_list";

    public ImageListJob() {
        this.setDescription("Get a list of images");
    }
    
    @Override
    public void run() throws Exception {
        Document doc = WebManager.getInstance().parse("http://www.freeimages.com/new");
        Elements elements = doc.select("div.listing-main ul li img");
        if(!elements.isEmpty()) {
            List<ImageInfo> images = new ArrayList<ImageInfo>();
            for(int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String image = element.attr("rel");
                String title = element.parent().parent().attr("title");
                if(!StringUtils.isBlank(image)) {
                    ImageInfo info = new ImageInfo();
                    info.name = title;
                    info.url = image;
                    images.add(info);
                }
            }
            
            WebManager.getInstance().put(IMAGE_LIST, images);
        }
    }

}
