package org.wilson.world.image;

import org.wilson.world.manager.ImageManager;

public class DefaultImageContributor extends AbstractImageContributor {
	public static final String IMAGE_PREFIX = "image";
	
	@Override
	public String getStoragePrefix() {
		return ImageManager.STORAGE_PREFIX;
	}

	@Override
	public String getStorageSuffix() {
		return ImageManager.STORAGE_SUFFIX;
	}

	@Override
	public String getNamePrefix() {
		return IMAGE_PREFIX;
	}

}
