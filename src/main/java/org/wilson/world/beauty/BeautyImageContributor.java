package org.wilson.world.beauty;

import org.wilson.world.image.AbstractImageContributor;
import org.wilson.world.manager.BeautyManager;

public class BeautyImageContributor extends AbstractImageContributor {
	public static final String IMAGE_PREFIX = "beauty";
	
	@Override
	public String getStoragePrefix() {
		return BeautyManager.STORAGE_PREFIX;
	}

	@Override
	public String getStorageSuffix() {
		return BeautyManager.STORAGE_SUFFIX;
	}

	@Override
	public String getNamePrefix() {
		return IMAGE_PREFIX;
	}

}
