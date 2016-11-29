package org.wilson.world.chart;

import java.util.HashMap;
import java.util.Map;

public class DemoChartProvider extends AbstractChartProvider {
	public DemoChartProvider() {
		this.setName("demo");
		this.setChartType(ChartType.Pie);
	}
	
	@Override
	public ChartData getChartData() {
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("Red", 0.5);
		map.put("Green", 0.5);
		PieChartData data = new PieChartData(this.getName(), "Color", map);
		return data;
	}

}
