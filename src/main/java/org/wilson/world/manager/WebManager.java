package org.wilson.world.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.wilson.world.article.LiteroticaJob;
import org.wilson.world.beauty.BeautyListJob;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.clip.ClipDownloadJob;
import org.wilson.world.clip.ClipListJob;
import org.wilson.world.etiquette.EtiquetteJob;
import org.wilson.world.fashion.FashionListJob;
import org.wilson.world.feed.FeedJob;
import org.wilson.world.food.SaveurJob;
import org.wilson.world.howto.HowToListJob;
import org.wilson.world.image.ImageListJob;
import org.wilson.world.java.JavaExtensionListener;
import org.wilson.world.joke.XiaohuaJob;
import org.wilson.world.lifecycle.ManagerLifecycle;
import org.wilson.world.manga.MangaDownloadJob;
import org.wilson.world.manga.MangaListJob;
import org.wilson.world.model.Feed;
import org.wilson.world.model.Hopper;
import org.wilson.world.model.HopperData;
import org.wilson.world.novel.H528Job;
import org.wilson.world.novel.Novel1000Job;
import org.wilson.world.novel.Story69Job;
import org.wilson.world.parn.JapanParnList2Job;
import org.wilson.world.parn.JapanParnListJob;
import org.wilson.world.parn.ParnListJob;
import org.wilson.world.proxy.DynaProxyJob;
import org.wilson.world.proxy.DynamicProxy;
import org.wilson.world.scam.PianJuJob;
import org.wilson.world.sentence.RandomSentenceJob;
import org.wilson.world.storage.StorageStatusJob;
import org.wilson.world.storage.StorageSyncJob;
import org.wilson.world.story.BedtimeJob;
import org.wilson.world.util.FormatUtils;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.DataSizeInfo;
import org.wilson.world.web.DataSizeItem;
import org.wilson.world.web.DataSizeReportInfo;
import org.wilson.world.web.DefaultWebJob;
import org.wilson.world.web.DefaultWebJobMonitor;
import org.wilson.world.web.IPJob;
import org.wilson.world.web.NounsListJob;
import org.wilson.world.web.QuoteOfTheDayJob;
import org.wilson.world.web.SystemWebJob;
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

public class WebManager implements ManagerLifecycle, JavaExtensionListener<SystemWebJob> {
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
    
    private LinkedList<DataSizeInfo> infos = new LinkedList<DataSizeInfo>();
    
    private Map<String, Object> runtimeData = new HashMap<String, Object>();
    
    private WebManager() {
        this.jobs = new DefaultCache<Integer, WebJob>("web_manager_jobs", false);
        
        this.jsoupTimeout = ConfigManager.getInstance().getConfigAsInt("web.jsoup.timeout", 30000);
        
        MonitorManager.getInstance().registerMonitorParticipant(new WebJobStatusMonitor());
        
        ExtManager.getInstance().addJavaExtensionListener(this);
        
        this.loadSystemWebJobs();
    }
    
    private void loadSystemWebJobs() {
        GLOBAL_ID = 1;
        
        this.loadSystemWebJob(new WordOfTheDayJob());
        this.loadSystemWebJob(new WordListJob());
        this.loadSystemWebJob(new NounsListJob());
        this.loadSystemWebJob(new ImageListJob());
        this.loadSystemWebJob(new QuoteOfTheDayJob());
        this.loadSystemWebJob(new WordLookupJob());
        this.loadSystemWebJob(new BeautyListJob());
        this.loadSystemWebJob(new ParnListJob());
        this.loadSystemWebJob(new MangaListJob());
        this.loadSystemWebJob(new MangaDownloadJob());
        this.loadSystemWebJob(new ClipListJob());
        this.loadSystemWebJob(new ClipDownloadJob());
        this.loadSystemWebJob(new JapanParnListJob());
        this.loadSystemWebJob(new FashionListJob());
        this.loadSystemWebJob(new JapanParnList2Job());
        this.loadSystemWebJob(new StorageSyncJob());
        this.loadSystemWebJob(new Novel1000Job());
        this.loadSystemWebJob(new HowToListJob());
        this.loadSystemWebJob(new BedtimeJob());
        this.loadSystemWebJob(new LiteroticaJob());
        this.loadSystemWebJob(new Story69Job());
        this.loadSystemWebJob(new H528Job());
        this.loadSystemWebJob(new XiaohuaJob());
        this.loadSystemWebJob(new PianJuJob());
        this.loadSystemWebJob(new EtiquetteJob());
        this.loadSystemWebJob(new SaveurJob());
        this.loadSystemWebJob(new RandomSentenceJob());
        this.loadSystemWebJob(new IPJob());
        this.loadSystemWebJob(new StorageStatusJob());
        this.loadSystemWebJob(new DynaProxyJob());
        
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
        	WebJob oldJob = this.getWebJob(job.getName());
        	if(oldJob != null) {
        		this.removeSystemWebJob(oldJob);
        	}
        	
            job.setId(-GLOBAL_ID++);
            this.jobs.put(job.getId(), job);
        }
    }
    
    private void removeSystemWebJob(WebJob job) {
    	if(job != null) {
    		this.jobs.delete(job.getId());
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
    
    private void loadHopper(Hopper hopper) {
        if(hopper == null) {
            return;
        }
        
        String action = hopper.action;
        WebJobExecutor executor = (WebJobExecutor) ExtManager.getInstance().getExtension(action, WebJobExecutor.class);
        
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
            	for(Hopper hopper : all) {
            		cachePut(null, hopper);
            	}
            	
            }

            @Override
            public void cacheLoading(List<Hopper> old) {
            	//do not clear the cache as java extensible web jobs loaded will also be cleared
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
    	Object ret = this.runtimeData.get(key);
    	if(ret != null) {
    		return ret;
    	}
    	
        return this.data.get(key);
    }
    
    public void putRuntimeData(String key, Object value) {
    	if(value == null) {
    		this.runtimeData.remove(key);
    	}
    	else {
    		this.runtimeData.put(key, value);
    	}
    }
    
    public Object getRuntimeData(String key) {
    	return this.runtimeData.get(key);
    }
    
    public void resetRuntimeData() {
    	this.runtimeData.clear();
    }
    
    public void addRuntimeData(Map<String, Object> data) {
    	this.runtimeData.putAll(data);
    }
    
    public void removeRuntimeData(Map<String, Object> data) {
    	for(String key : data.keySet()) {
    		this.runtimeData.remove(key);
    	}
    }
    
    @SuppressWarnings("rawtypes")
    public List getList(String key) {
        List list = (List) this.data.get(key);
        if(list == null) {
            list = new ArrayList();
            this.data.put(key, list);
        }
        
        return list;
    }
    
    public Document toDocument(String html) {
        return Jsoup.parse(html);
    }
    
    public Document parse(String url) throws IOException {
        return this.parse(url, null);
    }
    
    public Document parse(String url, String userAgent) throws IOException {
        return this.parse(url, userAgent, -1);
    }
    
    public Document parse(String url, String userAgent, int timeout) throws IOException {
        return this.getConnection(url, userAgent, timeout).get();
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
    
    public WebJob getWebJob(String name) {
    	if(StringUtils.isBlank(name)) {
    		return null;
    	}
    	
    	for(WebJob job : this.jobs.getAll()) {
    		if(name.equals(job.getName())) {
    			return job;
    		}
    	}
    	
    	return null;
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
            next = data.lastTime + period * TimeUtils.HOUR_DURATION;
            if(next < now) {
            	next = now;
            }
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
    
    public void run(WebJob job, Map<String, Object> vars) {
    	this.resetRuntimeData();
    	
    	if(vars != null) {
    		this.addRuntimeData(vars);
    	}
    	
    	try {
    		this.run(job);
    	}
    	finally {
        	if(vars != null) {
        		this.removeRuntimeData(vars);
        	}
    	}
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
        return this.getConnection(url, userAgent, -1);
    }
    
    public Connection getConnection(String url, String userAgent, int timeout) {
        if(timeout <= 0) {
            timeout = this.jsoupTimeout;
        }
        Connection con = HttpConnection.connect(url).timeout(timeout).maxBodySize(0);
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
    
    public String getContentWithProxy(String url, String proxyHost, int proxyPort) throws IOException {
        if(StringUtils.isBlank(url) || StringUtils.isBlank(proxyHost)) {
            return null;
        }
        
        //proxy tunneling to https will throw exception and is not fixed
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        URL website = new URL(url); 
        HttpURLConnection httpUrlConnetion = (HttpURLConnection) website.openConnection(proxy);
        httpUrlConnetion.connect();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnetion.getInputStream()));
        StringBuilder buffer = new StringBuilder();
        String str;

        while((str = br.readLine()) != null )
        {
            buffer.append(str);
        }
        
        return buffer.toString();
    }
    
    /**
     * Better not used with https connection
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public String getContentWithProxy(String url) throws IOException {
    	int retry = 0;
    	int maxRetry = ConfigManager.getInstance().getConfigAsInt("web.get_with_proxy.max_retry", 3);
    	while(retry < maxRetry) {
    		String content = null;
    		DynamicProxy proxy = ProxyManager.getInstance().randomDynamicProxy();
    		try {
    			if(proxy != null) {
    				content = this.getContentWithProxy(url, proxy.host, proxy.port);
    			}
    			else {
    				content = this.getContent(url);
    			}
    		}
    		catch(Exception e) {
    			logger.error(e);
    		}
    		
    		if(content != null) {
    			return content;
    		}
    		
    		retry++;
    	}
    	
    	return null;
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
                this.put(WordLookupJob.WORD_LOOKUP, null);
                
                String meaning = WordManager.getInstance().getMeaning(word);
                if(meaning == null) {
                    WordManager.getInstance().createWord(ret);
                }
            }
            this.words.put(word, ret);
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
    
    public Map<String, Integer> getDataSizeStats() {
        Map<String, Integer> ret = new HashMap<String, Integer>();
        
        for(Entry<String, Object> entry : this.data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            int size = this.getDataSize(value);
            ret.put(key, size);
        }
        
        return ret;
    }
    
    @SuppressWarnings("rawtypes")
    private int getDataSize(Object obj) {
        if(obj == null) {
            return 0;
        }
        
        if(obj instanceof Collection) {
            return this.getDataSize((Collection)obj);
        }
        else if(obj instanceof Map) {
            return this.getDataSize((Map)obj);
        }
        else {
            return 1;
        }
    }
    
    @SuppressWarnings("rawtypes")
    private int getDataSize(Collection col) {
        if(col == null) {
            return 0;
        }
        
        int sum = 0;
        for(Object obj : col) {
            sum += this.getDataSize(obj);
        }
        
        return sum;
    }
    
    @SuppressWarnings("rawtypes")
    private int getDataSize(Map map) {
        if(map == null) {
            return 0;
        }
        
        int sum = 0;
        for(Object obj : map.values()) {
            sum += this.getDataSize(obj);
        }
        
        return sum;
    }
    
    public void addDataSizeInfo(DataSizeInfo info) {
        if(info == null) {
            return;
        }
        
        info.time = System.currentTimeMillis();
        
        int limit = ConfigManager.getInstance().getConfigAsInt("web.data.size.info.limit", 1000);
        this.infos.add(info);
        
        while(this.infos.size() > limit) {
            this.infos.removeFirst();
        }
    }
    
    public boolean allJobsRun() {
        List<WebJob> jobs = this.getJobs();
        if(jobs.isEmpty()) {
            return false;
        }
        
        boolean allRun = true;
        
        for(WebJob job : jobs) {
            String status = this.getJobStatus(job);
            if(WebJobStatus.Disabled.name().equals(status)) {
                continue;
            }
            WebJobProgress progress = this.jobProgresses.get(job.getId());
            if(progress == null) {
                allRun = false;
                break;
            }
            
            if(WebJobProgressStatus.NotStarted == progress.status || WebJobProgressStatus.InProgress == progress.status) {
                allRun = false;
                break;
            }
        }
        
        return allRun;
    }
    
    public List<DataSizeItem> getDataSizeTrend(String name, TimeZone tz) {
        List<DataSizeItem> ret = new ArrayList<DataSizeItem>();
        
        if(StringUtils.isBlank(name)) {
            return ret;
        }
        if(tz == null) {
            tz = TimeZone.getDefault();
        }
        
        for(DataSizeInfo info : this.infos) {
            Integer count = info.data.get(name);
            if(count == null) {
                count = 0;
            }
            DataSizeItem item = new DataSizeItem();
            item.count = count;
            item.display = TimeUtils.getDateTimeUTCString(info.time, tz);
            ret.add(item);
        }
        
        return ret;
    }
    
    public List<String> getDataSetNames() {
        return new ArrayList<String>(this.data.keySet());
    }
    
    public List<DataSizeReportInfo> getDataSizeReport() {
        Map<String, DataSizeReportInfo> ret = new HashMap<String, DataSizeReportInfo>();
        if(this.infos.isEmpty()) {
            return Collections.emptyList();
        }
        
        DataSizeInfo first = this.infos.getFirst();
        for(Entry<String, Integer> entry : first.data.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();
            DataSizeReportInfo info = new DataSizeReportInfo();
            info.name = key;
            info.startTime = first.time;
            info.startCount = count;
            ret.put(info.name, info);
        }
        
        DataSizeInfo last = this.infos.getLast();
        for(Entry<String, Integer> entry : last.data.entrySet()) {
            String key = entry.getKey();
            int count = entry.getValue();
            DataSizeReportInfo info = ret.get(key);
            if(info == null) {
                continue;
            }
            info.endTime = last.time;
            info.endCount = count;
        }
        
        for(DataSizeReportInfo info : ret.values()) {
            if(info.startTime == info.endTime) {
                continue;
            }
            
            double ratio = FormatUtils.getRoundedValue((info.endCount - info.startCount) * 1.0 * TimeUtils.HOUR_DURATION / (info.endTime - info.startTime));
            info.ratio = ratio;
        }
        
        List<DataSizeReportInfo> all = new ArrayList<DataSizeReportInfo>(ret.values());
        Collections.sort(all, new Comparator<DataSizeReportInfo>(){

            @Override
            public int compare(DataSizeReportInfo o1, DataSizeReportInfo o2) {
                if(o1.ratio > o2.ratio) {
                    return -1;
                }
                else if(o1.ratio < o2.ratio) {
                    return 1;
                }
                else {
                    return o1.name.compareTo(o2.name);
                }
            }
            
        });
        
        return all;
    }

	@Override
	public Class<SystemWebJob> getExtensionClass() {
		return SystemWebJob.class;
	}

	@Override
	public void created(SystemWebJob t) {
		if(t != null) {
			this.loadSystemWebJob(t);
		}
	}

	@Override
	public void removed(SystemWebJob t) {
		if(t != null) {
			this.removeSystemWebJob(t);
		}
	}
	
	public String getSystemExternalIP() {
		return (String) this.get(IPJob.EXTERNAL_IP);
	}
}
