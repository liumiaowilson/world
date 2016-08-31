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
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.story.StoryInfo;
import org.wilson.world.story.StoryItem;
import org.wilson.world.util.IOUtils;
import org.wilson.world.web.WebJob;

public class StoryManager implements StorageListener{
    public static final String STORIES = "stories";
    
    private static StoryManager instance;
    
    private String current;
    
    private Map<Integer, StoryInfo> ids = new ConcurrentHashMap<Integer, StoryInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private StoryInfo selected;
    
    public static final String STORY_FILE_NAME = "story.html";
    
    private Map<Integer, StoryItem> items = new HashMap<Integer, StoryItem>();
    
    public static final String STORAGE_PREFIX = "/stories/";
    
    public static final String STORAGE_SUFFIX = ".html";
    
    private StoryManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static StoryManager getInstance() {
        if(instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public StoryInfo getSelected() {
        return selected;
    }

    public void setSelected(StoryInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<StoryInfo>> getStoryInfoMap() {
        Map<String, List<StoryInfo>> map = (Map<String, List<StoryInfo>>) WebManager.getInstance().get(STORIES);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<StoryInfo>>();
            this.setStoryInfoMap(map);
        }
        
        return map;
    }
    
    public void setStoryInfoMap(Map<String, List<StoryInfo>> map) {
        WebManager.getInstance().put(STORIES, map);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<StoryInfo>> map = this.getStoryInfoMap();
        List<StoryInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(StoryInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addStoryInfo(StoryInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<StoryInfo>> map = this.getStoryInfoMap();
        List<StoryInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<StoryInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public StoryInfo getStoryInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<StoryInfo> getStoryInfos() {
        return new ArrayList<StoryInfo>(this.ids.values());
    }
    
    public StoryInfo randomStoryInfo() {
        String from = this.current;
        
        Map<String, List<StoryInfo>> map = this.getStoryInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<StoryInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadStoryInfo(StoryInfo info) {
        if(info == null) {
            return;
        }
        
        for(StoryInfo i : this.getStoryInfos()) {
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
    }
    
    public String getStoryFileName() {
        return STORY_FILE_NAME;
    }

    @Override
    public void created(StorageAsset asset) {
        StoryItem item = this.toStoryItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        StoryItem item = this.toStoryItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            StoryItem item = this.toStoryItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private StoryItem toStoryItem(StorageAsset asset) {
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
        
        StoryItem item = new StoryItem();
        item.id = asset.id;
        item.name = name;
        
        return item;
    }
    
    public String save(StoryInfo info, String name) throws Exception{
        if(info == null) {
            return "Story should be provided";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(info.html.getBytes());
        String checksum = IOUtils.getChecksum(in);
        if(StorageManager.getInstance().hasDuplicate(checksum)) {
            return "Duplicate story has been found";
        }
        
        in = new ByteArrayInputStream(info.html.getBytes());
        ReadableByteChannel rbc = Channels.newChannel(in);
        FileOutputStream fos = new FileOutputStream(ConfigManager.getInstance().getDataDir() + StoryManager.getInstance().getStoryFileName());
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            in.close();
            fos.close();
        }
        
        name = this.toStorageName(name);
        
        String ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/story/get_file");
        
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
    
    public List<StoryItem> getStoryItems() {
        return new ArrayList<StoryItem>(this.items.values());
    }
    
    public StoryItem getStoryItem(int id) {
        return this.items.get(id);
    }
    
    public StoryInfo load(StoryItem item) throws Exception {
        if(item == null) {
            return null;
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return null;
        }
        
        String content = StorageManager.getInstance().getContent(asset);
        
        StoryInfo info = new StoryInfo();
        info.title = item.name;
        info.html = content;
        
        return info;
    }
    
    public void removeStoryInfo(StoryInfo info) {
        if(info != null) {
            Map<String, List<StoryInfo>> stories = this.getStoryInfoMap();
            List<StoryInfo> infos = stories.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
        }
    }
}
