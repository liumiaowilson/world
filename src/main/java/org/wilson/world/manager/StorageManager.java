package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.List;

import org.wilson.world.dao.DAO;
import org.wilson.world.item.ItemTypeProvider;
import org.wilson.world.model.Storage;
import org.wilson.world.search.Content;
import org.wilson.world.search.ContentProvider;

public class StorageManager implements ItemTypeProvider {
    public static final String NAME = "storage";
    
    private static StorageManager instance;
    
    private DAO<Storage> dao = null;
    
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
    
}
