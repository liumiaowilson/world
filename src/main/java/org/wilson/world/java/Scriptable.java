package org.wilson.world.java;

/**
 * Mark that the class can handle script objects
 * 
 * @author mialiu
 *
 */
public interface Scriptable {

	/**
	 * Inject the script object
	 * 
	 * @param script
	 */
	public void setScript(Script script);
}
