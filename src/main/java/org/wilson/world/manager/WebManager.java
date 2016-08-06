package org.wilson.world.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.image.ImageListJob;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.model.Hopper;
import org.wilson.world.model.HopperData;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.DefaultWebJob;
import org.wilson.world.web.NounsListJob;
import org.wilson.world.web.WebJob;
import org.wilson.world.web.WebJobExecutor;
import org.wilson.world.web.WebJobStatus;
import org.wilson.world.web.WebJobWorker;
import org.wilson.world.web.WordListJob;
import org.wilson.world.web.WordOfTheDayJob;

import net.sf.json.JSONObject;

public class WebManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(WebManager.class);
    
    private static WebManager instance;
    
    private WebJobWorker worker = null;
    private Thread workerThread = null;
    
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private Cache<Integer, WebJob> jobs = null;
    
    private static int GLOBAL_ID = 1;
    
    private int jsoupTimeout;
    
    private WebManager() {
        this.jobs = new DefaultCache<Integer, WebJob>("web_manager_jobs", false);
        
        this.jsoupTimeout = ConfigManager.getInstance().getConfigAsInt("web.jsoup.timeout", 30000);
    }
    
    private void loadSystemWebJobs() {
        GLOBAL_ID = 1;
        
        this.loadSystemWebJob(new WordOfTheDayJob());
        this.loadSystemWebJob(new WordListJob());
        this.loadSystemWebJob(new NounsListJob());
        this.loadSystemWebJob(new ImageListJob());
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
        return Jsoup.connect(url).timeout(this.jsoupTimeout).get();
    }
    
    public HopperData getHopperData(WebJob job) {
        if(job == null) {
            return null;
        }
        HopperData data = HopperDataManager.getInstance().getHopperDataByHopperId(job.getId());
        return data;
    }
    
    public String getJobStatus(WebJob job) {
        HopperData data = this.getHopperData(job);
        if(data == null) {
            return WebJobStatus.Enabled.name();
        }
        else {
            return data.status;
        }
    }
    
    public WebJob getWebJob(int id) {
        return this.jobs.get(id);
    }
    
    public int getFailCount(WebJob job) {
        HopperData data = this.getHopperData(job);
        if(data == null) {
            return 0;
        }
        else {
            return data.failCount;
        }
    }
    
    public long getLastTime(WebJob job) {
        HopperData data = this.getHopperData(job);
        if(data == null) {
            return -1;
        }
        else {
            return data.lastTime;
        }
    }
    
    private void syncJobStatus(HopperData data) {
        if(data.failCount >= 3) {
            data.status = WebJobStatus.Error.name();
        }
        else if(data.failCount > 0) {
            data.status = WebJobStatus.Inactive.name();
        }
        else {
            data.status = WebJobStatus.Active.name();
        }
    }
    
    public void setWebJob(WebJob job, int failCount, long lastTime) {
        if(job == null) {
            return;
        }
        HopperData data = this.getHopperData(job);
        if(data == null) {
            data = new HopperData();
            data.hopperId = job.getId();
            data.failCount = failCount;
            data.lastTime = lastTime;
            this.syncJobStatus(data);
            HopperDataManager.getInstance().createHopperData(data);
        }
        else {
            data.failCount = failCount;
            data.lastTime = lastTime;
            this.syncJobStatus(data);
            HopperDataManager.getInstance().updateHopperData(data);
        }
    }
    
    public String getNextRunTime(WebJob job, TimeZone tz) {
        if(job == null) {
            return null;
        }
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        int period = job.getPeriod();
        HopperData data = this.getHopperData(job);
        long now = System.currentTimeMillis();
        long next;
        if(data == null) {
            next = now;
        }
        else {
            next = now + period * TimeUtils.HOUR_DURATION;
        }
        
        return TimeUtils.toDateTimeString(next, tz);
    }
    
    public void setJobStatus(WebJob job, String status) {
        if(job == null) {
            return;
        }
        
        HopperData data = this.getHopperData(job);
        if(data == null) {
            data = new HopperData();
            data.hopperId = job.getId();
            data.status = status;
            data.failCount = 0;
            data.lastTime = -1;
            HopperDataManager.getInstance().createHopperData(data);
        }
        else {
            data.status = status;
            HopperDataManager.getInstance().updateHopperData(data);
        }
    }
    
    public void enableJob(WebJob job) {
        if(job != null) {
            this.setJobStatus(job, WebJobStatus.Enabled.name());
        }
    }
    
    public void disableJob(WebJob job) {
        if(job != null) {
            this.setJobStatus(job, WebJobStatus.Disabled.name());
        }
    }
    
    public boolean isEnabled(WebJob job) {
        return !WebJobStatus.Disabled.name().equals(this.getJobStatus(job));
    }
    
    public void debug(final WebJob job) {
        if(job == null) {
            return;
        }
        
        ThreadPoolManager.getInstance().execute(new Runnable(){

            @Override
            public void run() {
                try {
                    job.run();
                } catch (Exception e) {
                    logger.error(e);
                }
            }
            
        });
    }
    
    public String parseJSON(String url) throws IOException{
        Connection con = HttpConnection.connect(url).timeout(this.jsoupTimeout);
        con.method(Method.GET).ignoreContentType(true);
        Response resp = con.execute();
        String body = resp.body();
        return body;
    }
    
    public JSONObject toJSONObject(String json) {
        return JSONObject.fromObject(json);
    }
}
