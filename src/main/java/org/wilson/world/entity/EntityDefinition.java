package org.wilson.world.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityDefinition {
	public int id;
	
	public String name;
	
	public Map<String, EntityProperty> properties = new HashMap<String, EntityProperty>();
}
