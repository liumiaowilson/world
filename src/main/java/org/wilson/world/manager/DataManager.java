package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.cache.CacheProvider;
import org.wilson.world.dao.DAO;
import org.wilson.world.dao.DataItemDAO.DataItemQueryByNameTemplate;
import org.wilson.world.dao.QueryTemplate;
import org.wilson.world.model.DataItem;

public class DataManager implements CacheProvider {
    public static final String NAME = "data";
    
    private static DataManager instance;
    
    private Map<Integer, DataItem> cache;
    private Map<String, String> dataCache;
    
    private DAO<DataItem> dao = null;
    
    @SuppressWarnings("unchecked")
    private DataManager() {
        this.dao = DAOManager.getInstance().getDAO(DataItem.class);
        
        CacheManager.getInstance().registerCacheProvider(this);
    }
    
    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private Map<Integer, DataItem> getCache() {
        if(cache == null) {
            this.reloadCache();
        }
        return cache;
    }
    
    private Map<String, String> getDataCache() {
        if(dataCache == null) {
            this.reloadCache();
        }
        return dataCache;
    }
    
    public void createDataItem(DataItem item) {
        this.dao.create(item);

        this.getCache().put(item.id, item);
        this.getDataCache().put(item.name, item.value);
    }
    
    public void updateDataItem(DataItem item) {
        this.dao.update(item);

        getCache().put(item.id, item);
        getDataCache().put(item.name, item.value);
    }
    
    public DataItem getDataItemFromDB(int id) {
        return this.dao.get(id);
    }
    
    public DataItem getDataItemFromDBByName(String name) {
        QueryTemplate qt = this.dao.getQueryTemplate(DataItemQueryByNameTemplate.NAME);
        List<DataItem> items = this.dao.query(qt, name);
        if(items.isEmpty()) {
            return null;
        }
        else {
            return items.get(0);
        }
    }
    
    public DataItem getDataItem(int id) {
        DataItem item = getCache().get(id);
        if(item != null) {
            return item;
        }
        
        item = getDataItemFromDB(id);
        if(item != null) {
            getCache().put(item.id, item);
            getDataCache().put(item.name, item.value);
            return item;
        }
        else {
            return null;
        }
    }
    
    public List<DataItem> getDataItemsFromDB() {
        return this.dao.getAll();
    }
    
    public List<DataItem> getDataItems() {
        List<DataItem> result = new ArrayList<DataItem>();
        for(DataItem item : getCache().values()) {
            result.add(item);
        }
        Collections.sort(result, new Comparator<DataItem>(){

            @Override
            public int compare(DataItem o1, DataItem o2) {
                return o1.name.compareTo(o2.name);
            }
            
        });
        return result;
    }
    
    public void deleteDataItem(int id) {
        this.dao.delete(id);

        DataItem item = this.getCache().get(id);
        if(item != null) {
            getCache().remove(id);
            getDataCache().remove(item.name);
        }
    }
    
    public int getValueAsInt(String name) {
        String value = this.getValue(name);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        }
        catch(Exception e) {
        }
        return ret;
    }
    
    public String getValue(String name) {
        if(name == null) {
            return null;
        }
        String value = this.getDataCache().get(name);
        if(value != null) {
            return value;
        }
        else {
            DataItem item = this.getDataItemFromDBByName(name);
            if(item == null) {
                return null;
            }
            else {
                this.getCache().put(item.id, item);
                this.getDataCache().put(item.name, item.value);
                return item.value;
            }
        }
    }
    
    public void deleteValue(String name) {
        if(name == null) {
            return;
        }
        DataItem item = this.getCache().get(name);
        if(item != null) {
            this.deleteDataItem(item.id);
        }
        else {
            item = this.getDataItemFromDBByName(name);
            if(item == null) {
                return;
            }
            else {
                this.deleteDataItem(item.id);
            }
        }
    }
    
    public void setValue(String name, int value) {
        this.setValue(name, String.valueOf(value));
    }
    
    public void setValue(String name, String value) {
        DataItem item = this.getDataItemFromDBByName(name);
        if(item == null) {
            item = new DataItem();
            item.name = name;
            item.value = value;
            this.createDataItem(item);
        }
        else {
            item.value = value;
            this.updateDataItem(item);
        }
        this.getCache().put(item.id, item);
        this.getDataCache().put(item.name, item.value);
    }
    
    @Override
    public String getCacheProviderName() {
        return NAME;
    }

    @Override
    public boolean canPreload() {
        return true;
    }

    @Override
    public void reloadCache() {
        List<DataItem> items = getDataItemsFromDB();
        cache = new HashMap<Integer, DataItem>();
        dataCache = new HashMap<String, String>();
        for(DataItem item : items) {
            cache.put(item.id, item);
            dataCache.put(item.name, item.value);
        }
    }
}
