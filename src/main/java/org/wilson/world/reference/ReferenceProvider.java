package org.wilson.world.reference;

import org.wilson.world.java.JavaExtensible;

/**
 * Base for reference provider
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Extensible reference provider", name = "system.reference")
public interface ReferenceProvider {

	/**
	 * Get the name of the reference provider;
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the reference mapped by the key
	 * 
	 * @param key
	 * @return
	 */
	public Object getReference(String key);
	
	/**
	 * Called when initiated
	 * 
	 */
	public void init();
	
	/**
	 * Called when destroyed
	 * 
	 */
	public void destroy();
}
