package org.wilson.world.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.manager.StorageManager;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;

public abstract class AbstractImageContributor implements ImageContributor, StorageListener {
	private static final Logger logger = Logger.getLogger(AbstractImageContributor.class);
	
	public static final String PREFIX_SEPARATOR = ":";
	
	protected Map<String, ImageRef> images = new HashMap<String, ImageRef>();
	
	public AbstractImageContributor() {
		StorageManager.getInstance().addStorageListener(this);
	}
	
	private boolean accept(StorageAsset asset) {
        String name = asset.name;
        
        if(!name.startsWith(this.getStoragePrefix())) {
            return false;
        }
        
        if(!name.endsWith(this.getStorageSuffix())) {
            return false;
        }
        
        return true;
    }
	
	protected String toImageName(String name) {
		name = name.substring(this.getStoragePrefix().length(), name.length() - this.getStorageSuffix().length());
		name = this.getNamePrefix() + PREFIX_SEPARATOR + name;
		return name;
	}
	
	protected String toAssetName(String name) {
		String [] items = name.split(PREFIX_SEPARATOR);
		name = items[1];
		return this.getStoragePrefix() + name + this.getStorageSuffix();
	}
    
    private ImageRef toImageRef(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        ImageRef ref = new ImageRef();
        ref.name = this.toImageName(asset.name);
        try {
        	ref.url = StorageManager.getInstance().getImageUrl(asset);
        }
        catch(Exception e) {
        	logger.error(e);
        }
        
        return ref;
    }

	@Override
	public void created(StorageAsset asset) {
		ImageRef ref = this.toImageRef(asset);
        if(ref != null) {
            this.images.put(ref.name, ref);
        }
	}

	@Override
	public void deleted(StorageAsset asset) {
		ImageRef ref = this.toImageRef(asset);
        if(ref != null) {
            this.images.remove(ref.name);
        }
	}

	@Override
	public void reloaded(List<StorageAsset> assets) {
		this.images.clear();
        
        for(StorageAsset asset : assets) {
        	ImageRef ref = this.toImageRef(asset);
            if(ref != null) {
                this.images.put(ref.name, ref);
            }
        }
	}

	@Override
	public List<String> getNames() {
		return new ArrayList<String>(this.images.keySet());
	}

	@Override
	public ImageRef getImage(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.images.get(name);
	}

	/**
	 * Get the storage prefix
	 * 
	 * @return
	 */
	public abstract String getStoragePrefix();
	
	/**
	 * Get the storage suffix
	 * 
	 * @return
	 */
	public abstract String getStorageSuffix();
}