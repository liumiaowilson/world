package org.wilson.world.manga;

import org.wilson.world.image.AbstractImageContributor;
import org.wilson.world.manager.MangaManager;

public class MangaImageContributor extends AbstractImageContributor {
	public static final String IMAGE_PREFIX = "manga";
	
	@Override
	public String getNamePrefix() {
		return IMAGE_PREFIX;
	}

	@Override
	public String getStoragePrefix() {
		return MangaManager.STORAGE_PREFIX;
	}

	@Override
	public String getStorageSuffix() {
		return MangaManager.STORAGE_SUFFIX;
	}

}
