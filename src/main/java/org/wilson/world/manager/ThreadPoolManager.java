package org.wilson.world.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class ThreadPoolManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(ThreadPoolManager.class);
    
    private static ThreadPoolManager instance;
    
    private ExecutorService executors = null;
    
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

    @Override
    public void start() {
        int size = ConfigManager.getInstance().getConfigAsInt("thread.pool.size", 5);
        this.executors = Executors.newFixedThreadPool(size);
        logger.info("Thread pool has been set up.");
    }

    @Override
    public void shutdown() {
        if(this.executors != null) {
            this.executors.shutdown();
            while(!this.executors.isTerminated()) {
            }
            logger.info("Thread pool has been shut down.");
        }
    }
}
