package org.wilson.world.monitor;

import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.MonitorManager;

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
        while(!this.isStopped()) {
            try {
                List<MonitorParticipant> participants = MonitorManager.getInstance().getMonitorParticipants();
                for(MonitorParticipant participant : participants) {
                    boolean result = participant.doMonitor();
                    String name = participant.getName();
                    if(result) {
                        if(MonitorManager.getInstance().hasAlert(name)) {
                            MonitorManager.getInstance().removeAlert(name);
                        }
                    }
                    else {
                        String content = participant.getAlertMessage();
                        MonitorManager.getInstance().addAlert(name, content);
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
