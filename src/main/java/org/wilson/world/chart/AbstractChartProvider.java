package org.wilson.world.chart;

import org.wilson.world.java.JavaExtensible;

@JavaExtensible(description = "Generic chart provider", name = "system.chart")
public abstract class AbstractChartProvider implements ChartProvider {
	private int id;
	private String name;
	private ChartType type;
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public ChartType getChartType() {
		return this.type;
	}
	
	protected void setChartType(ChartType type) {
		this.type = type;
	}

}
