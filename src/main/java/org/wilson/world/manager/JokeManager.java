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
import org.wilson.world.joke.JokeInfo;
import org.wilson.world.joke.JokeItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;
import org.wilson.world.web.WebJob;

public class JokeManager implements StorageListener{
    public static final String JOKES = "jokes";
    
    public static final String JOKES_REMOVED = "jokes_removed";
    
    private static JokeManager instance;
    
    private String current;
    
    private Map<Integer, JokeInfo> ids = new ConcurrentHashMap<Integer, JokeInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private JokeInfo selected;
    
    public static final String JOKE_FILE_NAME = "joke.html";
    
    private Map<Integer, JokeItem> items = new HashMap<Integer, JokeItem>();
    
    public static final String STORAGE_PREFIX = "/jokes/";
    
    public static final String STORAGE_SUFFIX = ".html";
    
    private JokeManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static JokeManager getInstance() {
        if(instance == null) {
            instance = new JokeManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public JokeInfo getSelected() {
        return selected;
    }

    public void setSelected(JokeInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<JokeInfo>> getJokeInfoMap() {
        Map<String, List<JokeInfo>> map = (Map<String, List<JokeInfo>>) WebManager.getInstance().get(JOKES);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<JokeInfo>>();
            this.setJokeInfoMap(map);
        }
        
        return map;
    }
    
    public void setJokeInfoMap(Map<String, List<JokeInfo>> map) {
        WebManager.getInstance().put(JOKES, map);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, JokeInfo> getJokesRemoved() {
        return (Map<String, JokeInfo>) WebManager.getInstance().get(JOKES_REMOVED);
    }
    
    public void setJokesRemoved(Map<String, JokeInfo> removed) {
        WebManager.getInstance().put(JOKES_REMOVED, removed);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<JokeInfo>> map = this.getJokeInfoMap();
        List<JokeInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(JokeInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addJokeInfo(JokeInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, JokeInfo> removed = this.getJokesRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<JokeInfo>> map = this.getJokeInfoMap();
        List<JokeInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<JokeInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public JokeInfo getJokeInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<JokeInfo> getJokeInfos() {
        return new ArrayList<JokeInfo>(this.ids.values());
    }
    
    public JokeInfo randomJokeInfo() {
        String from = this.current;
        
        Map<String, List<JokeInfo>> map = this.getJokeInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<JokeInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadJokeInfo(JokeInfo info) {
        if(info == null) {
            return;
        }
        
        for(JokeInfo i : this.getJokeInfos()) {
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
    
    public String getJokeFileName() {
        return JOKE_FILE_NAME;
    }

    @Override
    public void created(StorageAsset asset) {
        JokeItem item = this.toJokeItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        JokeItem item = this.toJokeItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            JokeItem item = this.toJokeItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private JokeItem toJokeItem(StorageAsset asset) {
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
        
        JokeItem item = new JokeItem();
        item.id = asset.id;
        item.name = name;
        
        return item;
    }
    
    public String save(JokeInfo info, String name) throws Exception{
        if(info == null) {
            return "Joke should be provided";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(info.html.getBytes());
        String checksum = IOUtils.getChecksum(in);
        if(StorageManager.getInstance().hasDuplicate(checksum)) {
            return "Duplicate joke has been found";
        }
        
        in = new ByteArrayInputStream(info.html.getBytes());
        ReadableByteChannel rbc = Channels.newChannel(in);
        FileOutputStream fos = new FileOutputStream(ConfigManager.getInstance().getDataDir() + JokeManager.getInstance().getJokeFileName());
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            in.close();
            fos.close();
        }
        
        name = this.toStorageName(name);
        
        String ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/joke/get_file");
        
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
    
    public List<JokeItem> getJokeItems() {
        return new ArrayList<JokeItem>(this.items.values());
    }
    
    public JokeItem getJokeItem(int id) {
        return this.items.get(id);
    }
    
    public JokeInfo load(JokeItem item) throws Exception {
        if(item == null) {
            return null;
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return null;
        }
        
        String content = StorageManager.getInstance().getContent(asset);
        
        JokeInfo info = new JokeInfo();
        info.title = item.name;
        info.html = content;
        
        return info;
    }
    
    public void removeJokeInfo(JokeInfo info) {
        if(info != null) {
            Map<String, List<JokeInfo>> jokes = this.getJokeInfoMap();
            List<JokeInfo> infos = jokes.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, JokeInfo> removed = this.getJokesRemoved();
            if(removed == null) {
                removed = new HashMap<String, JokeInfo>();
                this.setJokesRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
}
