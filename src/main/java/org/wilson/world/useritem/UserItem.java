package org.wilson.world.useritem;

public interface UserItem {
    public void setId(int id);
    
    public int getId();
    
    public String getName();
    
    public String getType();
    
    public String getDescription();
    
    public void takeEffect();
    
    public int getValue();
}
