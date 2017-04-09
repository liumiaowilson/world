package org.wilson.world.image;

import org.wilson.world.manager.ImageManager;
import org.wilson.world.manager.StorageManager;

public class DefaultImageContributor extends AbstractImageContributor {
	public static final String IMAGE_PREFIX = "image";
	
	@Override
	public String getStoragePrefix() {
		return ImageManager.STORAGE_PREFIX;
	}

	@Override
	public String getStorageSuffix() {
		return "";
	}

	@Override
	public String getNamePrefix() {
		return IMAGE_PREFIX;
	}

	@Override
	public boolean deleteImage(String name) {
		String assetName = this.toAssetName(name);
		
		try {
			StorageManager.getInstance().deleteStorageAsset(assetName);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

}
