package org.wilson.world.chart;

import java.util.Map;

public class PieChartData implements ChartData {
	private String name;
	private String title;
	private Map<String, Double> data;
	
	public PieChartData(String name, String title, Map<String, Double> data) {
		this.name = name;
		this.title = title;
		this.data = data;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public Object getData() {
		return this.data;
	}

}
