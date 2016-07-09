package org.wilson.world.monitor;

public interface MonitorParticipant {
    public String getName();
    
    public boolean doMonitor();
    
    public String getAlertMessage();
}
