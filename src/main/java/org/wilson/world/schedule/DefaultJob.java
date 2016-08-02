package org.wilson.world.schedule;

import java.util.Date;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ConfigManager;
import org.wilson.world.manager.DataManager;
import org.wilson.world.util.TimeUtils;

public abstract class DefaultJob implements ScheduledJob{
    private static final Logger logger = Logger.getLogger(DefaultJob.class);
    private long lastRunTime = -1;
    private static final String SUFFIX_LAST_RUN = "_last";
    
    @Override
    public String getJobName() {
        String name = this.getClass().getSimpleName();
        int len = 20 - SUFFIX_LAST_RUN.length();
        if(name.length() > len) {
            name = name.substring(0, len);
        }
        return name;
    }

    @Override
    public void doJob() {
        logger.info("Job [" + this.getJobName() + "] started.");
        
        this.execute();
        
        this.lastRunTime = System.currentTimeMillis();
        this.setLastRunTime(this.lastRunTime);
    }
    
    private long getLastRunTime() {
        return DataManager.getInstance().getValueAsLong(this.getJobName() + SUFFIX_LAST_RUN);
    }
    
    private void setLastRunTime(long time) {
        DataManager.getInstance().setValue(this.getJobName() + SUFFIX_LAST_RUN, time);
    }

    @Override
    public Date getNextStartDate() {
        this.lastRunTime = this.getLastRunTime();
        if(this.lastRunTime < 0) {
            return new Date();
        }
        
        int hrs = ConfigManager.getInstance().getConfigAsInt("job." + this.getJobName() + ".interval.hrs", 1);
        long time = hrs * TimeUtils.HOUR_DURATION + this.lastRunTime;
        return new Date(time);
    }

    @Override
    public Date getLastRunDate() {
        if(this.lastRunTime < 0) {
            return new Date();
        }
        else {
            return new Date(this.lastRunTime);
        }
    }

    public abstract void execute();
}
