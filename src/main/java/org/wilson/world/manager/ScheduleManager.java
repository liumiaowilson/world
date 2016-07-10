package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.schedule.ScheduleWorker;
import org.wilson.world.schedule.ScheduledJob;

public class ScheduleManager implements ManagerLifecycle{
    private static final Logger logger = Logger.getLogger(ScheduleManager.class);
    
    private static ScheduleManager instance;
    
    private List<ScheduledJob> jobs = new ArrayList<ScheduledJob>();
    private ScheduleWorker worker = null;
    private Thread workerThread = null;
    
    private ScheduleManager() {
        
    }
    
    public static ScheduleManager getInstance() {
        if(instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    @Override
    public void start() {
        worker = new ScheduleWorker();
        workerThread = new Thread(worker);
        workerThread.start();
    }

    @Override
    public void shutdown() {
        if(worker != null) {
            worker.setStopped(true);
        }
        try {
            if(workerThread != null) {
                workerThread.interrupt();
                workerThread.join();
            }
        }
        catch(Exception e) {
            logger.error(e);
        }
    }
    
    public List<ScheduledJob> getJobs() {
        return this.jobs;
    }
    
    public void addJob(ScheduledJob job) {
        if(job != null) {
            this.jobs.add(job);
        }
    }
    
    public void removeJob(ScheduledJob job) {
        if(job != null) {
            this.jobs.remove(job);
        }
    }
}
