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
import org.wilson.world.novel.NovelInfo;
import org.wilson.world.novel.NovelItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;
import org.wilson.world.web.WebJob;

public class NovelManager implements StorageListener{
    public static final String NOVELS = "novels";
    
    public static final String NOVELS_REMOVED = "novels_removed";
    
    private static NovelManager instance;
    
    private String current;
    
    private Map<Integer, NovelInfo> ids = new ConcurrentHashMap<Integer, NovelInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private NovelInfo selected;
    
    public static final String NOVEL_FILE_NAME = "novel.html";
    
    private Map<Integer, NovelItem> items = new HashMap<Integer, NovelItem>();
    
    public static final String STORAGE_PREFIX = "/novels/";
    
    public static final String STORAGE_SUFFIX = ".html";
    
    private NovelManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static NovelManager getInstance() {
        if(instance == null) {
            instance = new NovelManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public NovelInfo getSelected() {
        return selected;
    }

    public void setSelected(NovelInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<NovelInfo>> getNovelInfoMap() {
        Map<String, List<NovelInfo>> map = (Map<String, List<NovelInfo>>) WebManager.getInstance().get(NOVELS);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<NovelInfo>>();
            this.setNovelInfoMap(map);
        }
        
        return map;
    }
    
    public void setNovelInfoMap(Map<String, List<NovelInfo>> map) {
        WebManager.getInstance().put(NOVELS, map);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, NovelInfo> getNovelsRemoved() {
        return (Map<String, NovelInfo>) WebManager.getInstance().get(NOVELS_REMOVED);
    }
    
    public void setNovelsRemoved(Map<String, NovelInfo> removed) {
        WebManager.getInstance().put(NOVELS_REMOVED, removed);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<NovelInfo>> map = this.getNovelInfoMap();
        List<NovelInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(NovelInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addNovelInfo(NovelInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, NovelInfo> removed = this.getNovelsRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<NovelInfo>> map = this.getNovelInfoMap();
        List<NovelInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<NovelInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public NovelInfo getNovelInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<NovelInfo> getNovelInfos() {
        return new ArrayList<NovelInfo>(this.ids.values());
    }
    
    public NovelInfo randomNovelInfo() {
        String from = this.current;
        
        Map<String, List<NovelInfo>> map = this.getNovelInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<NovelInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadNovelInfo(NovelInfo info) {
        if(info == null) {
            return;
        }
        
        for(NovelInfo i : this.getNovelInfos()) {
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
    
    public String getNovelFileName() {
        return NOVEL_FILE_NAME;
    }

    @Override
    public void created(StorageAsset asset) {
        NovelItem item = this.toNovelitem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        NovelItem item = this.toNovelitem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            NovelItem item = this.toNovelitem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private NovelItem toNovelitem(StorageAsset asset) {
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
        
        NovelItem item = new NovelItem();
        item.id = asset.id;
        item.name = name;
        
        return item;
    }
    
    public String save(NovelInfo info, String name) throws Exception{
        if(info == null) {
            return "Novel should be provided";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(info.html.getBytes());
        String checksum = IOUtils.getChecksum(in);
        if(StorageManager.getInstance().hasDuplicate(checksum)) {
            return "Duplicate novel has been found";
        }
        
        in = new ByteArrayInputStream(info.html.getBytes());
        ReadableByteChannel rbc = Channels.newChannel(in);
        FileOutputStream fos = new FileOutputStream(ConfigManager.getInstance().getDataDir() + NovelManager.getInstance().getNovelFileName());
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            fos.close();
            in.close();
        }
        
        name = this.toStorageName(name);
        
        String ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/novel/get_file");
        
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
    
    public List<NovelItem> getNovelItems() {
        return new ArrayList<NovelItem>(this.items.values());
    }
    
    public NovelItem getNovelItem(int id) {
        return this.items.get(id);
    }
    
    public NovelItem randomNovelItem() {
        List<NovelItem> items = this.getNovelItems();
        if(items.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }
    
    public NovelInfo load(NovelItem item) throws Exception {
        if(item == null) {
            return null;
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return null;
        }
        
        String content = StorageManager.getInstance().getContent(asset);
        
        NovelInfo info = new NovelInfo();
        info.title = item.name;
        info.html = content;
        
        return info;
    }
    
    public void removeNoveInfo(NovelInfo info) {
        if(info != null) {
            Map<String, List<NovelInfo>> novels = this.getNovelInfoMap();
            List<NovelInfo> infos = novels.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, NovelInfo> removed = this.getNovelsRemoved();
            if(removed == null) {
                removed = new HashMap<String, NovelInfo>();
                this.setNovelsRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
}
