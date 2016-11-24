package org.wilson.world.java;

/**
 * Lifecycle listener for java class
 * 
 * @author mialiu
 *
 */
public interface JavaClassListener {
	/**
	 * Called when a java class is created
	 * 
	 * @param javaClass
	 */
	public void created(JavaClass javaClass);
	
	/**
	 * Called when a java class is removed
	 * 
	 * @param javaClass
	 */
	public void removed(JavaClass javaClass);
}
