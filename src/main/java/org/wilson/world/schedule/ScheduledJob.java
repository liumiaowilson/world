package org.wilson.world.schedule;

import java.util.Date;

public interface ScheduledJob {
    public String getJobName();
    
    public void doJob();
    
    public Date getLastRunDate();
    
    public Date getNextStartDate();
}
