package org.wilson.world.graph;

import org.apache.commons.lang.StringUtils;

public class Node {
	public static final String DEFAULT_COLOR = "#11479e";
	
    public String id;
    
    public String name;
    
    public String color;
    
    public String getColor() {
    	if(StringUtils.isNotBlank(color)) {
    		return color;
    	}
    	else {
    		return DEFAULT_COLOR;
    	}
    }
}
