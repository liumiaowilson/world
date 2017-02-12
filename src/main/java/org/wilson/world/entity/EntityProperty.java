package org.wilson.world.entity;

import java.util.HashMap;
import java.util.Map;

import org.wilson.world.pagelet.FieldInfo;

public class EntityProperty {
	public String name;
	
	public String type;
	public String label;
	public String field;
	public boolean index;
	
	public Map<String, Object> data = new HashMap<String, Object>();
	
	public FieldInfo toFieldInfo() {
		FieldInfo info = new FieldInfo();
		info.name = name;
		info.type = field;
		info.label = label;
		info.data = data;
		
		return info;
	}
}
