package org.wilson.world.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Hopper;
import org.wilson.world.web.DefaultWebJob;
import org.wilson.world.web.WebJob;
import org.wilson.world.web.WebJobExecutor;
import org.wilson.world.web.WebJobWorker;
import org.wilson.world.web.WordOfTheDayJob;

public class WebManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(WebManager.class);
    
    private static WebManager instance;
    
    private WebJobWorker worker = null;
    private Thread workerThread = null;
    
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private Cache<Integer, WebJob> jobs = null;
    
    private static int GLOBAL_ID = 1;
    
    private WebManager() {
        this.jobs = new DefaultCache<Integer, WebJob>("web_manager_jobs", false);
    }
    
    private void loadSystemWebJobs() {
        GLOBAL_ID = 1;
        
        this.loadSystemWebJob(new WordOfTheDayJob());
    }
    
    private void loadSystemWebJob(WebJob job) {
        if(job != null) {
            job.setId(-GLOBAL_ID++);
            this.jobs.put(job.getId(), job);
        }
    }
    
    public static WebManager getInstance() {
        if(instance == null) {
            instance = new WebManager();
        }
        return instance;
    }
    
    public List<WebJob> getJobs() {
        return this.jobs.getAll();
    }
    
    @SuppressWarnings("rawtypes")
    private void loadHopper(Hopper hopper) {
        if(hopper == null) {
            return;
        }
        
        WebJobExecutor executor = null;
        String action = hopper.action;
        try {
            Class clazz = Class.forName(action);
            executor = (WebJobExecutor) clazz.newInstance();
            logger.info("Loaded hopper using class [" + action + "]");
        }
        catch(Exception e) {
            executor = (WebJobExecutor) ExtManager.getInstance().wrapAction(action, WebJobExecutor.class);
            if(executor == null) {
                logger.warn("Failed to load hopper using [" + action + "]");
                return;
            }
            else {
                logger.info("Loaded hopper using action [" + action + "]");
            }
        }
        
        if(executor != null) {
            DefaultWebJob job = new DefaultWebJob(hopper, executor);
            this.jobs.put(job.getId(), job);
        }
    }

    @Override
    public void start() {
        Cache<Integer, Hopper> cache = HopperManager.getInstance().getCache();
        cache.addCacheListener(new CacheListener<Hopper>(){

            @Override
            public void cachePut(Hopper old, Hopper v) {
                if(old != null) {
                    cacheDeleted(old);
                }
                
                loadHopper(v);
            }

            @Override
            public void cacheDeleted(Hopper v) {
                WebManager.this.jobs.delete(v.id);
            }

            @Override
            public void cacheLoaded(List<Hopper> all) {
                loadSystemWebJobs();
            }

            @Override
            public void cacheLoading(List<Hopper> old) {
                WebManager.this.jobs.clear();
            }
            
        });
        cache.notifyLoaded();
        
        worker = new WebJobWorker();
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
    
    public void put(String key, Object value) {
        this.data.put(key, value);
    }
    
    public Object get(String key) {
        return this.data.get(key);
    }
    
    public Document parse(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
