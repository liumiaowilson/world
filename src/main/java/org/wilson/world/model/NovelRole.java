package org.wilson.world.model;

import java.util.HashMap;
import java.util.Map;

public class NovelRole {
    public int id;
    
    public String name;
    
    public String description;
    
    public String definition;
    
    //for UI display
    public String display;
    
    //for common access
    //should not update this
    public Map<String, String> variables = new HashMap<String, String>();
}
