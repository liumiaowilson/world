package org.wilson.world.monitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.MonitorManager;
import org.wilson.world.model.Alert;

public class MonitorWorker implements Runnable {
    private static final Logger logger = Logger.getLogger(MonitorWorker.class);
    private volatile boolean stopped;
    private long interval;
    
    public MonitorWorker(long interval) {
        this.interval = interval;
    }
    
    public void setStopped(boolean flag) {
        this.stopped = flag;
    }
    
    public boolean isStopped() {
        return this.stopped;
    }
    
    @Override
    public void run() {
        logger.info("Monitor worker is ready to start monitoring.");
        while(!this.isStopped()) {
            try {
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
                
                Thread.sleep(interval);
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
    }

}
