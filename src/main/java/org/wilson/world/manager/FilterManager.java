package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.filter.ActiveFilter;
import org.wilson.world.filter.DemoFilter;
import org.wilson.world.java.JavaExtensionListener;

public class FilterManager implements JavaExtensionListener<ActiveFilter> {
	private static FilterManager instance;
	
	private Map<String, ActiveFilter> filters = new HashMap<String, ActiveFilter>();
	
	private FilterManager() {
		ExtManager.getInstance().addJavaExtensionListener(this);
		
		this.loadSystemActiveFilters();
	}
	
	public static FilterManager getInstance() {
		if(instance == null) {
			instance = new FilterManager();
		}
		
		return instance;
	}
	
	private void loadSystemActiveFilters() {
		this.addActiveFilter(new DemoFilter());
	}
	
	public void addActiveFilter(ActiveFilter filter) {
		if(filter != null && filter.getName() != null) {
			this.filters.put(filter.getName(), filter);
		}
	}
	
	public void removeActiveFilter(ActiveFilter filter) {
		if(filter != null) {
			this.filters.remove(filter.getName());
		}
	}
	
	public List<ActiveFilter> getActiveFilters() {
		return new ArrayList<ActiveFilter>(this.filters.values());
	}
	
	public ActiveFilter getActiveFilter(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.filters.get(name);
	}

	@Override
	public Class<ActiveFilter> getExtensionClass() {
		return ActiveFilter.class;
	}

	@Override
	public void created(ActiveFilter t) {
		this.addActiveFilter(t);
	}

	@Override
	public void removed(ActiveFilter t) {
		this.removeActiveFilter(t);
	}
	
	public List<ActiveFilter> getMatchingActiveFilters(String url) {
		List<ActiveFilter> ret = new ArrayList<ActiveFilter>();
		if(StringUtils.isBlank(url)) {
			return ret;
		}
		
		for(ActiveFilter filter : this.filters.values()) {
			String pattern = filter.getPattern();
			if(url.matches(pattern)) {
				ret.add(filter);
			}
		}
		
		return ret;
	}
}
