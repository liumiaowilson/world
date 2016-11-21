package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.image.ImageInfo;
import org.wilson.world.image.ImageItem;
import org.wilson.world.image.ImageListJob;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;

public class ImageManager implements StorageListener {
	public static final String IMAGE_PATH = "image.jpg";
	
	private Map<Integer, ImageItem> items = new HashMap<Integer, ImageItem>();
    
    public static final String STORAGE_PREFIX = "/images/";
    public static final String STORAGE_SUFFIX = ".jpg";
    
    private static ImageManager instance;
    
    private ImageManager() {
    	StorageManager.getInstance().addStorageListener(this);
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
    
    private boolean accept(StorageAsset asset) {
        String name = asset.name;
        
        if(!name.startsWith(STORAGE_PREFIX)) {
            return false;
        }
        
        if(!name.endsWith(STORAGE_SUFFIX)) {
            return false;
        }
        
        return true;
    }
    
    private ImageItem toImageItem(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        ImageItem item = new ImageItem();
        item.id = asset.id;
        String name = asset.name;
        name = name.substring(STORAGE_PREFIX.length(), name.length() - STORAGE_SUFFIX.length());
        item.name = name;
        
        return item;
    }

	@Override
	public void created(StorageAsset asset) {
		ImageItem item = this.toImageItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
	}

	@Override
	public void deleted(StorageAsset asset) {
		ImageItem item = this.toImageItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
	}

	@Override
	public void reloaded(List<StorageAsset> assets) {
		this.items.clear();
        
        for(StorageAsset asset : assets) {
        	ImageItem item = this.toImageItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
	}

    public String getImageUrl(ImageItem item) throws Exception {
        if(item == null) {
            return "";
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return "";
        }
        
        return StorageManager.getInstance().getImageUrl(asset);
    }
    
    private String toAssetName(String name) {
    	if(!name.startsWith(STORAGE_PREFIX)) {
            name = STORAGE_PREFIX + name;
        }
        
        if(!name.endsWith(STORAGE_SUFFIX)) {
            name = name + STORAGE_SUFFIX;
        }
        
        return name;
    }
    
    public void createImageItem(String name, String url) throws Exception {
    	if(StringUtils.isBlank(name) || StringUtils.isBlank(url)) {
    		return;
    	}
    	
        name = toAssetName(name);
    	
    	StorageManager.getInstance().createStorageAsset(name, url);
    }
    
    public ImageItem getImageItem(int id) {
    	return this.items.get(id);
    }
    
    public List<ImageItem> getImageItems() {
    	return new ArrayList<ImageItem>(this.items.values());
    }
    
    public void deleteImageItem(int id) throws Exception {
    	ImageItem item = this.getImageItem(id);
    	if(item == null) {
    		return;
    	}
    	
    	String name = item.name;
    	
    	name = toAssetName(name);
        
        StorageManager.getInstance().deleteStorageAsset(name);
    }
}
