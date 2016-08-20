package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

public class Checklist {
    public int id;
    
    public int defId;
    
    public String name;
    
    public String progress;
    
    public List<Integer> checked = new ArrayList<Integer>();
}
