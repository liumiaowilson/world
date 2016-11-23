package org.wilson.world.novel;

import org.wilson.world.image.ImageContributor;
import org.wilson.world.image.ImageSetImageContributor;
import org.wilson.world.manager.ImageSetManager;
import org.wilson.world.model.ImageSet;
import org.wilson.world.model.NovelRole;

public class NovelRoleImageProviderImpl implements NovelRoleImageProvider {

	@Override
	public String getImage(NovelRole role) {
		if(role == null) {
			return null;
		}
		
		if(role.variables.containsKey("job")) {
			String job = role.get("job");
			ImageSet set = ImageSetManager.getInstance().getImageSet(job);
			if(set != null) {
				return ImageSetImageContributor.IMAGE_PREFIX + ImageContributor.PREFIX_SEPARATOR + job;
			}
		}
		
		return null;
	}

}
