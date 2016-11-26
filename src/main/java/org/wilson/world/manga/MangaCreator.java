package org.wilson.world.manga;

import org.wilson.world.java.JavaExtensible;

/**
 * Create a manga
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Create a manga", name = "manga.creator")
public interface MangaCreator {
	/**
	 * Get a unique name of this creator
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Describe what parameters are wanted
	 * 
	 * @return
	 */
	public String getParametersHint();
	
	/**
	 * Create a manga from parameters.
	 * 
	 * @param parameters
	 */
	public void create(String parameters);
}
