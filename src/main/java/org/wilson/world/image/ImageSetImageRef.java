package org.wilson.world.image;

import java.util.List;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.ImageManager;
import org.wilson.world.manager.ImageSetManager;
import org.wilson.world.model.ImageSet;

public class ImageSetImageRef extends DefaultImageRef {
	private String setName;
	
	public ImageSetImageRef(String name, String setName) {
		super();
		this.setName(name);
		this.setName = setName;
	}
	
	@Override
	public String getUrl() {
		ImageRef ref = this.getImageRef();
		
		return ref == null ? "" : ref.getUrl();
	}
	
	private ImageRef getImageRef() {
		ImageSet set = ImageSetManager.getInstance().getImageSet(setName);
		List<String> refNames = set.refs;
		if(refNames.isEmpty()) {
			return null;
		}
		
		int n = DiceManager.getInstance().random(refNames.size());
		String refName = refNames.get(n);
		ImageRef ref = ImageManager.getInstance().getImageRef(refName);
		if(ref == null) {
			return null;
		}
		
		return ref;
	}

	@Override
	public String getUrl(String url, int width, int height, boolean adjust) {
		ImageRef ref = this.getImageRef();
		
		return ref == null ? "" : ref.getUrl(url, width, height, adjust);
	}

}
