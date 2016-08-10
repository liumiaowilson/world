package org.wilson.world.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.manager.ThreadPoolManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.util.TimeUtils;

public class WebJobWorker implements Runnable {
    private static final Logger logger = Logger.getLogger(WebJobWorker.class);
    
    private boolean firstTime = true;
    
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
            List<WebJob> jobs = WebManager.getInstance().getJobs();
            Collections.sort(jobs, new Comparator<WebJob>(){

                @Override
                public int compare(WebJob o1, WebJob o2) {
                    return -(o1.getId() - o2.getId());
                }
                
            });
            for(final WebJob job : jobs) {
                String status = WebManager.getInstance().getJobStatus(job);
                if(WebJobStatus.Disabled.name().equals(status)) {
                    continue;
                }
                int period = job.getPeriod();
                final long now = System.currentTimeMillis();
                long last = WebManager.getInstance().getLastTime(job);
                if(last > 0) {
                    if(last + TimeUtils.HOUR_DURATION * period > now) {
                        if(!firstTime) {
                            continue;
                        }
                    }
                }
                
                ThreadPoolManager.getInstance().execute(new Runnable(){

                    @Override
                    public void run() {
                        WebManager.getInstance().run(job, now);
                    }
                    
                });
            }
            
            this.firstTime = false;
            
            try {
                Thread.sleep(TimeUtils.HOUR_DURATION);
            }
            catch(Exception e) {
                logger.error(e);
            }
        }
    }

}
