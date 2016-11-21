package org.wilson.world.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.model.ImageSet;

public class ImageSetImageContributor implements ImageContributor, CacheListener<ImageSet> {
	public static final String IMAGE_PREFIX = "set";
	
	private Map<String, ImageRef> images = new HashMap<String, ImageRef>();
	
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

	@Override
	public String getNamePrefix() {
		return IMAGE_PREFIX;
	}

	@Override
	public void cachePut(ImageSet old, ImageSet v) {
		if(old != null) {
			this.cacheDeleted(old);
		}
		
		String name = this.getNamePrefix() + PREFIX_SEPARATOR + v.name;
		ImageSetImageRef ref = new ImageSetImageRef(name, v);
		this.images.put(ref.getName(), ref);
	}

	@Override
	public void cacheDeleted(ImageSet v) {
		String name = this.getNamePrefix() + PREFIX_SEPARATOR + v.name;
		this.images.remove(name);
	}

	@Override
	public void cacheLoaded(List<ImageSet> all) {
	}

	@Override
	public void cacheLoading(List<ImageSet> old) {
		this.images.clear();
	}
}
