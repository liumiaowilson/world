package org.wilson.world.monitor;

import org.wilson.world.model.Alert;

public interface MonitorParticipant {
    public boolean doMonitor();
    
    public Alert getAlert();
}
