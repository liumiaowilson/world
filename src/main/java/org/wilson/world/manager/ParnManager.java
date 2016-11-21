package org.wilson.world.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.parn.ParnImageContributor;
import org.wilson.world.parn.ParnInfo;
import org.wilson.world.parn.ParnItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;

public class ParnManager implements StorageListener {
    public static final String IMAGE_PATH = "image.jpg";
    
    public static final String PARN = "parns";
    
    public static final String PARNS_REMOVED = "parns_removed";
    
    private String source;
    
    private static ParnManager instance;
    
    private static int GLOBAL_ID = 1;
    
    private Map<Integer, ParnInfo> infos = new HashMap<Integer, ParnInfo>();
    
    private Map<Integer, ParnItem> items = new HashMap<Integer, ParnItem>();
    
    public static final String STORAGE_PREFIX = "/parns/";
    public static final String STORAGE_SUFFIX = ".jpg";
    
    private ParnManager() {
        StorageManager.getInstance().addStorageListener(this);
        
        ImageManager.getInstance().addImageContributor(new ParnImageContributor());
    }
    
    public static ParnManager getInstance() {
        if(instance == null) {
            instance = new ParnManager();
        }
        return instance;
    }
    
    public ParnInfo randomParn() {
        String source = this.source;
        
        Map<String, List<ParnInfo>> parns = this.getParns();
        if(parns == null) {
            return null;
        }
        if(StringUtils.isBlank(source)) {
            List<String> froms = new ArrayList<String>();
            for(Entry<String, List<ParnInfo>> entry : parns.entrySet()) {
                String from = entry.getKey();
                List<ParnInfo> infos = entry.getValue();
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
        
        List<ParnInfo> infos = parns.get(source);
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public String getParnImagePath(ParnInfo info) {
        if(info == null) {
            return null;
        }
        
        return IMAGE_PATH;
    }
    
    public void downloadParn(ParnInfo info) throws IOException {
        if(info == null) {
            return;
        }
        
        if(!StringUtils.isBlank(info.url)) {
            DownloadManager.getInstance().download(info.url, ConfigManager.getInstance().getDataDir() + this.getParnImagePath(info));
        }
    }
    
    public void clearParnInfos(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<ParnInfo>> parns = this.getParns();
        if(parns != null) {
            List<ParnInfo> infos = parns.get(from);
            if(infos != null && !infos.isEmpty()) {
                for(ParnInfo info : infos) {
                    this.infos.remove(info.id);
                }
                
                infos.clear();
            }
        }
    }
    
    public void addParnInfo(ParnInfo info) {
        if(info == null) {
            return;
        }
        
        if(StringUtils.isBlank(info.from)) {
            return;
        }
        
        Map<String, ParnInfo> removed = this.getParnsRemoved();
        if(removed != null && removed.containsKey(info.url)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<ParnInfo>> parns = this.getParns();
        if(parns == null) {
            parns = new HashMap<String, List<ParnInfo>>();
            this.setParns(parns);
        }
        
        List<ParnInfo> infos = parns.get(info.from);
        if(infos == null) {
            infos = new ArrayList<ParnInfo>();
            parns.put(info.from, infos);
        }
        infos.add(info);
        
        this.infos.put(info.id, info);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<ParnInfo>> getParns() {
        return (Map<String, List<ParnInfo>>) WebManager.getInstance().get(PARN);
    }
    
    public void setParns(Map<String, List<ParnInfo>> parns) {
        WebManager.getInstance().put(PARN, parns);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, ParnInfo> getParnsRemoved() {
        return (Map<String, ParnInfo>) WebManager.getInstance().get(PARNS_REMOVED);
    }
    
    public void setParnsRemoved(Map<String, ParnInfo> parns) {
        WebManager.getInstance().put(PARNS_REMOVED, parns);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public ParnInfo getParnInfo(int id) {
        return this.infos.get(id);
    }
    
    public String saveParnInfo(ParnInfo info, String name) throws Exception {
        if(info == null) {
            return "Parn info should be provided";
        }
        
        if(StringUtils.isBlank(name)) {
            return "Name should be provided";
        }
        
        String url = info.url;
        if(StringUtils.isBlank(url)) {
            return "Url is invalid";
        }
        
        String path = ConfigManager.getInstance().getDataDir() + this.getParnImagePath(info);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            String checksum = IOUtils.getChecksum(fis);
            if(StorageManager.getInstance().hasDuplicate(checksum)) {
                return "Duplicate parn has been found";
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
    
    public List<ParnItem> getParnItems() {
        return new ArrayList<ParnItem>(this.items.values());
    }
    
    public ParnItem getParnItem(int id) {
        return this.items.get(id);
    }
    
    public ParnItem randomParnItem() {
        List<ParnItem> items = this.getParnItems();
        if(items.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }

    @Override
    public void created(StorageAsset asset) {
        ParnItem item = this.toParnItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        ParnItem item = this.toParnItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            ParnItem item = this.toParnItem(asset);
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
    
    private ParnItem toParnItem(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        ParnItem item = new ParnItem();
        item.id = asset.id;
        String name = asset.name;
        name = name.substring(STORAGE_PREFIX.length(), name.length() - STORAGE_SUFFIX.length());
        item.name = name;
        
        return item;
    }
    
    public String getImageUrl(ParnItem item) throws Exception {
        if(item == null) {
            return "";
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return "";
        }
        
        return StorageManager.getInstance().getImageUrl(asset);
    }
    
    public void removeParnInfo(ParnInfo info) {
        if(info != null) {
            Map<String, List<ParnInfo>> parns = this.getParns();
            List<ParnInfo> infos = parns.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
            
            Map<String, ParnInfo> removed = this.getParnsRemoved();
            if(removed == null) {
                removed = new HashMap<String, ParnInfo>();
                this.setParnsRemoved(removed);
            }
            removed.put(info.url, info);
        }
    }
}
