package org.wilson.world.monitor;

import org.wilson.world.model.Alert;

public interface MonitorParticipant {
    public boolean isOK();
    
    public Alert getAlert();
}
