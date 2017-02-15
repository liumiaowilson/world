package org.wilson.world.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.JsFileManager;
import org.wilson.world.model.JsFile;
import org.wilson.world.reference.ReferenceIterable;
import org.wilson.world.reference.ReferenceProvider;

public class JsFileReferenceProvider implements ReferenceProvider, ReferenceIterable, JsFileListener {

	private Map<String, String> refs = new HashMap<String, String>();
	
	@Override
	public List<String> getKeys() {
		return new ArrayList<String>(refs.keySet());
	}

	@Override
	public String getName() {
		return "jsFile";
	}

	@Override
	public Object getReference(String key) {
		return this.refs.get(key);
	}

	@Override
	public void init() {
		JsFileManager.getInstance().addJsFileListener(this);
	}

	@Override
	public void destroy() {
		JsFileManager.getInstance().removeJsFileListener(this);
	}

	@Override
	public void created(JsFile file) {
		if(file != null) {
			if(JsFileStatus.Active.name().equals(file.status)) {
				file = JsFileManager.getInstance().getJsFile(file.id, false);
				this.refs.put(file.name, file.source);
			}
		}
	}

	@Override
	public void removed(JsFile file) {
		if(file != null) {
			this.refs.remove(file.name);
		}
	}

}
