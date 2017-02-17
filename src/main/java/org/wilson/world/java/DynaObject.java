package org.wilson.world.java;

@JavaExtensible(description = "Extensible dynamic object", name = "system.dynaObject")
public interface DynaObject {

	/**
	 * Get the name of the dyna object
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the js wrapper object
	 * 
	 * @return
	 */
	public Object getJsObject();
}
