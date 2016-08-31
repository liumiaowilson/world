package org.wilson.world.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.porn.PornInfo;
import org.wilson.world.porn.PornItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;

public class PornManager implements StorageListener {
    public static final String IMAGE_PATH = "image.jpg";
    
    public static final String PORNS = "porns";
    
    private String source;
    
    private static PornManager instance;
    
    private static int GLOBAL_ID = 1;
    
    private Map<Integer, PornInfo> infos = new HashMap<Integer, PornInfo>();
    
    private Map<Integer, PornItem> items = new HashMap<Integer, PornItem>();
    
    public static final String STORAGE_PREFIX = "/porns/";
    public static final String STORAGE_SUFFIX = ".jpg";
    
    private PornManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static PornManager getInstance() {
        if(instance == null) {
            instance = new PornManager();
        }
        return instance;
    }
    
    public PornInfo randomPorn() {
        String source = this.source;
        
        Map<String, List<PornInfo>> porns = this.getPorns();
        if(porns == null) {
            return null;
        }
        if(StringUtils.isBlank(source)) {
            List<String> froms = new ArrayList<String>();
            for(Entry<String, List<PornInfo>> entry : porns.entrySet()) {
                String from = entry.getKey();
                List<PornInfo> infos = entry.getValue();
                if(infos != null && !infos.isEmpty()) {
                    froms.add(from);
                }
            }
            
            if(froms.isEmpty()) {
                source = null;
            }
            
            int t = DiceManager.getInstance().random(froms.size());
            source = froms.get(t);
        }
        
        if(StringUtils.isBlank(source)) {
            return null;
        }
        
        List<PornInfo> infos = porns.get(source);
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public String getPornImagePath(PornInfo info) {
        if(info == null) {
            return null;
        }
        
        return IMAGE_PATH;
    }
    
    public void downloadPorn(PornInfo info) throws IOException {
        if(info == null) {
            return;
        }
        
        if(!StringUtils.isBlank(info.url)) {
            DownloadManager.getInstance().download(info.url, ConfigManager.getInstance().getDataDir() + this.getPornImagePath(info));
        }
    }
    
    public void clearPornInfos(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<PornInfo>> porns = this.getPorns();
        if(porns != null) {
            List<PornInfo> infos = porns.get(from);
            if(infos != null && !infos.isEmpty()) {
                for(PornInfo info : infos) {
                    this.infos.remove(info.id);
                }
                
                infos.clear();
            }
        }
    }
    
    public void addPornInfo(PornInfo info) {
        if(info == null) {
            return;
        }
        
        if(StringUtils.isBlank(info.from)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<PornInfo>> porns = this.getPorns();
        if(porns == null) {
            porns = new HashMap<String, List<PornInfo>>();
            this.setPorns(porns);
        }
        
        List<PornInfo> infos = porns.get(info.from);
        if(infos == null) {
            infos = new ArrayList<PornInfo>();
            porns.put(info.from, infos);
        }
        infos.add(info);
        
        this.infos.put(info.id, info);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<PornInfo>> getPorns() {
        return (Map<String, List<PornInfo>>) WebManager.getInstance().get(PORNS);
    }
    
    public void setPorns(Map<String, List<PornInfo>> porns) {
        WebManager.getInstance().put(PORNS, porns);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public PornInfo getPornInfo(int id) {
        return this.infos.get(id);
    }
    
    public String savePornInfo(PornInfo info, String name) throws Exception {
        if(info == null) {
            return "Porn info should be provided";
        }
        
        if(StringUtils.isBlank(name)) {
            return "Name should be provided";
        }
        
        String url = info.url;
        if(StringUtils.isBlank(url)) {
            return "Url is invalid";
        }
        
        String path = this.getPornImagePath(info);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            String checksum = IOUtils.getChecksum(fis);
            if(StorageManager.getInstance().hasDuplicate(checksum)) {
                return "Duplicate porn has been found";
            }
        }
        finally {
            if(fis != null) {
                fis.close();
            }
        }
        
        if(!name.startsWith(STORAGE_PREFIX)) {
            name = STORAGE_PREFIX + name;
        }
        
        if(!name.endsWith(STORAGE_SUFFIX)) {
            name = name + STORAGE_SUFFIX;
        }
        
        return StorageManager.getInstance().createStorageAsset(name, url);
    }
    
    public List<PornItem> getPornItems() {
        return new ArrayList<PornItem>(this.items.values());
    }
    
    public PornItem getPornItem(int id) {
        return this.items.get(id);
    }
    
    public PornItem randomPornItem() {
        List<PornItem> items = this.getPornItems();
        if(items.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }

    @Override
    public void created(StorageAsset asset) {
        PornItem item = this.toPornItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        PornItem item = this.toPornItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            PornItem item = this.toPornItem(asset);
            if(item != null) {
                this.items.put(item.id, item);
            }
        }
    }
    
    private boolean accept(StorageAsset asset) {
        String name = asset.name;
        
        if(!name.startsWith(STORAGE_PREFIX)) {
            return false;
        }
        
        if(!name.endsWith(STORAGE_SUFFIX)) {
            return false;
        }
        
        return true;
    }
    
    private PornItem toPornItem(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        PornItem item = new PornItem();
        item.id = asset.id;
        String name = asset.name;
        name = name.substring(STORAGE_PREFIX.length(), name.length() - STORAGE_SUFFIX.length());
        item.name = name;
        
        return item;
    }
    
    public String getImageUrl(PornItem item) throws Exception {
        if(item == null) {
            return "";
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return "";
        }
        
        return StorageManager.getInstance().getImageUrl(asset);
    }
}
