package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

public class Action {
    public int id;
    
    public String name;
    
    public String script;
    
    public List<ActionParam> params = new ArrayList<ActionParam>();
    
    //display only
    public boolean marked;
}
