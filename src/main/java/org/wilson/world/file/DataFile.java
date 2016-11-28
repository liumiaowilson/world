package org.wilson.world.file;

/**
 * Represent data file of different implementations
 * 
 * @author mialiu
 *
 */
public interface DataFile {
	/**
	 * The name of the data file
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the content of the data file
	 * 
	 * @return
	 */
	public String getContent();
}
