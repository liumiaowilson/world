package org.wilson.world.entity;

import java.util.LinkedHashMap;

public class EntityDefinition {
	public int id;
	
	public String name;
	
	public LinkedHashMap<String, EntityProperty> properties = new LinkedHashMap<String, EntityProperty>();
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entity: ").append(name).append("\n");
		for(EntityProperty property : properties.values()) {
			sb.append(property.name + "[" + property.type + "]\n");
		}
		
		return sb.toString();
	}
}
