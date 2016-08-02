package org.wilson.world.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.web.WebJob;
import org.wilson.world.web.WebJobWorker;
import org.wilson.world.web.WordOfTheDayJob;

public class WebManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(WebManager.class);
    
    private static WebManager instance;
    
    private List<WebJob> jobs = new ArrayList<WebJob>();
    
    private WebJobWorker worker = null;
    private Thread workerThread = null;
    
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private WebManager() {
        this.loadJobs();
    }
    
    private void loadJobs() {
        this.addWebJob(new WordOfTheDayJob());
    }
    
    public static WebManager getInstance() {
        if(instance == null) {
            instance = new WebManager();
        }
        return instance;
    }
    
    public void addWebJob(WebJob job) {
        if(job != null) {
            this.jobs.add(job);
        }
    }
    
    public void removeWebJob(WebJob job) {
        if(job != null) {
            this.jobs.remove(job);
        }
    }
    
    public List<WebJob> getJobs() {
        return this.jobs;
    }

    @Override
    public void start() {
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
