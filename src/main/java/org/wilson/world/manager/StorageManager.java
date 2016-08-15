package org.wilson.world.manager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Storage;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;
import org.wilson.world.storage.StorageAsset;
import org.wilson.world.web.WebJobMonitor;

public class StorageManager implements ItemTypeProvider {
    private static final Logger logger = Logger.getLogger(StorageManager.class);
    
    public static final String NAME = "storage";
    
    private static StorageManager instance;
    
    private DAO<Storage> dao = null;
    
    private Map<String, StorageAsset> assets = new HashMap<String, StorageAsset>();
    private Map<Integer, StorageAsset> ids = new HashMap<Integer, StorageAsset>();
    
    private static int GLOBAL_ID = 1;
    
    @SuppressWarnings("unchecked")
    private StorageManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(Storage.class);
        
        ItemManager.getInstance().registerItemTypeProvider(this);
        
        SearchManager.getInstance().registerContentProvider(new ContentProvider() {

            @Override
            public String getName() {
                return getItemTypeName();
            }

            @Override
            public List<Content> search(String text) {
                List<Content> ret = new ArrayList<Content>();
                
                for(Storage storage : getStorages()) {
                    boolean found = storage.name.contains(text) || storage.description.contains(text);
                    if(found) {
                        Content content = new Content();
                        content.id = storage.id;
                        content.name = storage.name;
                        content.description = storage.description;
                        ret.add(content);
                    }
                }
                
                return ret;
            }
            
        });
    }
    
    public static StorageManager getInstance() {
        if(instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }
    
    public void createStorage(Storage storage) {
        ItemManager.getInstance().checkDuplicate(storage);
        
        this.dao.create(storage);
    }
    
    public Storage getStorage(int id) {
        Storage storage = this.dao.get(id);
        if(storage != null) {
            return storage;
        }
        else {
            return null;
        }
    }
    
    public List<Storage> getStorages() {
        List<Storage> result = new ArrayList<Storage>();
        for(Storage storage : this.dao.getAll()) {
            result.add(storage);
        }
        return result;
    }
    
    public void updateStorage(Storage storage) {
        this.dao.update(storage);
    }
    
    public void deleteStorage(int id) {
        this.dao.delete(id);
    }

    @Override
    public String getItemTableName() {
        return this.dao.getItemTableName();
    }

    @Override
    public String getItemTypeName() {
        return NAME;
    }

    @Override
    public boolean accept(Object target) {
        return target instanceof Storage;
    }

    @Override
    public String getID(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Storage storage = (Storage)target;
        return String.valueOf(storage.id);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public DAO getDAO() {
        return this.dao;
    }
    
    @Override
    public String getIdentifier(Object target) {
        if(!accept(target)) {
            return null;
        }
        
        Storage storage = (Storage)target;
        return storage.name;
    }
    
    public void addStorageAsset(StorageAsset asset) {
        if(asset == null) {
            return;
        }
        this.assets.put(asset.name, asset);
        this.ids.put(asset.id, asset);
    }
    
    public void removeStorageAsset(StorageAsset asset) {
        if(asset == null) {
            return;
        }
        this.assets.remove(asset.name);
        this.ids.remove(asset.id);
    }
    
    public void sync() throws Exception {
        this.sync(null);
    }
    
    public void sync(WebJobMonitor monitor) throws Exception {
        if(monitor != null) {
            monitor.start(this.getStorages().size());
        }
        
        GLOBAL_ID = 1;
        this.assets.clear();
        
        for(Storage storage : this.getStorages()) {
            String resp = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + storage.key + "&command=list");
            if(!StringUtils.isBlank(resp)) {
                resp = resp.trim();
                if(resp.startsWith("[ERROR]")) {
                    logger.warn(resp);
                    continue;
                }
                
                String [] lines = resp.split("\n");
                for(String line : lines) {
                    if(!StringUtils.isBlank(line)) {
                        StorageAsset asset = new StorageAsset();
                        asset.id = GLOBAL_ID++;
                        asset.name = line;
                        asset.storageId = storage.id;
                        
                        this.addStorageAsset(asset);
                    }
                }
            }
            
            if(monitor != null) {
                if(monitor.isStopRequired()) {
                    monitor.stop();
                    return;
                }
                
                monitor.progress(1);
            }
        }
    }
    
    public List<StorageAsset> getStorageAssets() {
        return new ArrayList<StorageAsset>(this.assets.values());
    }
    
    public StorageAsset getStorageAsset(int id) {
        return this.ids.get(id);
    }
    
    public StorageAsset getStorageAsset(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        return this.assets.get(name);
    }
    
    public Storage randomStorage() {
        List<Storage> storages = this.getStorages();
        if(storages.isEmpty()) {
            return null;
        }
        
        int n = DiceManager.getInstance().random(storages.size());
        return storages.get(n);
    }
    
    public String createStorageAsset(String name, String url) throws Exception{
        if(StringUtils.isBlank(name)) {
            return "Storage asset name should be provided";
        }
        if(StringUtils.isBlank(url)) {
            return "Storage asset url should be provided";
        }
        
        StorageAsset asset = this.getStorageAsset(name);
        if(asset != null) {
            return "Storage asset with the same name already exists";
        }
        
        url = URLEncoder.encode(url, "UTF-8");
        Storage storage = this.randomStorage();
        if(storage == null) {
            return "No storage could be found";
        }
        
        String resp = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + storage.key + "&command=create&path=" + name + "&url=" + url);
        
        if(!StringUtils.isBlank(resp) && resp.trim().startsWith("[ERROR]")) {
            return resp;
        }
        
        asset = new StorageAsset();
        asset.id = GLOBAL_ID++;
        asset.name = name;
        asset.storageId = storage.id;
        this.addStorageAsset(asset);
        
        return null;
    }
    
    public String deleteStorageAsset(String name) throws Exception {
        if(StringUtils.isBlank(name)) {
            return "Storage asset name should be provided";
        }
        
        StorageAsset asset = this.getStorageAsset(name);
        if(asset == null) {
            return "No such storage asset could be found";
        }
        
        Storage storage = this.getStorage(asset.storageId);
        
        String resp = WebManager.getInstance().getContent(storage.url + "/servlet/file?key=" + storage.key + "&command=delete&path=" + name);
        if(!StringUtils.isBlank(resp) && resp.trim().startsWith("[ERROR]")) {
            return resp;
        }
        
        this.removeStorageAsset(asset);
        
        return null;
    }
}
