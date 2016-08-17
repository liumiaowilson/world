package org.wilson.world.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.wilson.world.beauty.BeautyListJob;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.clip.ClipDownloadJob;
import org.wilson.world.clip.ClipListJob;
import org.wilson.world.fashion.FashionListJob;
import org.wilson.world.feed.FeedJob;
import org.wilson.world.image.ImageListJob;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.manga.MangaDownloadJob;
import org.wilson.world.manga.MangaListJob;
import org.wilson.world.model.Feed;
import org.wilson.world.model.Hopper;
import org.wilson.world.model.HopperData;
import org.wilson.world.novel.Novel1000Job;
import org.wilson.world.porn.JapanPornList2Job;
import org.wilson.world.porn.JapanPornListJob;
import org.wilson.world.porn.PornListJob;
import org.wilson.world.storage.StorageSyncJob;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.ArticleListJob;
import org.wilson.world.web.ArticleLoadJob;
import org.wilson.world.web.DefaultWebJob;
import org.wilson.world.web.DefaultWebJobMonitor;
import org.wilson.world.web.NounsListJob;
import org.wilson.world.web.QuoteOfTheDayJob;
import org.wilson.world.web.WebJob;
import org.wilson.world.web.WebJobExecutor;
import org.wilson.world.web.WebJobProgress;
import org.wilson.world.web.WebJobProgressStatus;
import org.wilson.world.web.WebJobStatus;
import org.wilson.world.web.WebJobStatusMonitor;
import org.wilson.world.web.WebJobWorker;
import org.wilson.world.web.WordInfo;
import org.wilson.world.web.WordListJob;
import org.wilson.world.web.WordLookupJob;
import org.wilson.world.web.WordOfTheDayJob;

import net.sf.json.JSONObject;

public class WebManager implements ManagerLifecycle {
    private static final Logger logger = Logger.getLogger(WebManager.class);
    
    private static WebManager instance;
    
    private WebJobWorker worker = null;
    private Thread workerThread = null;
    
    private Map<String, Object> data = new ConcurrentHashMap<String, Object>();
    
    private Cache<Integer, WebJob> jobs = null;
    
    private Map<Integer, WebJobProgress> jobProgresses = new ConcurrentHashMap<Integer, WebJobProgress>();
    
    private static int GLOBAL_ID = 1;
    
    private static int GLOBAL_FEED_ID = 1000;
    
    private int jsoupTimeout;
    
    private Map<String, WordInfo> words = new HashMap<String, WordInfo>();
    
    private WebManager() {
        this.jobs = new DefaultCache<Integer, WebJob>("web_manager_jobs", false);
        
        this.jsoupTimeout = ConfigManager.getInstance().getConfigAsInt("web.jsoup.timeout", 30000);
        
        MonitorManager.getInstance().registerMonitorParticipant(new WebJobStatusMonitor());
    }
    
    private void loadSystemWebJobs() {
        GLOBAL_ID = 1;
        
        this.loadSystemWebJob(new WordOfTheDayJob());
        this.loadSystemWebJob(new WordListJob());
        this.loadSystemWebJob(new NounsListJob());
        this.loadSystemWebJob(new ImageListJob());
        this.loadSystemWebJob(new QuoteOfTheDayJob());
        this.loadSystemWebJob(new WordLookupJob());
        this.loadSystemWebJob(new ArticleListJob());
        this.loadSystemWebJob(new ArticleLoadJob());
        this.loadSystemWebJob(new BeautyListJob());
        this.loadSystemWebJob(new PornListJob());
        this.loadSystemWebJob(new MangaListJob());
        this.loadSystemWebJob(new MangaDownloadJob());
        this.loadSystemWebJob(new ClipListJob());
        this.loadSystemWebJob(new ClipDownloadJob());
        this.loadSystemWebJob(new JapanPornListJob());
        this.loadSystemWebJob(new FashionListJob());
        this.loadSystemWebJob(new JapanPornList2Job());
        this.loadSystemWebJob(new StorageSyncJob());
        this.loadSystemWebJob(new Novel1000Job());
        
        this.loadFeedWebJobs();
    }
    
    private void loadFeedWebJobs() {
        GLOBAL_FEED_ID = 1000;
        
        for(Feed feed : FeedManager.getInstance().getFeeds()) {
            this.loadFeedWebJob(feed);
        }
    }
    
    private void loadFeedWebJob(Feed feed) {
        if(feed != null) {
            FeedJob job = new FeedJob(feed);
            job.setId(-GLOBAL_FEED_ID++);
            this.jobs.put(job.getId(), job);
        }
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
        
        if(ConfigManager.getInstance().isInOffLineMode()) {
            MonitorManager.getInstance().addAlert("OffLine Mode", "The system is in OFFLINE mode, and cannot connect to any website.");
        }
    }

    @Override
    public void shutdown() {
        for(WebJob job : this.getJobs()) {
            this.stop(job);
        }
        
        while(true) {
            boolean allStopped = true;
            for(WebJob job : this.getJobs()) {
                if(this.isWebJobInProgress(job)) {
                    allStopped = false;
                    break;
                }
            }
            
            if(allStopped) {
                break;
            }
            else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }
        
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
        if(value == null) {
            this.data.remove(key);
        }
        else {
            this.data.put(key, value);
        }
    }
    
    public Object get(String key) {
        return this.data.get(key);
    }
    
    public Document parse(String url) throws IOException {
        return this.getConnection(url).get();
    }
    
    public Document parse(String url, String userAgent) throws IOException {
        return this.getConnection(url, userAgent).get();
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
    
    public WebJob getAvailableWebJobByName(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        for(WebJob job : this.getJobs()) {
            if(name.equals(job.getName())) {
                if(!WebJobStatus.Disabled.name().equals(this.getJobStatus(job))) {
                    return job;
                }
            }
        }
        
        return null;
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
                WebManager.getInstance().run(job, true);
            }
            
        });
    }
    
    public void run(WebJob job) {
        this.run(job, System.currentTimeMillis());
    }
    
    public void run(WebJob job, boolean debug) {
        this.run(job, System.currentTimeMillis(), debug);
    }
    
    public void run(WebJob job, long now) {
        this.run(job, now, false);
    }
    
    public void run(WebJob job, long now, boolean debug) {
        if(job == null) {
            return;
        }
        
        if(ConfigManager.getInstance().isInOffLineMode()) {
            return;
        }
        
        WebJobProgress progress = this.jobProgresses.get(job.getId());
        if(progress != null) {
            if(WebJobProgressStatus.InProgress.equals(progress.status)) {
                //cannot run a job that is already running
                return;
            }
        }
        
        try {
            DefaultWebJobMonitor monitor = new DefaultWebJobMonitor();
            monitor.setJob(job);
            job.run(monitor);
            
            this.setWebJob(job, 0, now);
        } catch (Exception e) {
            if(debug) {
                logger.error(e);
            }
            else {
                logger.warn(e.getMessage());
            }
            
            int failCount = this.getFailCount(job);
            failCount += 1;
            this.setWebJob(job, failCount, now);
        }
    }
    
    public Connection getConnection(String url) {
        return this.getConnection(url, null);
    }
    
    public Connection getConnection(String url, String userAgent) {
        Connection con = HttpConnection.connect(url).timeout(this.jsoupTimeout);
        if(!StringUtils.isBlank(userAgent)) {
            con = con.userAgent(userAgent);
        }
        return con;
    }
    
    public String getContent(String url) throws IOException{
        Connection con = this.getConnection(url);
        con.method(Method.GET).ignoreContentType(true);
        Response resp = con.execute();
        String body = resp.body();
        return body;
    }
    
    public String doPost(String url, Map<String, String> data) throws IOException {
        Connection con = this.getConnection(url);
        con.method(Method.POST).ignoreContentType(true);
        con.data(data);
        Response resp = con.execute();
        String body = resp.body();
        return body;
    }
    
    public JSONObject toJSONObject(String json) {
        return JSONObject.fromObject(json);
    }
    
    public WordInfo lookup(String word) {
        if(StringUtils.isBlank(word)) {
            return null;
        }
        
        WordInfo ret = this.words.get(word);
        if(ret == null) {
            WebJob job = this.getAvailableWebJobByName(WordLookupJob.class.getSimpleName());
            if(job != null) {
                this.put(WordLookupJob.WORD, word);
                this.put(WordLookupJob.WORD_LOOKUP, null);
                
                this.run(job);
                
                this.put(WordLookupJob.WORD, null);
                
                ret = (WordInfo) this.get(WordLookupJob.WORD_LOOKUP);
                this.words.put(word, ret);
                this.put(WordLookupJob.WORD_LOOKUP, null);
            }
        }
        
        return ret;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Map<Integer, WebJobProgress> getWebJobProgresses() {
        return this.jobProgresses;
    }
    
    public String getProgressStatus(WebJob job) {
        if(job == null) {
            return WebJobProgressStatus.NotStarted.name();
        }
        
        WebJobProgress progress = this.jobProgresses.get(job.getId());
        if(progress == null) {
            return WebJobProgressStatus.NotStarted.name();
        }
        
        if(progress.status == null) {
            return WebJobProgressStatus.NotStarted.name();
        }
        
        if(WebJobProgressStatus.InProgress.equals(progress.status)) {
            StringBuffer sb = new StringBuffer();
            sb.append("<div class='progress'>");
            sb.append("<div class=\"progress-bar progress-bar-success\" role=\"progressbar\" aria-valuenow=\"");
            sb.append(progress.percentage);
            sb.append("\" aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: ");
            sb.append(progress.percentage);
            sb.append("%\"></div></div>");
            return sb.toString();
        }
        else {
            return progress.status.name();
        }
    }
    
    public String stop(WebJob job) {
        if(job == null) {
            return "Web job is invalid";
        }
        
        WebJobProgress progress = this.jobProgresses.get(job.getId());
        if(progress == null) {
            return "No web job progress is found";
        }
        
        if(!WebJobProgressStatus.InProgress.equals(progress.status)) {
            return "Web job is not running in progress";
        }
        
        progress.stopRequired = true;
        
        return null;
    }
    
    public boolean isWebJobInProgress(WebJob job) {
        if(job == null) {
            return false;
        }
        
        WebJobProgress progress = this.jobProgresses.get(job.getId());
        if(progress == null) {
            return false;
        }
        
        return WebJobProgressStatus.InProgress.equals(progress.status);
    }
    
    public String getSpentTimeDisplay(WebJob job) {
        if(job == null) {
            return "";
        }
        
        WebJobProgress progress = this.jobProgresses.get(job.getId());
        if(progress == null) {
            return "";
        }
        
        if(!WebJobProgressStatus.InProgress.equals(progress.status)) {
            long elapsed = progress.endTime - progress.startTime;
            if(elapsed > 0) {
                return TimeUtils.getTimeReadableString(elapsed);
            }
            else {
                return "";
            }
        }
        else {
            long elapsed = System.currentTimeMillis() - progress.startTime;
            if(progress.percentage > 0) {
                long remaining = elapsed * (100 - progress.percentage) / progress.percentage;
                return TimeUtils.getTimeReadableString(elapsed) + ", remaining " + TimeUtils.getTimeReadableString(remaining);
            }
            else {
                return "";
            }
        }
    }
}
