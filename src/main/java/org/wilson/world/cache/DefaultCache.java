package org.wilson.world.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wilson.world.manager.CacheManager;

public class DefaultCache<K, V> implements Cache<K, V>, CacheProvider {
    private Map<K, V> map = null;
    private CacheLoader<K, V> loader = null;
    private String name;
    private boolean preload;
    private List<CacheListener<V>> listeners = new ArrayList<CacheListener<V>>();
    
    public DefaultCache(String name, boolean preload) {
        this.name = name;
        this.preload = preload;
    }
    
    private Map<K, V> getMap() {
        if(map == null) {
            this.reloadCache();
        }
        return map;
    }
    
    @Override
    public V get(K k) {
        return this.getMap().get(k);
    }

    @Override
    public List<V> getAll() {
        return new ArrayList<V>(this.getMap().values());
    }

    @Override
    public void put(K k, V v) {
        V old = this.getMap().get(k);
        this.getMap().put(k, v);
        for(CacheListener<V> listener : listeners) {
            listener.cachePut(old, v);
        }
    }

    @Override
    public void delete(K k) {
        V v = this.getMap().remove(k);
        for(CacheListener<V> listener : listeners) {
            listener.cacheDeleted(v);
        }
    }

    @Override
    public void init(CacheLoader<K, V> loader) {
        this.loader = loader;
        
        CacheManager.getInstance().registerCacheProvider(this);
    }

    @Override
    public String getCacheProviderName() {
        return this.name;
    }

    @Override
    public boolean canPreload() {
        return this.preload;
    }

    @Override
    public void reloadCache() {
        this.load();
    }

    @Override
    public void load() {
        List<V> old = null;
        if(this.map == null) {
            old = new ArrayList<V>();
        }
        else {
            old = new ArrayList<V>(this.map.values());
        }
        for(CacheListener<V> listener : listeners) {
            listener.cacheLoading(old);
        }
        
        map = new HashMap<K, V>();
        if(this.loader != null) {
            this.loader.load(this);
        }
        
        List<V> all = new ArrayList<V>(this.map.values());
        for(CacheListener<V> listener : listeners) {
            listener.cacheLoaded(all);
        }
    }

    @Override
    public void addCacheListener(CacheListener<V> listener) {
        if(listener == null) {
            return;
        }
        this.listeners.add(listener);
    }

    @Override
    public void removeCacheListener(CacheListener<V> listener) {
        if(listener == null) {
            return;
        }
        this.listeners.remove(listener);
    }

    @Override
    public void clear() {
        if(this.map != null) {
            this.map.clear();
        }
    }

    @Override
    public List<K> getKeys() {
        return new ArrayList<K>(this.map.keySet());
    }
}
