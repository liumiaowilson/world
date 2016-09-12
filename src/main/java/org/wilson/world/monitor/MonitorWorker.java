package org.wilson.world.monitor;

import java.util.List;

import org.wilson.world.manager.MonitorManager;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.model.Alert;
import org.wilson.world.thread.DefaultWorker;

public class MonitorWorker extends DefaultWorker {
    
    public MonitorWorker(long interval) {
        this.setPeriodTime(interval);
        
        ThreadPoolManager.getInstance().addWorker(this);
    }

    @Override
    public void work() throws Exception {
        List<MonitorParticipant> participants = MonitorManager.getInstance().getMonitorParticipants();
        for(MonitorParticipant participant : participants) {
            boolean result = participant.isOK();
            Alert alert = participant.getAlert();
            if(result) {
                if(MonitorManager.getInstance().hasAlert(alert.id)) {
                    MonitorManager.getInstance().removeAlert(alert.id);
                }
            }
            else {
                MonitorManager.getInstance().addAlert(alert);
            }
        }
    }

}
