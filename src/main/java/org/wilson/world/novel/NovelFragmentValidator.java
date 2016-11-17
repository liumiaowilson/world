package org.wilson.world.novel;

import org.wilson.world.model.NovelFragment;

public interface NovelFragmentValidator {
	/**
	 * Validate the novel fragment and returns the result
	 * 
	 * @param fragment
	 * @return
	 */
	public String validate(NovelFragment fragment);
}
