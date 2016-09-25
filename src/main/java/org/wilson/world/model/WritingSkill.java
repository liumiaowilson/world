package org.wilson.world.model;

import java.util.HashMap;
import java.util.Map;

public class WritingSkill {
    public int id;
    
    public String topic;
    
    public String name;
    
    public String definition;
    
    public Map<String, String> examples = new HashMap<String, String>();
}
