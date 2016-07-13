package org.wilson.world.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wilson.world.cache.CacheProvider;
import org.wilson.world.event.Event;
import org.wilson.world.event.EventListener;
import org.wilson.world.event.EventType;

public class CacheManager implements EventListener{
    private static final Logger logger = Logger.getLogger(CacheManager.class);
    
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
        Collections.sort(ret);
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
    
    public void doPreload() {
        for(CacheProvider provider : providers.values()) {
            if(provider.canPreload()) {
                logger.info("Start preloading [" + provider.getCacheProviderName() + "]");
                provider.reloadCache();
                logger.info("Cache [" + provider.getCacheProviderName() + "] preloaded.");
            }
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
