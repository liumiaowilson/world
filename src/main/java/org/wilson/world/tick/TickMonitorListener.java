package org.wilson.world.tick;

public interface TickMonitorListener {
    public void messageSent(TickMessage message);
    
    public void cleared();
}
