package org.wilson.world.chart;

/**
 * Build the chart
 * 
 * @author mialiu
 *
 */
public interface ChartProvider {
	/**
	 * Get the id of the provider
	 * 
	 * @return
	 */
	public int getId();
	
	/**
	 * Set the id of the provider
	 * 
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * Get the name of the provider
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the type of the chart
	 * 
	 * @return
	 */
	public ChartType getChartType();
	
	/**
	 * Get the chart data
	 * 
	 * @return
	 */
	public ChartData getChartData();
}
