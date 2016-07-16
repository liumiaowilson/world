package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    public int id;
    
    public String name;
    
    public String content;
    
    public long createdTime;
    
    public long modifiedTime;
    
    public List<TaskAttr> attrs = new ArrayList<TaskAttr>();
    
    /**
     * Used for UI
     */
    public boolean marked;
    
    public boolean starred;
}
