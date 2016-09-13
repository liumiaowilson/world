package org.wilson.world.thread;

import org.apache.log4j.Logger;
import org.wilson.world.util.TimeUtils;

public abstract class DefaultWorker implements Worker {
    private static final Logger logger = Logger.getLogger(DefaultWorker.class);
    
    private volatile boolean stopped;
    
    private int periods;
    private long startTime;
    private long workingTime;
    
    private long startWorkTime;
    
    private long periodTime;
    
    public long getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(long periodTime) {
        this.periodTime = periodTime;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    
    @Override
    public void run() {
        logger.info("[" + this.getClass().getName() + "] is starting to work.");
        this.startTime = System.currentTimeMillis();
        
        while(!this.isStopped()) {
            try {
                long spent = 0;
                
                try {
                    this.startWork();
                    
                    this.work();
                }
                catch(Exception e) {
                    logger.error(e);
                }
                finally {
                    spent = this.endWork();
                }
                
                long sleepTime = this.getPeriodTime() - spent;
                if(sleepTime > 0) {
                    if(sleepTime > TimeUtils.MINUTE_DURATION) {
                        long sum = 0;
                        while(!this.isStopped() && sum < sleepTime) {
                            Thread.sleep(TimeUtils.MINUTE_DURATION);
                            
                            sum += TimeUtils.MINUTE_DURATION;
                        }
                    }
                    else {
                        Thread.sleep(sleepTime);
                    }
                }
                
                this.periods++;
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
    }
    
    public void startWork() {
        this.startWorkTime = System.currentTimeMillis();
    }
    
    public long endWork() {
        long spent = System.currentTimeMillis() - this.startWorkTime;
        this.workingTime += spent;
        return spent;
    }

    @Override
    public long getPeriods() {
        return this.periods;
    }

    @Override
    public long getTotalTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    @Override
    public long getWorkingTime() {
        return this.workingTime;
    }

    public abstract void work() throws Exception;
}
