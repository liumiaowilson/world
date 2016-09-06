package org.wilson.world.manager;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.article.ArticleInfo;
import org.wilson.world.article.ArticleItem;
import org.wilson.world.article.ArticleSpeedTrainResult;
import org.wilson.world.model.ArticleSpeedInfo;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;
import org.wilson.world.util.TimeUtils;
import org.wilson.world.web.WebJob;

public class ArticleManager implements StorageListener{
    public static final String ARTICLES = "articles";
    
    public static final String ARTICLES_REMOVED = "articles_removed";
    
    private static ArticleManager instance;
    
    private String current;
    
    private Map<Integer, ArticleInfo> ids = new ConcurrentHashMap<Integer, ArticleInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private ArticleInfo selected;
    
    public static final String ARTICLE_FILE_NAME = "article.html";
    
    private Map<Integer, ArticleItem> items = new HashMap<Integer, ArticleItem>();
    
    public static final String STORAGE_PREFIX = "/articles/";
    
    public static final String STORAGE_SUFFIX = ".html";
    
    private ArticleInfo trainArticle = new ArticleInfo();
    
    private ArticleManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static ArticleManager getInstance() {
        if(instance == null) {
            instance = new ArticleManager();
        }
        return instance;
    }
    
    public void setTrainArticleInfo(ArticleInfo info) {
        this.trainArticle.id = info.id;
        this.trainArticle.from = info.from;
        this.trainArticle.title = info.title;
        this.trainArticle.url = info.url;
        this.trainArticle.html = info.html;
        this.trainArticle.text = info.text;
        this.trainArticle.expectedTime = info.expectedTime;
    }
    
    public void resetTrainArticleInfo() {
        this.trainArticle.id = 0;
        this.trainArticle.from = null;
        this.trainArticle.title = null;
        this.trainArticle.url = null;
        this.trainArticle.html = null;
        this.trainArticle.text = null;
        this.trainArticle.expectedTime = 0;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public ArticleInfo getSelected() {
        return selected;
    }

    public void setSelected(ArticleInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<ArticleInfo>> getArticleInfoMap() {
        Map<String, List<ArticleInfo>> map = (Map<String, List<ArticleInfo>>) WebManager.getInstance().get(ARTICLES);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<ArticleInfo>>();
            this.setArticleInfoMap(map);
        }
        
        return map;
    }
    
    public void setArticleInfoMap(Map<String, List<ArticleInfo>> map) {
        WebManager.getInstance().put(ARTICLES, map);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, ArticleInfo> getArticlesRemoved() {
        return (Map<String, ArticleInfo>) WebManager.getInstance().get(ARTICLES_REMOVED);
    }
    
    public void setArticlesRemoved(Map<String, ArticleInfo> removed) {
        WebManager.getInstance().put(ARTICLES_REMOVED, removed);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<ArticleInfo>> map = this.getArticleInfoMap();
        List<ArticleInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(ArticleInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addArticleInfo(ArticleInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, ArticleInfo> removed = this.getArticlesRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<ArticleInfo>> map = this.getArticleInfoMap();
        List<ArticleInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<ArticleInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public ArticleInfo getArticleInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<ArticleInfo> getArticleInfos() {
        return new ArrayList<ArticleInfo>(this.ids.values());
    }
    
    public ArticleInfo randomArticleInfo() {
        String from = this.current;
        
        Map<String, List<ArticleInfo>> map = this.getArticleInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<ArticleInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadArticleInfo(ArticleInfo info) {
        if(info == null) {
            return;
        }
        
        for(ArticleInfo i : this.getArticleInfos()) {
            if(i.html != null) {
                i.html = null;
            }
        }
        
        String from = info.from;
        String jobName = from;
        WebJob job = WebManager.getInstance().getAvailableWebJobByName(jobName);
        if(job == null) {
            job = WebManager.getInstance().getAvailableWebJobByName(jobName + "Job");
        }
        
        if(job == null) {
            return;
        }
        
        try {
            this.setSelected(info);
            WebManager.getInstance().run(job);
        }
        finally {
            this.setSelected(null);
        }
        
        int [] speed = this.getArrayOfWPM();
        int avg_speed = speed[0];
        if(avg_speed != 0) {
            info.expectedTime = this.getNumOfWords(info.text) / avg_speed;
        }
    }
    
    public String getArticleFileName() {
        return ARTICLE_FILE_NAME;
    }

    @Override
    public void created(StorageAsset asset) {
        ArticleItem item = this.toArticleItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        ArticleItem item = this.toArticleItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            ArticleItem item = this.toArticleItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private ArticleItem toArticleItem(StorageAsset asset) {
        if(asset == null) {
            return null;
        }
        
        String name = asset.name;
        if(!name.startsWith(STORAGE_PREFIX)) {
            return null;
        }
        if(!name.endsWith(STORAGE_SUFFIX)) {
            return null;
        }
        
        name = name.substring(STORAGE_PREFIX.length(), name.length() - STORAGE_SUFFIX.length());
        
        ArticleItem item = new ArticleItem();
        item.id = asset.id;
        item.name = name;
        
        return item;
    }
    
    public String save(ArticleInfo info, String name) throws Exception{
        if(info == null) {
            return "Article should be provided";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(info.html.getBytes());
        String checksum = IOUtils.getChecksum(in);
        if(StorageManager.getInstance().hasDuplicate(checksum)) {
            return "Duplicate article has been found";
        }
        
        in = new ByteArrayInputStream(info.html.getBytes());
        ReadableByteChannel rbc = Channels.newChannel(in);
        FileOutputStream fos = new FileOutputStream(ConfigManager.getInstance().getDataDir() + this.getArticleFileName());
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            fos.close();
            in.close();
        }
        
        name = this.toStorageName(name);
        
        String ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/article/get_file");
        
        return ret;
    }
    
    private String toStorageName(String name) {
        if(!name.startsWith(STORAGE_PREFIX)) {
            name = STORAGE_PREFIX + name;
        }
        
        if(!name.endsWith(STORAGE_SUFFIX)) {
            name = name + STORAGE_SUFFIX;
        }
        
        return name;
    }
    
    public List<ArticleItem> getArticleItems() {
        return new ArrayList<ArticleItem>(this.items.values());
    }
    
    public ArticleItem getArticleItem(int id) {
        return this.items.get(id);
    }
    
    public ArticleInfo load(ArticleItem item) throws Exception {
        if(item == null) {
            return null;
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return null;
        }
        
        String content = StorageManager.getInstance().getContent(asset);
        
        ArticleInfo info = new ArticleInfo();
        info.title = item.name;
        info.html = content;
        
        return info;
    }
    
    public ArticleSpeedTrainResult trainSpeed(String title, long startTime, long endTime) {
        ArticleSpeedTrainResult result = new ArticleSpeedTrainResult();
        if(StringUtils.isBlank(title)) {
            result.errorMessage = "Invalid article title";
            return result;
        }
        
        ArticleInfo info = this.trainArticle;
        if(info == null) {
            result.errorMessage = "No such article info found";
            return result;
        }
        
        if(info.html == null || info.text == null) {
            result.errorMessage = "Article is not loaded";
            return result;
        }
        
        int words = this.getNumOfWords(info.text);
        long time = endTime - startTime;
        
        ArticleSpeedInfo speedInfo = new ArticleSpeedInfo();
        speedInfo.words = words;
        speedInfo.time = time;
        ArticleSpeedInfoManager.getInstance().createArticleSpeedInfo(speedInfo);
        
        result.wordsPerMinute = this.getWordsPerMinute(words, time);
        
        this.resetTrainArticleInfo();
        
        return result;
    }
    
    public int [] getArrayOfWPM() {
        int [] ret = new int [3];
        
        int total_words = 0;
        long total_time = 0;
        int max = 0;
        int min = 0;
        
        for(ArticleSpeedInfo info : ArticleSpeedInfoManager.getInstance().getArticleSpeedInfos()) {
            total_words += info.words;
            total_time += info.time;
            
            int wpm = this.getWordsPerMinute(info.words, info.time);
            if(wpm > max) {
                max = wpm;
            }
            if(min == 0 || wpm < min) {
                min = wpm;
            }
        }
        
        if(total_time == 0) {
            return null;
        }
        
        int avg = this.getWordsPerMinute(total_words, total_time);
        ret[0] = avg;
        ret[1] = min;
        ret[2] = max;
        
        return ret;
    }
    
    public Map<Integer, Integer> getSpeedStats() {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
        
        for(ArticleSpeedInfo info : ArticleSpeedInfoManager.getInstance().getArticleSpeedInfos()) {
            int minutes = (int) (info.time / TimeUtils.MINUTE_DURATION);
            ret.put(info.words, minutes);
        }
        
        return ret;
    }
    
    private int getWordsPerMinute(int words, long time) {
        double speed = words * 1.0 / time;
        return (int) (speed * TimeUtils.MINUTE_DURATION);
    }
    
    private int getNumOfWords(String content) {
        int sum = 0;
        for(String item : content.split(" ")) {
            if(!StringUtils.isBlank(item)) {
                sum++;
            }
        }
        return sum;
    }
    
    public void removeArticleInfo(ArticleInfo info) {
        if(info != null) {
            Map<String, List<ArticleInfo>> articles = this.getArticleInfoMap();
            List<ArticleInfo> infos = articles.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, ArticleInfo> removed = this.getArticlesRemoved();
            if(removed == null) {
                removed = new HashMap<String, ArticleInfo>();
                this.setArticlesRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
    
    public ArticleInfo getArticleSection(ArticleInfo info) {
        if(info == null || info.html == null) {
            return null;
        }
        
        int sectionLength = ConfigManager.getInstance().getConfigAsInt("article.section.length", 1000);
        String text = info.html;
        int pos = DiceManager.getInstance().random(text.length());
        pos = text.indexOf(".", pos);
        if(pos < 0) {
            return null;
        }
        
        int end_pos = pos + sectionLength;
        if(end_pos > text.length()) {
            end_pos = text.length();
        }
        String s = text.substring(pos + 1, end_pos).trim() + "...";
        
        ArticleInfo section = new ArticleInfo();
        section.title = "Section of [" + info.title + "]";
        section.html = s;
        
        return section;
    }
}
