package org.wilson.world.parn;

import org.wilson.world.image.AbstractImageContributor;
import org.wilson.world.manager.ParnManager;

public class ParnImageContributor extends AbstractImageContributor {
	public static final String IMAGE_PREFIX = "parn";
	
	@Override
	public String getStoragePrefix() {
		return ParnManager.STORAGE_PREFIX;
	}

	@Override
	public String getStorageSuffix() {
		return ParnManager.STORAGE_SUFFIX;
	}

	@Override
	public String getNamePrefix() {
		return IMAGE_PREFIX;
	}

}
