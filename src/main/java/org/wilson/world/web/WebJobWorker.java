package org.wilson.world.web;

import org.apache.log4j.Logger;
import org.wilson.world.manager.WebManager;
import org.wilson.world.util.TimeUtils;

public class WebJobWorker implements Runnable {
    private static final Logger logger = Logger.getLogger(WebJobWorker.class);
    
    public WebJobWorker() {
    }
    
    private volatile boolean stopped;
    
    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public void run() {
        logger.info("Web job worker is ready to execute jobs.");
        while(!this.isStopped()) {
            for(WebJob job : WebManager.getInstance().getJobs()) {
                try {
                    job.run();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            
            try {
                Thread.sleep(TimeUtils.HOUR_DURATION);
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
    }

}
