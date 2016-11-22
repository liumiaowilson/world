package org.wilson.world.image;

import java.util.List;

import org.wilson.world.manager.DiceManager;
import org.wilson.world.manager.ImageManager;
import org.wilson.world.model.ImageSet;

public class ImageSetImageRef extends DefaultImageRef {
	private ImageSet set;
	
	public ImageSetImageRef(String name, ImageSet set) {
		super();
		this.setName(name);
		this.set = set;
	}
	
	@Override
	public String getUrl() {
		List<String> refNames = this.set.refs;
		if(refNames.isEmpty()) {
			return null;
		}
		
		int n = DiceManager.getInstance().random(refNames.size());
		String refName = refNames.get(n);
		ImageRef ref = ImageManager.getInstance().getImageRef(refName);
		if(ref == null) {
			return null;
		}
		
		ref.setWidth(this.getWidth());
		ref.setHeight(this.getHeight());
		ref.setAdjust(this.isAdjust());
		
		return ref.getUrl();
	}

}
