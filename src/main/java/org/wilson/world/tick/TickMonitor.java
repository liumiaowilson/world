package org.wilson.world.tick;

public interface TickMonitor {
    public void send(TickMessage message);
    
    public void setEnded(boolean ended);
    
    public boolean isEnded();
    
    public void clear();
    
    public void addTickMonitorListener(TickMonitorListener listener);
}
