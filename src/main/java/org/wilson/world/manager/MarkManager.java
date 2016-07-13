package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.cache.Cache;
import org.wilson.world.cache.CacheListener;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.lifecycle.ManagerLifecycle;

public class MarkManager implements ManagerLifecycle{
    private static MarkManager instance;
    
    private Map<String, List<String>> markedMap = new HashMap<String, List<String>>();
    
    private MarkManager() {
    }
    
    public static MarkManager getInstance() {
        if(instance == null) {
            instance = new MarkManager();
        }
        return instance;
    }
    
    public void mark(Object target) {
        if(target == null) {
            return;
        }
        
        String type = ItemManager.getInstance().getItemTypeName(target);
        if(type == null) {
            return;
        }
        String id = ItemManager.getInstance().getItemID(target);
        if(id == null) {
            return;
        }
        
        mark(type, id);
    }
    
    public void mark(String type, String id) {
        if(type == null) {
            return;
        }
        if(id == null) {
            return;
        }
        
        List<String> list = markedMap.get(type);
        if(list == null) {
            list = new ArrayList<String>();
            markedMap.put(type, list);
        }
        if(!list.contains(id)) {
            list.add(id);
        }
    }
    
    public void unmark(Object target) {
        if(target == null) {
            return;
        }
        
        String type = ItemManager.getInstance().getItemTypeName(target);
        if(type == null) {
            return;
        }
        String id = ItemManager.getInstance().getItemID(target);
        if(id == null) {
            return;
        }
        
        unmark(type, id);
    }
    
    public void unmark(String type, String id) {
        if(type == null) {
            return;
        }
        if(id == null) {
            return;
        }
        
        List<String> list = markedMap.get(type);
        if(list == null) {
            return;
        }
        list.remove(id);
    }
    
    public boolean isMarked(Object target) {
        if(target == null) {
            return false;
        }
        
        String type = ItemManager.getInstance().getItemTypeName(target);
        if(type == null) {
            return false;
        }
        String id = ItemManager.getInstance().getItemID(target);
        if(id == null) {
            return false;
        }
        
        return isMarked(type, id);
    }
    
    public boolean isMarked(String type, String id) {
        if(!markedMap.containsKey(type)) {
            return false;
        }
        List<String> ids = markedMap.get(type);
        return ids != null && ids.contains(id);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getMarked(String type) {
        List<String> ret = markedMap.get(type);
        if(ret == null) {
            ret = Collections.EMPTY_LIST;
        }
        return ret;
    }
    
    public boolean hasMarked(String type) {
        return !this.getMarked(type).isEmpty();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void start() {
        List<CacheProvider> providers = CacheManager.getInstance().getCacheProviders();
        for(CacheProvider provider : providers) {
            if(provider instanceof Cache) {
                Cache cache = (Cache)provider;
                cache.addCacheListener(new CacheListener() {
                    @Override
                    public void cachePut(Object v) {
                    }

                    @Override
                    public void cacheDeleted(Object v) {
                        unmark(v);
                    }

                    @Override
                    public void cacheLoaded(List all) {
                    }

                    @Override
                    public void cacheLoading(List old) {
                        for(Object target : old) {
                            unmark(target);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void shutdown() {
    }
}
