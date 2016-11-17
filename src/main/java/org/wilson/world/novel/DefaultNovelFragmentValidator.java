package org.wilson.world.novel;

import org.wilson.world.model.NovelFragment;

public class DefaultNovelFragmentValidator implements NovelFragmentValidator {

	@Override
	public String validate(NovelFragment fragment) {
		if(fragment == null) {
			return null;
		}
		
		String content = fragment.content;
		
		if(!content.contains("我")) {
			return "Missing ME in the fragment.";
		}
		
		if(!content.contains("{") && !content.contains("}")) {
			return "Missing variables in the fragment.";
		}
		
		return null;
	}

}
