package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.servlet.ActiveServlet;
import org.wilson.world.servlet.DemoServlet;

public class ServletManager implements JavaExtensionListener<ActiveServlet> {
	private static ServletManager instance;
	
	private Map<String, ActiveServlet> servlets = new HashMap<String, ActiveServlet>();
	
	private ServletManager() {
		ExtManager.getInstance().addJavaExtensionListener(this);
		
		this.loadSystemActiveManagers();
	}
	
	public static ServletManager getInstance() {
		if(instance == null) {
			instance = new ServletManager();
		}
		
		return instance;
	}
	
	private void loadSystemActiveManagers() {
		this.addActiveServlet(new DemoServlet());
	}
	
	public void addActiveServlet(ActiveServlet servlet) {
		if(servlet != null && servlet.getName() != null) {
			this.servlets.put(servlet.getName(), servlet);
		}
	}
	
	public void removeActiveServlet(ActiveServlet servlet) {
		if(servlet != null) {
			this.servlets.remove(servlet.getName());
		}
	}
	
	public ActiveServlet getActiveServlet(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.servlets.get(name);
	}
	
	public List<ActiveServlet> getActiveServlets() {
		return new ArrayList<ActiveServlet>(this.servlets.values());
	}

	@Override
	public Class<ActiveServlet> getExtensionClass() {
		return ActiveServlet.class;
	}

	@Override
	public void created(ActiveServlet t) {
		this.addActiveServlet(t);
	}

	@Override
	public void removed(ActiveServlet t) {
		this.removeActiveServlet(t);
	}
	
	public List<ActiveServlet> getMatchingActiveServlets(String url) {
		List<ActiveServlet> ret = new ArrayList<ActiveServlet>();
		if(StringUtils.isBlank(url)) {
			return ret;
		}
		
		for(ActiveServlet servlet : this.servlets.values()) {
			String pattern = servlet.getPattern();
			if(url.matches(pattern)) {
				ret.add(servlet);
			}
		}
		
		return ret;
	}
}
