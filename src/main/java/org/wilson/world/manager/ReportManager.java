package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.report.AbstractReportBuilder;
import org.wilson.world.report.DemoReportBuilder;
import org.wilson.world.report.ReportBuilder;
import org.wilson.world.report.ReportJumpPageMenuItemProvider;

public class ReportManager implements JavaExtensionListener<AbstractReportBuilder> {
	private static ReportManager instance;
	
	private Map<Integer, ReportBuilder> builders = new HashMap<Integer, ReportBuilder>();
	private static int GLOBAL_ID = 1;
	
	private ReportManager() {
		ExtManager.getInstance().addJavaExtensionListener(this);
		MenuManager.getInstance().addJumpPageMenuItemProvider(new ReportJumpPageMenuItemProvider());
		
		this.loadSystemReportBuilders();
	}
	
	private void loadSystemReportBuilders() {
		this.addReportBuilder(new DemoReportBuilder());
	}
	
	public static ReportManager getInstance() {
		if(instance == null) {
			instance = new ReportManager();
		}
		
		return instance;
	}
	
	public void addReportBuilder(ReportBuilder builder) {
		if(builder != null && builder.getName() != null) {
			builder.setId(GLOBAL_ID++);
			this.builders.put(builder.getId(), builder);
		}
	}
	
	public void removeReportBuilder(ReportBuilder builder) {
		if(builder != null && builder.getName() != null) {
			this.builders.remove(builder.getId());
		}
	}
	
	public List<ReportBuilder> getReportBuilders() {
		return new ArrayList<ReportBuilder>(this.builders.values());
	}
	
	public ReportBuilder getReportBuilder(int id) {
		return this.builders.get(id);
	}
	
	public ReportBuilder getReportBuilder(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		for(ReportBuilder builder : this.builders.values()) {
			if(name.equals(builder.getName())) {
				return builder;
			}
		}
		
		return null;
	}

	@Override
	public Class<AbstractReportBuilder> getExtensionClass() {
		return AbstractReportBuilder.class;
	}

	@Override
	public void created(AbstractReportBuilder t) {
		this.addReportBuilder(t);
	}

	@Override
	public void removed(AbstractReportBuilder t) {
		this.removeReportBuilder(t);
	}
}
