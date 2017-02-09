package org.wilson.world.model;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    public int id;
    
    public String name;
    
    public String type;
    
    public Map<String, String> data = new HashMap<String, String>();
    
    public void set(String name, String value) {
    	data.put(name, value);
    }
    
    public String get(String name) {
    	return data.get(name);
    }
}
