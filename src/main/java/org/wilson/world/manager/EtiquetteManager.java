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
import org.wilson.world.etiquette.EtiquetteInfo;
import org.wilson.world.etiquette.EtiquetteItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;
import org.wilson.world.web.WebJob;

public class EtiquetteManager implements StorageListener{
    public static final String ETIQUETTES = "etiquettes";
    
    public static final String ETIQUETTES_REMOVED = "etiquettes_removed";
    
    private static EtiquetteManager instance;
    
    private String current;
    
    private Map<Integer, EtiquetteInfo> ids = new ConcurrentHashMap<Integer, EtiquetteInfo>();
    
    private static int GLOBAL_ID = 1;
    
    private EtiquetteInfo selected;
    
    public static final String ETIQUETTE_FILE_NAME = "etiquette.html";
    
    private Map<Integer, EtiquetteItem> items = new HashMap<Integer, EtiquetteItem>();
    
    public static final String STORAGE_PREFIX = "/etiquettes/";
    
    public static final String STORAGE_SUFFIX = ".html";
    
    private EtiquetteManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static EtiquetteManager getInstance() {
        if(instance == null) {
            instance = new EtiquetteManager();
        }
        return instance;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }
    
    public EtiquetteInfo getSelected() {
        return selected;
    }

    public void setSelected(EtiquetteInfo selected) {
        this.selected = selected;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<EtiquetteInfo>> getEtiquetteInfoMap() {
        Map<String, List<EtiquetteInfo>> map = (Map<String, List<EtiquetteInfo>>) WebManager.getInstance().get(ETIQUETTES);
        if(map == null) {
            map = new ConcurrentHashMap<String, List<EtiquetteInfo>>();
            this.setEtiquetteInfoMap(map);
        }
        
        return map;
    }
    
    public void setEtiquetteInfoMap(Map<String, List<EtiquetteInfo>> map) {
        WebManager.getInstance().put(ETIQUETTES, map);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, EtiquetteInfo> getEtiquettesRemoved() {
        return (Map<String, EtiquetteInfo>) WebManager.getInstance().get(ETIQUETTES_REMOVED);
    }
    
    public void setEtiquettesRemoved(Map<String, EtiquetteInfo> removed) {
        WebManager.getInstance().put(ETIQUETTES_REMOVED, removed);
    }

    public void clear(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<EtiquetteInfo>> map = this.getEtiquetteInfoMap();
        List<EtiquetteInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return;
        }
        
        for(EtiquetteInfo info : infos) {
            this.ids.remove(info.id);
        }
        
        infos.clear();
    }
    
    public void addEtiquetteInfo(EtiquetteInfo info) {
        if(info == null) {
            return;
        }

        String from = info.from;
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, EtiquetteInfo> removed = this.getEtiquettesRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<EtiquetteInfo>> map = this.getEtiquetteInfoMap();
        List<EtiquetteInfo> infos = map.get(from);
        if(infos == null) {
            infos = new ArrayList<EtiquetteInfo>();
            map.put(from, infos);
        }
        infos.add(info);
        
        this.ids.put(info.id, info);
    }
    
    public EtiquetteInfo getEtiquetteInfo(int id) {
        return this.ids.get(id);
    }
    
    public List<EtiquetteInfo> getEtiquetteInfos() {
        return new ArrayList<EtiquetteInfo>(this.ids.values());
    }
    
    public EtiquetteInfo randomEtiquetteInfo() {
        String from = this.current;
        
        Map<String, List<EtiquetteInfo>> map = this.getEtiquetteInfoMap();
        if(StringUtils.isBlank(from)) {
            List<String> froms = new ArrayList<String>(map.keySet());
            if(froms.isEmpty()) {
                return null;
            }
            int n = DiceManager.getInstance().random(froms.size());
            from = froms.get(n);
        }
        
        List<EtiquetteInfo> infos = map.get(from);
        if(infos == null || infos.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public void loadEtiquetteInfo(EtiquetteInfo info) {
        if(info == null) {
            return;
        }
        
        for(EtiquetteInfo i : this.getEtiquetteInfos()) {
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
    
    public String getEtiquetteFileName() {
        return ETIQUETTE_FILE_NAME;
    }

    @Override
    public void created(StorageAsset asset) {
        EtiquetteItem item = this.toEtiquetteItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        EtiquetteItem item = this.toEtiquetteItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            EtiquetteItem item = this.toEtiquetteItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private EtiquetteItem toEtiquetteItem(StorageAsset asset) {
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
        
        EtiquetteItem item = new EtiquetteItem();
        item.id = asset.id;
        item.name = name;
        
        return item;
    }
    
    public String save(EtiquetteInfo info, String name) throws Exception{
        if(info == null) {
            return "Etiquette should be provided";
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(info.html.getBytes());
        String checksum = IOUtils.getChecksum(in);
        if(StorageManager.getInstance().hasDuplicate(checksum)) {
            return "Duplicate etiquette has been found";
        }
        
        in = new ByteArrayInputStream(info.html.getBytes());
        ReadableByteChannel rbc = Channels.newChannel(in);
        FileOutputStream fos = new FileOutputStream(ConfigManager.getInstance().getDataDir() + EtiquetteManager.getInstance().getEtiquetteFileName());
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        finally {
            in.close();
            fos.close();
        }
        
        name = this.toStorageName(name);
        
        String ret = StorageManager.getInstance().createStorageAsset(name, URLManager.getInstance().getBaseUrl() + "/api/etiquette/get_file");
        
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
    
    public List<EtiquetteItem> getEtiquetteItems() {
        return new ArrayList<EtiquetteItem>(this.items.values());
    }
    
    public EtiquetteItem getEtiquetteItem(int id) {
        return this.items.get(id);
    }
    
    public EtiquetteInfo load(EtiquetteItem item) throws Exception {
        if(item == null) {
            return null;
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return null;
        }
        
        String content = StorageManager.getInstance().getContent(asset);
        
        EtiquetteInfo info = new EtiquetteInfo();
        info.title = item.name;
        info.html = content;
        
        return info;
    }
    
    public void removeEtiquetteInfo(EtiquetteInfo info) {
        if(info != null) {
            Map<String, List<EtiquetteInfo>> etiquettes = this.getEtiquetteInfoMap();
            List<EtiquetteInfo> infos = etiquettes.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, EtiquetteInfo> removed = this.getEtiquettesRemoved();
            if(removed == null) {
                removed = new HashMap<String, EtiquetteInfo>();
                this.setEtiquettesRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
}
