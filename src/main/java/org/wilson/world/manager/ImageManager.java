package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.image.DefaultImageContributor;
import org.wilson.world.image.ImageContributor;
import org.wilson.world.image.ImageInfo;
import org.wilson.world.image.ImageItem;
import org.wilson.world.image.ImageListJob;
import org.wilson.world.image.ImageRef;
import org.wilson.world.image.ImageRefInfo;
import org.wilson.world.image.ImageSetImageContributor;
import org.wilson.world.manga.MangaImageContributor;
import org.wilson.world.model.ImageList;
import org.wilson.world.model.ImageSet;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;

public class ImageManager implements StorageListener {
	public static final String IMAGE_PATH = "image.jpg";
	
	private Map<Integer, ImageItem> items = new HashMap<Integer, ImageItem>();
    
    public static final String STORAGE_PREFIX = "/images/";
    public static final String STORAGE_SUFFIX = ".jpg";
    
    private List<ImageContributor> contributors = new ArrayList<ImageContributor>();
    
    private static ImageManager instance;
    
    private ImageManager() {
    	StorageManager.getInstance().addStorageListener(this);
    	
    	this.addImageContributor(new DefaultImageContributor());
    }
    
    public static ImageManager getInstance() {
        if(instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }
    
    public void addImageContributor(ImageContributor contributor) {
    	if(contributor != null) {
    		this.contributors.add(contributor);
    	}
    }
    
    public void removeImageContributor(ImageContributor contributor) {
    	if(contributor != null) {
    		this.contributors.remove(contributor);
    	}
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
    
    public ImageItem getImageItem(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	for(ImageItem item : this.items.values()) {
    		if(name.equals(item.name)) {
    			return item;
    		}
    	}
    	
    	return null;
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
    
    public List<String> getImageRefNames() {
    	List<String> names = new ArrayList<String>();
    	
    	for(ImageContributor contributor : this.contributors) {
    		names.addAll(contributor.getNames());
    	}
    	
    	return names;
    }
    
    public ImageContributor getImageContributorByPrefix(String prefix) {
    	if(StringUtils.isBlank(prefix)) {
    		return null;
    	}
    	
    	for(ImageContributor contributor : this.contributors) {
    		if(prefix.equals(contributor.getNamePrefix())) {
    			return contributor;
    		}
    	}
    	
    	return null;
    }
    
    public List<String> getImageRefNames(String prefix) {
    	if(StringUtils.isBlank(prefix)) {
    		return Collections.emptyList();
    	}
    	
    	ImageContributor contributor = this.getImageContributorByPrefix(prefix);
    	if(contributor == null) {
    		return Collections.emptyList();
    	}
    	
    	return contributor.getNames();
    }
    
    public ImageRef getImageRef(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	for(ImageContributor contributor : this.contributors) {
    		ImageRef ref = contributor.getImage(name);
    		if(ref != null) {
    			return ref;
    		}
    	}
    	
    	return null;
    }
    
    public List<ImageRefInfo> getImageRefInfos() {
    	List<ImageRefInfo> infos = new ArrayList<ImageRefInfo>();
    	
    	for(ImageContributor contributor : this.contributors) {
    		List<String> names = contributor.getNames();
    		String prefix = contributor.getNamePrefix();
    		for(String name : names) {
    			ImageRefInfo info = new ImageRefInfo();
    			info.from = prefix;
    			info.name = name;
    			infos.add(info);
    		}
    	}
    	
    	return infos;
    }
    
    public boolean isNaturallyGrouped(String prefix) {
    	if(StringUtils.isBlank(prefix)) {
    		return false;
    	}
    	
    	return ImageSetImageContributor.IMAGE_PREFIX.equals(prefix) ||
    			MangaImageContributor.IMAGE_PREFIX.equals(prefix);
    }
    
    public List<ImageRefInfo> getUngroupedImageRefInfos() {
    	List<ImageRefInfo> infos = new ArrayList<ImageRefInfo>();
    	
    	for(ImageRefInfo info : this.getImageRefInfos()) {
    		if(this.isNaturallyGrouped(info.from)) {
    			continue;
    		}
    		List<ImageSet> sets = ImageSetManager.getInstance().getEnclosingImageSets(info.name);
    		List<ImageList> lists = ImageListManager.getInstance().getEnclosingImageLists(info.name);
    		if(sets.isEmpty() && lists.isEmpty()) {
    			infos.add(info);
    		}
    	}
    	
    	return infos;
    }
}
