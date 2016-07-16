package org.wilson.world.model;

public class Task {
    public int id;
    
    public String name;
    
    public String content;
    
    public long createdTime;
    
    public long modifiedTime;
    
    /**
     * Used for UI
     */
    public boolean marked;
    
    public boolean starred;
}
