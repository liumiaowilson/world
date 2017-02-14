package org.wilson.world.reference;

import org.wilson.world.manager.ReferenceManager;

public class DefaultReferenceProvider implements ReferenceProvider {

	@Override
	public String getName() {
		return "default";
	}

	@Override
	public Object getReference(String key) {
		return ReferenceManager.getInstance().getDefaultReference(key);
	}

}
