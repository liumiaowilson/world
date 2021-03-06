package org.wilson.world.java;

/**
 * This is a mark interface, used to create an instance of a class proactively.
 * 
 * @author mialiu
 *
 */
@JavaExtensible(description = "Active objects to be used in javascript", name = "js.activeobject")
public interface ActiveObject {
	/**
	 * Get the unique name of the object
	 * 
	 * @return
	 */
	public String getName();
}
