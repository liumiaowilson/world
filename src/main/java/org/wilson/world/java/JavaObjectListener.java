package org.wilson.world.java;

/**
 * Lifecycle listener for java object
 * 
 * @author mialiu
 *
 */
public interface JavaObjectListener {
	/**
	 * Called when a java object is created
	 * 
	 * @param javaObject
	 */
	public void created(JavaObject javaObject);
	
	/**
	 * Called when a java object is removed
	 * 
	 * @param javaObject
	 */
	public void removed(JavaObject javaObject);
}
