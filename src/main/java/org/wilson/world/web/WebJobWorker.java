package org.wilson.world.web;

import org.apache.log4j.Logger;
import org.wilson.world.manager.HopperDataManager;
import org.wilson.world.manager.WebManager;
import org.wilson.world.model.HopperData;
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
                long now = System.currentTimeMillis();
                HopperData data = HopperDataManager.getInstance().getHopperDataByHopperId(job.getId());
                int period = job.getPeriod();
                if(data != null) {
                    long last = data.lastTime;
                    if(last + TimeUtils.HOUR_DURATION * period > now) {
                        continue;
                    }
                }
                try {
                    job.run();
                    if(data == null) {
                        data = new HopperData();
                        data.hopperId = job.getId();
                        data.status = WebJobStatus.Active.name();
                        data.failCount = 0;
                        data.lastTime = now;
                        HopperDataManager.getInstance().createHopperData(data);
                    }
                    else {
                        data.status = WebJobStatus.Active.name();
                        data.failCount = 0;
                        data.lastTime = now;
                        HopperDataManager.getInstance().updateHopperData(data);
                    }
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                    
                    if(data == null) {
                        data = new HopperData();
                        data.hopperId = job.getId();
                        data.status = WebJobStatus.Inactive.name();
                        data.failCount = 1;
                        data.lastTime = now;
                        HopperDataManager.getInstance().createHopperData(data);
                    }
                    else {
                        data.status = WebJobStatus.Inactive.name();
                        data.failCount += 1;
                        data.lastTime = now;
                        if(data.failCount >= 3) {
                            data.status = WebJobStatus.Error.name();
                        }
                        HopperDataManager.getInstance().updateHopperData(data);
                    }
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
