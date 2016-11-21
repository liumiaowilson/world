package org.wilson.world.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class NovelRole {
    public int id;
    
    public String name;
    
    public String description;
    
    public String definition;
    
    public String image;
    
    //for UI display
    public String display;
    
    //for common access
    //should not update this
    public Map<String, String> variables = new HashMap<String, String>();
    
    public String get(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	return this.variables.get(name);
    }
    
    public boolean is(String name) {
    	if(StringUtils.isBlank(name)) {
    		return false;
    	}
    	
    	if(this.variables.containsKey(name)) {
    		Boolean b = Boolean.valueOf(this.variables.get(name));
    		if(b != null) {
    			return b;
    		}
    	}
    	
    	return false;
    }
}
