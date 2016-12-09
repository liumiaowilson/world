package org.wilson.world.image;

import org.wilson.world.manager.StorageManager;
import org.wilson.world.storage.StorageAsset;

public class DefaultImageRef implements ImageRef {
	private String name;
	private StorageAsset asset;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getUrl() {
		try {
			return StorageManager.getInstance().getImageUrl(asset);
		}
		catch(Exception e) {
			return "";
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public StorageAsset getStorageAsset() {
		return this.asset;
	}
	
	public void setStorageAsset(StorageAsset asset) {
		this.asset = asset;
	}

	@Override
	public String getUrl(String url, int width, int height, boolean adjust) {
		try {
			return StorageManager.getInstance().getImageUrl(asset, width, height, adjust);
		}
		catch(Exception e) {
			return "";
		}
	}
}
