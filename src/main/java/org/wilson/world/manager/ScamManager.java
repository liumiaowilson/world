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
import org.wilson.world.scam.ScamInfo;
import org.wilson.world.scam.ScamItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;
import org.wilson.world.web.WebJob;

public class ScamManager implements StorageListener{
    public static final String SCAMS = "scams";
    
    public static final String SCAMS_REMOVED = "scams_removed";
    
    private static ScamManager instance;
    
    private String current;
    
    private Map<Integer, ScamInfo> ids = new ConcurrentHashMap<Integer, ScamInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private ScamInfo selected;
    
    public static final String SCAM_FILE_NAME = "scam.html";
    
    private Map<Integer, ScamItem> items = new HashMap<Integer, ScamItem>();
    
    public static final String STORAGE_PREFIX = "/scams/";
    
    public static final String STORAGE_SUFFIX = ".html";
    
    private ScamManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static ScamManager getInstance() {
        if(instance == null) {
            instance = new ScamManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public ScamInfo getSelected() {
        return selected;
    }

    public void setSelected(ScamInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<ScamInfo>> getScamInfoMap() {
        Map<String, List<ScamInfo>> map = (Map<String, List<ScamInfo>>) WebManager.getInstance().get(SCAMS);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<ScamInfo>>();
            this.setScamInfoMap(map);
        }
        
        return map;
    }
    
    public void setScamInfoMap(Map<String, List<ScamInfo>> map) {
        WebManager.getInstance().put(SCAMS, map);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, ScamInfo> getScamsRemoved() {
        return (Map<String, ScamInfo>) WebManager.getInstance().get(SCAMS_REMOVED);
    }
    
    public void setScamsRemoved(Map<String, ScamInfo> removed) {
        WebManager.getInstance().put(SCAMS_REMOVED, removed);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<ScamInfo>> map = this.getScamInfoMap();
        List<ScamInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(ScamInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addScamInfo(ScamInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, ScamInfo> removed = this.getScamsRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<ScamInfo>> map = this.getScamInfoMap();
        List<ScamInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<ScamInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public ScamInfo getScamInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<ScamInfo> getScamInfos() {
        return new ArrayList<ScamInfo>(this.ids.values());
    }
    
    public ScamInfo randomScamInfo() {
        String from = this.current;
        
        Map<String, List<ScamInfo>> map = this.getScamInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<ScamInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadScamInfo(ScamInfo info) {
        if(info == null) {
            return;
        }
        
        for(ScamInfo i : this.getScamInfos()) {
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
    
    public String getScamFileName() {
        return SCAM_FILE_NAME;
    }

    @Override
    public void created(StorageAsset asset) {
        ScamItem item = this.toScamItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        ScamItem item = this.toScamItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            ScamItem item = this.toScamItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private ScamItem toScamItem(StorageAsset asset) {
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
        
        ScamItem item = new ScamItem();
        item.id = asset.id;
        item.name = name;
        
        return item;
    }
    
    public String save(ScamInfo info, String name) throws Exception{
        if(info == null) {
            return "Scam should be provided";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(info.html.getBytes());
        String checksum = IOUtils.getChecksum(in);
        if(StorageManager.getInstance().hasDuplicate(checksum)) {
            return "Duplicate scam has been found";
        }
        
        in = new ByteArrayInputStream(info.html.getBytes());
        ReadableByteChannel rbc = Channels.newChannel(in);
        FileOutputStream fos = new FileOutputStream(ConfigManager.getInstance().getDataDir() + ScamManager.getInstance().getScamFileName());
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            in.close();
            fos.close();
        }
        
        name = this.toStorageName(name);
        
        String ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/scam/get_file");
        
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
    
    public List<ScamItem> getScamItems() {
        return new ArrayList<ScamItem>(this.items.values());
    }
    
    public ScamItem getScamItem(int id) {
        return this.items.get(id);
    }
    
    public ScamInfo load(ScamItem item) throws Exception {
        if(item == null) {
            return null;
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return null;
        }
        
        String content = StorageManager.getInstance().getContent(asset);
        
        ScamInfo info = new ScamInfo();
        info.title = item.name;
        info.html = content;
        
        return info;
    }
    
    public void removeScamInfo(ScamInfo info) {
        if(info != null) {
            Map<String, List<ScamInfo>> stories = this.getScamInfoMap();
            List<ScamInfo> infos = stories.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, ScamInfo> removed = this.getScamsRemoved();
            if(removed == null) {
                removed = new HashMap<String, ScamInfo>();
                this.setScamsRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
}
