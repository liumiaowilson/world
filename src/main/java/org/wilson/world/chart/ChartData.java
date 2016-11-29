package org.wilson.world.chart;

/**
 * Data of the chart
 * 
 * @author mialiu
 *
 */
public interface ChartData {
	/**
	 * Get the name of the chart
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the title of the chart
	 * 
	 * @return
	 */
	public String getTitle();
	
	/**
	 * Get the data of the chart
	 * 
	 * @return
	 */
	public Object getData();
}
