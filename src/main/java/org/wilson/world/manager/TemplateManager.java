package org.wilson.world.manager;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class TemplateManager {
	private static TemplateManager instance;
	
	private VelocityEngine engine;
	
	private TemplateManager() {
		this.engine = new VelocityEngine();
		this.engine.init();
	}
	
	public static TemplateManager getInstance() {
		if(instance == null) {
			instance = new TemplateManager();
		}
		
		return instance;
	}
	
	public String evaluate(String template, Map<String, Object> context) {
		if(StringUtils.isBlank(template)) {
			return null;
		}
		if(context == null) {
			context = new HashMap<String, Object>();
		}
		
		VelocityContext vc = new VelocityContext();
		for(Entry<String, Object> entry : context.entrySet()) {
			vc.put(entry.getKey(), entry.getValue());
		}
		
		StringWriter sw = new StringWriter();
		this.engine.evaluate(vc, sw, this.getClass().getSimpleName(), template);
		
		return sw.toString();
	}
}
