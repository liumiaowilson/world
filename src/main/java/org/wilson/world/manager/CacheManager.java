package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.cache.CacheProvider;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;

public class CacheManager implements EventListener{
    private static CacheManager instance;
    
    private Map<String, CacheProvider> providers = new HashMap<String, CacheProvider>();
    
    private CacheManager() {
        EventManager.getInstance().registerListener(EventType.ClearTable, this);
    }
    
    public static CacheManager getInstance() {
        if(instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }
    
    public void registerCacheProvider(CacheProvider provider) {
        if(provider != null) {
            if(!this.providers.containsKey(provider.getCacheProviderName())) {
                this.providers.put(provider.getCacheProviderName(), provider);
            }
        }
    }
    
    public void unregisterCacheProvider(CacheProvider provider) {
        if(provider != null) {
            this.providers.remove(provider.getCacheProviderName());
        }
    }
    
    public List<String> getCacheNames() {
        List<String> ret = new ArrayList<String>();
        ret.addAll(providers.keySet());
        return ret;
    }
    
    public void reloadCache(String name) {
        if(name == null) {
            return;
        }
        CacheProvider provider = this.providers.get(name);
        if(provider != null) {
            provider.reloadCache();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(Event event) {
        List<String> names = (List<String>) event.data.get("names");
        if(names == null || names.isEmpty()) {
            return;
        }
        for(String name : names) {
            this.reloadCache(name);
        }
    }
}
