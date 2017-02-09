package org.wilson.world.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.model.Entity;

import net.sf.json.JSONObject;

public class EntityDefinition {
	
	public int id;
	
	public String name;
	
	public LinkedHashMap<String, EntityProperty> properties = new LinkedHashMap<String, EntityProperty>();
	
	public Entity toEntity(JSONObject obj) {
		if(obj == null) {
			return null;
		}
		
		Entity entity = new Entity();
		if(obj.containsKey("_id")) {
			entity.id = obj.getInt("_id");
		}
		if(obj.containsKey("name")) {
			entity.name = obj.getString("name");
		}
		entity.type = name;
		for(EntityProperty property : properties.values()) {
			if(obj.containsKey(property.name)) {
				entity.data.put(property.name, obj.getString(property.name));
			}
		}
		
		return entity;
	}
	
	public JSONObject fromEntity(Entity entity) {
		if(entity == null) {
			return null;
		}
		
		JSONObject obj = new JSONObject();
		for(EntityProperty property : properties.values()) {
			obj.put(property.name, entity.get(property.name));
		}
		obj.put("_id", entity.id);
		obj.put("name", entity.name);
		
		return obj;
	}
	
	public Entity toIndexEntity(JSONObject obj) {
		if(obj == null) {
			return null;
		}
		
		Entity entity = new Entity();
		if(obj.containsKey("_id")) {
			entity.id = obj.getInt("_id");
		}
		if(obj.containsKey("name")) {
			entity.name = obj.getString("name");
		}
		entity.type = name;
		for(EntityProperty property : this.getIndexedProperties()) {
			if(obj.containsKey(property.name)) {
				entity.data.put(property.name, obj.getString(property.name));
			}
		}
		
		return entity;
	}
	
	public JSONObject fromIndexEntity(Entity entity) {
		if(entity == null) {
			return null;
		}
		
		JSONObject obj = new JSONObject();
		for(EntityProperty property : this.getIndexedProperties()) {
			obj.put(property.name, entity.get(property.name));
		}
		obj.put("_id", entity.id);
		obj.put("name", entity.name);
		
		return obj;
	}
	
	public List<EntityProperty> getIndexedProperties() {
		List<EntityProperty> ret = new ArrayList<EntityProperty>();
		for(EntityProperty property : properties.values()) {
			if(property.index) {
				ret.add(property);
			}
		}
		
		return ret;
	}
	
	public EntityProperty getProperty(String name) {
		if(StringUtils.isBlank(name)) {
			return null;
		}
		
		return this.properties.get(name);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entity: ").append(name).append("\n");
		for(EntityProperty property : properties.values()) {
			sb.append(property.name + "[" + property.type + "]\n");
		}
		
		return sb.toString();
	}
}
