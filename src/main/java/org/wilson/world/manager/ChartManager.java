package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.chart.AbstractChartProvider;
import org.wilson.world.chart.ChartJumpPageMenuItemProvider;
import org.wilson.world.chart.ChartProvider;
import org.wilson.world.chart.DemoChartProvider;
import org.wilson.world.java.JavaExtensionListener;

public class ChartManager implements JavaExtensionListener<AbstractChartProvider> {
	private static ChartManager instance;
	
	private Map<Integer, ChartProvider> providers = new HashMap<Integer, ChartProvider>();
	
	private static int GLOBAL_ID = 1;
	
	private ChartManager() {
		this.loadSystemChartProviders();
		
		ExtManager.getInstance().addJavaExtensionListener(this);
		MenuManager.getInstance().addJumpPageMenuItemProvider(new ChartJumpPageMenuItemProvider());
	}
	
	private void loadSystemChartProviders() {
		this.addChartProvider(new DemoChartProvider());
	}
	
	public static ChartManager getInstance() {
		if(instance == null) {
			instance = new ChartManager();
		}
		
		return instance;
	}
	
	public void addChartProvider(ChartProvider provider) {
		if(provider != null) {
			provider.setId(GLOBAL_ID++);
			this.providers.put(provider.getId(), provider);
		}
	}
	
	public void removeChartProvider(ChartProvider provider) {
		if(provider != null) {
			this.providers.remove(provider.getId());
		}
	}

	@Override
	public Class<AbstractChartProvider> getExtensionClass() {
		return AbstractChartProvider.class;
	}

	@Override
	public void created(AbstractChartProvider t) {
		this.addChartProvider(t);
	}

	@Override
	public void removed(AbstractChartProvider t) {
		this.removeChartProvider(t);
	}
	
	public List<ChartProvider> getChartProviders() {
		return new ArrayList<ChartProvider>(this.providers.values());
	}
	
	public ChartProvider getChartProvider(int id) {
		return this.providers.get(id);
	}
	
	public ChartProvider getChartProvider(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(ChartProvider provider : this.providers.values()) {
			if(name.equals(provider.getName())) {
				return provider;
			}
		}
		
		return null;
	}
}
