package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CachedDAO;
import org.wilson.world.cache.DefaultCache;
import org.wilson.world.dao.DAO;
import org.wilson.world.dao.DataItemDAO.DataItemQueryByNameTemplate;
import org.wilson.world.dao.QueryTemplate;
import org.wilson.world.model.DataItem;

public class DataManager {
    private static DataManager instance;
    
    private Cache<String, String> dataCache;
    
    private DAO<DataItem> dao = null;
    
    @SuppressWarnings("unchecked")
    private DataManager() {
        this.dao = DAOManager.getInstance().getCachedDAO(DataItem.class);
        
        this.dataCache = new DefaultCache<String, String>("data_item_data_cache", false);
        if(this.dao instanceof CachedDAO) {
            Cache<Integer, DataItem> cache = ((CachedDAO<DataItem>)this.dao).getCache();
            cache.addCacheListener(new CacheListener<DataItem>(){
                @Override
                public void cachePut(DataItem old, DataItem v) {
                    if(old != null) {
                        DataManager.this.dataCache.delete(old.name);
                    }
                    DataManager.this.dataCache.put(v.name, v.value);
                }

                @Override
                public void cacheDeleted(DataItem v) {
                    DataManager.this.dataCache.delete(v.name);
                }

                @Override
                public void cacheLoaded(List<DataItem> all) {
                }

                @Override
                public void cacheLoading(List<DataItem> old) {
                    DataManager.this.dataCache.clear();
                }
            });
        }
    }
    
    public static DataManager getInstance() {
        if(instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    public void reload() {
        ((CachedDAO<DataItem>)this.dao).getCache().load();
    }
    
    public void addCacheListener(CacheListener<DataItem> listener) {
        ((CachedDAO<DataItem>)this.dao).getCache().addCacheListener(listener);
    }
    
    public void createDataItem(DataItem item) {
        this.dao.create(item);
    }
    
    public void updateDataItem(DataItem item) {
        this.dao.update(item);
    }
    
    public DataItem getDataItemFromDBByName(String name) {
        QueryTemplate<DataItem> qt = this.dao.getQueryTemplate(DataItemQueryByNameTemplate.NAME);
        List<DataItem> items = this.dao.query(qt, name);
        if(items.isEmpty()) {
            return null;
        }
        else {
            return items.get(0);
        }
    }
    
    public DataItem getDataItem(int id) {
        DataItem item = this.dao.get(id);
        if(item != null) {
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
        for(DataItem item : this.dao.getAll()) {
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
    
    public boolean getValueAsBoolean(String name) {
        String value = this.getValue(name);
        boolean ret = false;
        try {
            ret = "true".equals(value);
        }
        catch(Exception e) {
        }
        return ret;
    }
    
    public long getValueAsLong(String name) {
        String value = this.getValue(name);
        long ret = -1L;
        try {
            ret = Long.parseLong(value);
        }
        catch(Exception e) {
        }
        return ret;
    }
    
    public String getValue(String name) {
        if(name == null) {
            return null;
        }
        String value = this.dataCache.get(name);
        if(value != null) {
            return value;
        }
        else {
            DataItem item = this.getDataItemFromDBByName(name);
            if(item == null) {
                return null;
            }
            else {
                return item.value;
            }
        }
    }
    
    public DataItem getDataItem(String name) {
        if(StringUtils.isBlank(name)) {
            return null;
        }
        
        for(DataItem item : this.dao.getAll()) {
            if(name.equals(item.name)) {
                return item;
            }
        }
        return null;
    }
    
    public void deleteValue(String name) {
        if(name == null) {
            return;
        }
        DataItem item = this.getDataItem(name);
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
    
    public void setValue(String name, boolean value) {
        this.setValue(name, String.valueOf(value));
    }
    
    public void setValue(String name, long value) {
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
    }
}
