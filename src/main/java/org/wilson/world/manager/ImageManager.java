package org.wilson.world.manager;

import java.util.List;

import org.wilson.world.image.ImageInfo;
import org.wilson.world.image.ImageListJob;

public class ImageManager {
    private static ImageManager instance;
    
    private ImageManager() {
    }
    
    public static ImageManager getInstance() {
        if(instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public ImageInfo randomImage() {
        List<ImageInfo> images = (List<ImageInfo>) WebManager.getInstance().get(ImageListJob.IMAGE_LIST);
        if(images == null || images.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(images.size());
        return images.get(n);
    }
}
