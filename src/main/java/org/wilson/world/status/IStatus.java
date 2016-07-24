package org.wilson.world.status;

public interface IStatus {
    public int getID();
    
    public String getName();
    
    public String getDescription();
    
    public String getIcon();
    
    public void activate();
    
    public void deactivate();
}
