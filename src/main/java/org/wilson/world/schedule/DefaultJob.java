package org.wilson.world.schedule;

import java.util.Date;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;

public abstract class DefaultJob implements ScheduledJob{
    private static final Logger logger = Logger.getLogger(DefaultJob.class);
    public static final long HOUR_TIME = 60 * 60 * 1000L;
    private long lastRunTime = -1;
    
    @Override
    public String getJobName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void doJob() {
        logger.info("Job [" + this.getJobName() + "] started.");
        
        this.execute();
        
        this.lastRunTime = System.currentTimeMillis();
    }

    @Override
    public boolean canStart(Date date) {
        if(this.lastRunTime < 0) {
            return true;
        }
        long time = date.getTime();
        int hrs = ConfigManager.getInstance().getConfigAsInt("job." + this.getJobName() + ".interval.hrs", 1);
        if(time > hrs * HOUR_TIME + this.lastRunTime) {
            return true;
        }
        else {
            return false;
        }
    }

    public abstract void execute();
}
