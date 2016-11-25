package org.wilson.world.java;

/**
 * Lifecycle listener for java extensions
 * 
 * @author mialiu
 *
 */
public interface JavaExtensionListener<T> {
	/**
	 * Get extension class
	 * 
	 * @return
	 */
	public Class<T> getExtensionClass();
	
	/**
	 * extension created
	 * 
	 * @param t
	 */
	public void created(T t);
	
	/**
	 * extension removed
	 * 
	 * @param t
	 */
	public void removed(T t);
}
