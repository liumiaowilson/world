package org.wilson.world.usage;

import org.wilson.world.manager.ConsoleManager;
import org.wilson.world.model.Alert;
import org.wilson.world.monitor.MonitorParticipant;

public class StorageUsageMonitor implements MonitorParticipant {
    private Alert alert;
    
    public StorageUsageMonitor() {
        this.alert = new Alert();
        this.alert.id = "Storage used too much";
        this.alert.message = "Too much storage has been used.";
        this.alert.url = "usage.jsp";
    }
    
    @Override
    public boolean isOK() {
        double [] ret = ConsoleManager.getInstance().getStorageUsageDisplay();
        if(ret[0] >= 80.0) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public Alert getAlert() {
        return alert;
    }

}
