package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

public class MicroExpression {
    public int id;
    
    public String name;
    
    public String [] definition;
    
    public List<String> examples = new ArrayList<String>();
    
    public String getDefinition() {
        StringBuilder sb = new StringBuilder();
        for(String def : definition) {
            sb.append(def);
        }
        
        return sb.toString();
    }
}
