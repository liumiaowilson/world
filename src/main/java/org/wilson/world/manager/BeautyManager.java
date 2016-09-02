package org.wilson.world.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.beauty.BeautyInfo;
import org.wilson.world.beauty.BeautyItem;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.storage.StorageListener;
import org.wilson.world.util.IOUtils;

public class BeautyManager implements StorageListener {
    public static final String IMAGE_PATH = "image.jpg";
    
    public static final String BEAUTIES = "beauties";
    
    private String source;
    
    private static BeautyManager instance;
    
    private static int GLOBAL_ID = 1;
    
    private Map<Integer, BeautyInfo> infos = new HashMap<Integer, BeautyInfo>();
    
    private Map<Integer, BeautyItem> items = new HashMap<Integer, BeautyItem>();
    
    public static final String STORAGE_PREFIX = "/beauties/";
    public static final String STORAGE_SUFFIX = ".jpg";
    
    private BeautyManager() {
        StorageManager.getInstance().addStorageListener(this);
    }
    
    public static BeautyManager getInstance() {
        if(instance == null) {
            instance = new BeautyManager();
        }
        return instance;
    }
    
    public BeautyInfo randomBeauty() {
        String source = this.source;
        
        Map<String, List<BeautyInfo>> beauties = this.getBeauties();
        if(beauties == null) {
            return null;
        }
        if(StringUtils.isBlank(source)) {
            List<String> froms = new ArrayList<String>();
            for(Entry<String, List<BeautyInfo>> entry : beauties.entrySet()) {
                String from = entry.getKey();
                List<BeautyInfo> infos = entry.getValue();
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
        
        List<BeautyInfo> infos = beauties.get(source);
        
        int n = DiceManager.getInstance().random(infos.size());
        return infos.get(n);
    }
    
    public String getBeautyImagePath(BeautyInfo info) {
        if(info == null) {
            return null;
        }
        
        return IMAGE_PATH;
    }
    
    public void downloadBeauty(BeautyInfo info) throws IOException {
        if(info == null) {
            return;
        }
        
        if(!StringUtils.isBlank(info.url)) {
            DownloadManager.getInstance().download(info.url, ConfigManager.getInstance().getDataDir() + this.getBeautyImagePath(info));
        }
    }
    
    public void clearBeautyInfos(String from) {
        if(StringUtils.isBlank(from)) {
            return;
        }
        
        Map<String, List<BeautyInfo>> beauties = this.getBeauties();
        if(beauties != null) {
            List<BeautyInfo> infos = beauties.get(from);
            if(infos != null && !infos.isEmpty()) {
                for(BeautyInfo info : infos) {
                    this.infos.remove(info.id);
                }
                
                infos.clear();
            }
        }
    }
    
    public void addBeautyInfo(BeautyInfo info) {
        if(info == null) {
            return;
        }
        
        if(StringUtils.isBlank(info.from)) {
            return;
        }
        
        if(info.id == 0) {
            info.id = GLOBAL_ID++;
        }
        
        Map<String, List<BeautyInfo>> beauties = this.getBeauties();
        if(beauties == null) {
            beauties = new HashMap<String, List<BeautyInfo>>();
            this.setBeauties(beauties);
        }
        
        List<BeautyInfo> infos = beauties.get(info.from);
        if(infos == null) {
            infos = new ArrayList<BeautyInfo>();
            beauties.put(info.from, infos);
        }
        infos.add(info);
        
        this.infos.put(info.id, info);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, List<BeautyInfo>> getBeauties() {
        return (Map<String, List<BeautyInfo>>) WebManager.getInstance().get(BEAUTIES);
    }
    
    public void setBeauties(Map<String, List<BeautyInfo>> beauties) {
        WebManager.getInstance().put(BEAUTIES, beauties);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public BeautyInfo getBeautyInfo(int id) {
        return this.infos.get(id);
    }
    
    public String saveBeautyInfo(BeautyInfo info, String name) throws Exception {
        if(info == null) {
            return "Beauty info should be provided";
        }
        
        if(StringUtils.isBlank(name)) {
            return "Name should be provided";
        }
        
        String url = info.url;
        if(StringUtils.isBlank(url)) {
            return "Url is invalid";
        }
        
        String path = ConfigManager.getInstance().getDataDir() + this.getBeautyImagePath(info);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            String checksum = IOUtils.getChecksum(fis);
            if(StorageManager.getInstance().hasDuplicate(checksum)) {
                return "Duplicate beauty has been found";
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
    
    public List<BeautyItem> getBeautyItems() {
        return new ArrayList<BeautyItem>(this.items.values());
    }
    
    public BeautyItem getBeautyItem(int id) {
        return this.items.get(id);
    }
    
    public BeautyItem randomBeautyItem() {
        List<BeautyItem> items = this.getBeautyItems();
        if(items.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(items.size());
        return items.get(n);
    }

    @Override
    public void created(StorageAsset asset) {
        BeautyItem item = this.toBeautyItem(asset);
        if(item != null) {
            this.items.put(item.id, item);
        }
    }

    @Override
    public void deleted(StorageAsset asset) {
        BeautyItem item = this.toBeautyItem(asset);
        if(item != null) {
            this.items.remove(item.id);
        }
    }

    @Override
    public void reloaded(List<StorageAsset> assets) {
        this.items.clear();
        
        for(StorageAsset asset : assets) {
            BeautyItem item = this.toBeautyItem(asset);
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
    
    private BeautyItem toBeautyItem(StorageAsset asset) {
        if(!accept(asset)) {
            return null;
        }
        
        BeautyItem item = new BeautyItem();
        item.id = asset.id;
        String name = asset.name;
        name = name.substring(STORAGE_PREFIX.length(), name.length() - STORAGE_SUFFIX.length());
        item.name = name;
        
        return item;
    }
    
    public String getImageUrl(BeautyItem item) throws Exception {
        if(item == null) {
            return "";
        }
        
        StorageAsset asset = StorageManager.getInstance().getStorageAsset(item.id);
        if(asset == null) {
            return "";
        }
        
        return StorageManager.getInstance().getImageUrl(asset);
    }
    
    public void removeBeautyInfo(BeautyInfo info) {
        if(info != null) {
            Map<String, List<BeautyInfo>> beauties = this.getBeauties();
            List<BeautyInfo> infos = beauties.get(info.from);
            if(infos != null) {
                infos.remove(info);
            }
        }
    }
}
