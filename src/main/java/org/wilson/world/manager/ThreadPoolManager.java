package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.wilson.world.thread.Worker;
import org.wilson.world.thread.WorkerInfo;
import org.wilson.world.util.FormatUtils;

public class ThreadPoolManager {
    private static final Logger logger = Logger.getLogger(ThreadPoolManager.class);
    
    private static ThreadPoolManager instance;
    
    private ExecutorService executors = null;
    
    private List<Worker> workers = new ArrayList<Worker>();
    
    private ThreadPoolManager() {
        
    }
    
    public static ThreadPoolManager getInstance() {
        if(instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }
    
    public void execute(Runnable command) {
        this.executors.execute(command);
    }

    public void start() {
        int size = ConfigManager.getInstance().getConfigAsInt("thread.pool.size", 5);
        this.executors = Executors.newFixedThreadPool(size);
        logger.info("Thread pool has been set up.");
    }

    public void shutdown() {
        if(this.executors != null) {
            this.executors.shutdown();
            while(!this.executors.isTerminated()) {
            }
            logger.info("Thread pool has been shut down.");
        }
    }
    
    public List<Worker> getWorkers() {
        return this.workers;
    }
    
    public void addWorker(Worker worker) {
        if(worker != null) {
            this.workers.add(worker);
        }
    }
    
    public void removeWorker(Worker worker) {
        if(worker != null) {
            this.workers.remove(worker);
        }
    }
    
    public List<WorkerInfo> getWorkerInfos() {
        List<WorkerInfo> ret = new ArrayList<WorkerInfo>();
        
        for(Worker worker : this.workers) {
            WorkerInfo info = new WorkerInfo();
            info.name = worker.getClass().getSimpleName();
            info.periods = worker.getPeriods();
            if(info.periods == 0) {
                continue;
            }
            info.timePerPeriod = worker.getTotalTime() / info.periods;
            info.workingTimePerPeriod = worker.getWorkingTime() / info.periods;
            info.workingPercent = FormatUtils.getRoundedValue(info.workingTimePerPeriod * 100.0 / info.timePerPeriod);
            ret.add(info);
        }
        
        return ret;
    }
}
