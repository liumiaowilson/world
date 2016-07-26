package org.wilson.world.tick;

public interface Tickable {
    public int getTurnId();
    
    public void setTurnId(int turnId);
    
    public int tick(TickMonitor monitor);
    
    public Object getInfo();
}
