package org.wilson.world.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtensionPoint {
    public String name;
    
    public String description;
    
    public List<String> paramNames = new ArrayList<String>();
    
    @SuppressWarnings("rawtypes")
    public Map<String, Class> params = new HashMap<String, Class>();
    
    @SuppressWarnings("rawtypes")
    public Class returnType;
    
    //for display only
    public boolean marked;
}
