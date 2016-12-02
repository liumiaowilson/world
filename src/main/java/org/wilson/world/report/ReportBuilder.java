package org.wilson.world.report;

import org.wilson.world.java.JavaExtensible;

/**
 * Builder for the reports
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Generic report builder", name = "system.report")
public interface ReportBuilder {
	
	/**
	 * Set the id of the builder
	 * 
	 * @param id
	 */
	public void setId(int id);
	
	/**
	 * Get the id of the builder
	 * 
	 * @return
	 */
	public int getId();
	
	/**
	 * Get the name of the report builder
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Build the report data
	 * 
	 * @return
	 */
	public ReportData build();
}
