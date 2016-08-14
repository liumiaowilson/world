package org.wilson.world.web;

import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.manager.WebManager;

public class DefaultWebJobMonitor implements WebJobMonitor {
    private static final Logger logger = Logger.getLogger(DefaultWebJobMonitor.class);
    
    private WebJob job;
    
    public WebJob getJob() {
        return job;
    }

    public void setJob(WebJob job) {
        this.job = job;
    }

    private int getPercentage(int current, int total) {
        return current * 100 / total;
    }
    
    private int getPercentage(WebJobProgress progress) {
        return this.getPercentage(progress.current, progress.total);
    }
    
    private WebJobProgress getProgress() {
        Map<Integer, WebJobProgress> progresses = WebManager.getInstance().getWebJobProgresses();
        WebJobProgress progress = progresses.get(this.job.getId());
        return progress;
    }
    
    private void setProgress(WebJobProgress progress) {
        Map<Integer, WebJobProgress> progresses = WebManager.getInstance().getWebJobProgresses();
        progresses.put(this.job.getId(), progress);
    }
    
    @Override
    public void start(int totalSteps) {
        if(totalSteps <= 0) {
            return;
        }
        
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            progress = new WebJobProgress();
            this.setProgress(progress);
        }
        
        progress.status = WebJobProgressStatus.InProgress;
        progress.total = totalSteps;
        progress.current = 0;
        progress.percentage = this.getPercentage(progress);
        progress.startTime = System.currentTimeMillis();
        
        if(logger.isTraceEnabled()) {
            logger.trace(this.job.getName() + " start");
        }
    }

    @Override
    public void progress(int steps) {
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            return;
        }
        
        progress.status = WebJobProgressStatus.InProgress;
        progress.current += steps;
        if(progress.current > progress.total) {
            progress.current = progress.total;
        }
        progress.percentage = this.getPercentage(progress);
        
        if(logger.isTraceEnabled()) {
            logger.trace(this.job.getName() + " progress");
        }
    }

    @Override
    public void succeed() {
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            return;
        }
        
        if(!WebJobProgressStatus.Stopped.equals(progress.status)) {
            progress.status = WebJobProgressStatus.Successful;
        }
        
        progress.endTime = System.currentTimeMillis();
        
        if(logger.isTraceEnabled()) {
            logger.trace(this.job.getName() + " succeed");
        }
    }

    @Override
    public void fail() {
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            return;
        }
        
        progress.status = WebJobProgressStatus.Failed;
        progress.endTime = System.currentTimeMillis();
        
        if(logger.isTraceEnabled()) {
            logger.trace(this.job.getName() + " fail");
        }
    }
    
    @Override
    public void stop() {
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            return;
        }
        
        progress.status = WebJobProgressStatus.Stopped;
        progress.stopRequired = false;
        progress.endTime = System.currentTimeMillis();
        
        if(logger.isTraceEnabled()) {
            logger.trace(this.job.getName() + " stop");
        }
    }

    @Override
    public boolean isStopRequired() {
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            return false;
        }
        
        return progress.stopRequired;
    }

    @Override
    public void adjust(int adjustment) {
        WebJobProgress progress = this.getProgress();
        if(progress == null) {
            return;
        }
        
        progress.total += adjustment;
        progress.percentage = this.getPercentage(progress);
        
        if(logger.isTraceEnabled()) {
            logger.trace(this.job.getName() + " adjust");
        }
    }

}
