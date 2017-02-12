package org.wilson.world.pagelet;

import java.util.HashMap;
import java.util.Map;

public class FieldInfo {
	
	/**
	 * Name of the field
	 */
	public String name;
	
	/**
	 * Label of the field
	 */
	public String label;
	
	/**
	 * Type of the field
	 */
	public String type;
	
	/**
	 * Data of the field
	 */
	public Map<String, Object> data = new HashMap<String, Object>();
}
