package org.wilson.world.monitor;

import org.wilson.world.java.JavaExtensible;
import org.wilson.world.model.Alert;

@JavaExtensible(description = "Monitors to send alerts", name = "system.monitor")
public interface MonitorParticipant {
    public boolean isOK();
    
    public Alert getAlert();
}
