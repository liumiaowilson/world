package org.wilson.world.js;

import org.wilson.world.model.JsFile;

public interface JsFileListener {

	public void created(JsFile file);
	
	public void removed(JsFile file);
}
