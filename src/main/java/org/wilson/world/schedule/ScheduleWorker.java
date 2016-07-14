package org.wilson.world.schedule;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ScheduleManager;

public class ScheduleWorker implements Runnable{
    private static final Logger logger = Logger.getLogger(ScheduleWorker.class);
    
    private volatile boolean stopped;
    
    public boolean isStopped() {
        return this.stopped;
    }
    
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    
    @Override
    public void run() {
        logger.info("Schedule worker is ready to execute jobs.");
        while(!this.isStopped()) {
            List<ScheduledJob> jobs = ScheduleManager.getInstance().getJobs();
            for(ScheduledJob job : jobs) {
                Date next = job.getNextStartDate();
                if(next.getTime() < System.currentTimeMillis()) {
                    job.doJob();
                }
            }
            
            try {
                Thread.sleep(DefaultJob.HOUR_TIME);
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
    }

}
