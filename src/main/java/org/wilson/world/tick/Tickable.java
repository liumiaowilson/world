package org.wilson.world.tick;

public interface Tickable {
    public int getTurnId();
    
    public void setTurnId(int turnId);
    
    public int tick(int stepId, TickMonitor monitor);
    
    public Object getInfo();
}
